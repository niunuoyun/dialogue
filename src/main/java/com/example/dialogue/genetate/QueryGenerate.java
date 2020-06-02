package com.example.dialogue.genetate;

import com.example.dialogue.constant.PhraseTypeClassify;
import com.example.dialogue.entity.Phrase;
import com.example.dialogue.utils.StringUtil;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Huihua Niu
 * on 2020/1/16 11:21
 */
@Component
public class QueryGenerate {
/*    public String CombineQuery(List<Phrase> currentResult, List<Phrase> lastResult) {
        List<Phrase> LastStandardWord = cleanData(lastResult, 0);
        currentResult = fixData(currentResult);
        if (currentResult.size() == 0) return null;
        Phrase currentFirst = currentResult.get(0);
        String combineQuery;
        // 牡丹花的花期   开花时间呢   主语一般是问**呢或**的呢 谓语一般会带上询问语句 开花时间是什么时候
        if (currentFirst.getTypeSet().contains("r") || currentFirst.getTypeSet().contains("rr") || currentFirst.getTypeSet().contains("predicate") || currentResult.get(currentResult.size() - 1).getTypeSet().contains("ask")) {
            combineQuery = predicateStart(currentResult, LastStandardWord);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        combineQuery = getCombineResult(currentResult, LastStandardWord);
        return combineQuery;
    }*/

    public String CombineQuery(List<Phrase> currentResult, List<Phrase> lastResult, String entityType) {
        List<Phrase> LastStandardWord = cleanData(lastResult, 0);
        currentResult = fixData(currentResult,entityType);
        if (currentResult.size() == 0) return null;
        Phrase currentFirst = currentResult.get(0);
        int entityIndex = -1;
        for (int i = 0; i < lastResult.size(); i++) {
            if (typeIsIncluded(PhraseTypeClassify.entityType, lastResult.get(i).getTypeSet())) {
                entityIndex = i;
                break;
            }
        }
        String combineQuery;
        // 牡丹花的花期   开花时间呢   主语一般是问**呢或**的呢 谓语一般会带上询问语句 开花时间是什么时候
        if (currentFirst.getTypeSet().contains("vn") || currentFirst.getTypeSet().contains("v") || currentFirst.getTypeSet().contains("tri_pronoun") || currentFirst.getTypeSet().contains(entityType+"_pred") || currentResult.get(currentResult.size() - 1).getTypeSet().contains("ask")) {
            combineQuery = predicateHandler(currentResult, LastStandardWord, entityIndex,entityType);
            if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        }
        combineQuery = getCombineResult(currentResult, LastStandardWord);
        if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        combineQuery = singleFactor(currentResult, lastResult, entityType);
        if (!StringUtils.isEmpty(combineQuery)) return combineQuery;
        combineQuery = pronounHandler(currentResult, lastResult.get(entityIndex));
        return combineQuery;
    }

    //缺失主语的情况下的处理方式，看一下是否有谓语，是否有询问词，如果两个都有，直接把主语加上
    private static String singleFactor(List<Phrase> currentResult, List<Phrase> lastResult, String entityType) {
        Phrase subject = null, predicate = null, ask = null;
        for (int i = 0; i < currentResult.size(); i++) {
            if (currentResult.get(i).getTypeSet().contains("entityType")) {
                subject = currentResult.get(i);
                continue;
            }
            if (currentResult.get(i).getTypeSet().contains(entityType+"_pred")) {
                predicate = currentResult.get(i);
                continue;
            }
            if (currentResult.get(i).getTypeSet().contains("ask")) {
                ask = currentResult.get(i);
                continue;
            }
        }
        //主谓都不缺的情况下，直接返回，不做处理
        if (Objects.nonNull(subject) && Objects.nonNull(predicate) && Objects.nonNull(ask)) return null;
        StringBuilder queryNew = new StringBuilder();
        //缺失主语带有询问词的情况下，将上一轮主语拿过来使用
        if (Objects.isNull(subject) && Objects.nonNull(predicate) && Objects.nonNull(ask)) {
            for (int i = 0; i < lastResult.size(); i++) {
                if (lastResult.get(i).getTypeSet().contains(entityType) || lastResult.get(i).getTypeSet().contains("and")) {
                    queryNew.append(lastResult.get(i).getValue());
                    continue;
                }
            }
            if (StringUtils.isEmpty(queryNew)) return null;
            currentResult.forEach(val -> queryNew.append(val.getValue()));
            return queryNew.toString();
        }
        //缺失主语不带询问词的情况下，将上一轮主语拿过来使用
        if (Objects.isNull(subject) && Objects.nonNull(predicate) && Objects.isNull(ask)) {
            for (int i = 0; i < lastResult.size(); i++) {
                if (lastResult.get(i).getTypeSet().contains(entityType+"_pred")) {
                    queryNew.append(predicate.getValue());
                    continue;
                }
                queryNew.append(lastResult.get(i).getValue());
            }
        }
        //缺失谓语的情况下降上一轮的谓语
        if (Objects.nonNull(subject) && Objects.isNull(predicate) && Objects.isNull(ask)) {
            for (int i = 0; i < lastResult.size(); i++) {
                if (lastResult.get(i).getTypeSet().contains(entityType)) {
                    queryNew.append(subject.getValue());
                    continue;
                }
                queryNew.append(lastResult.get(i).getValue());
            }
        }
        return null;
    }

    private static String pronounHandler(List<Phrase> currentResult, Phrase entity) {
        StringBuilder queryNew = new StringBuilder();
        if (currentResult.size() >= 4) {
            boolean hasAks = false;
            for (int i = 0; i < currentResult.size(); i++) {
                if (currentResult.get(i).getTypeSet().contains("tri_pronoun")) {
                    queryNew.append(entity.getValue());
                } else {
                    queryNew.append(currentResult.get(i).getValue());
                }
                if (currentResult.get(i).getTypeSet().contains("ask")) hasAks = true;
            }
           // if (hasAks) return queryNew.toString();

        }
        return queryNew.toString();
    }

    /**
     * 清洗第一轮对话的数据
     *
     * @param phrases
     * @return
     */
    public static List<Phrase> cleanData(List<Phrase> phrases, int fromIndex) {
        if (phrases == null || phrases.size() == 0) return phrases;
        List<Phrase> newPhrase = new ArrayList<>();
        boolean subjectHas = false;
        //找到连词的位置
        for (int i = fromIndex; i < phrases.size(); i++) {
            Phrase phrase = phrases.get(i);
            if (!subjectHas) {
                if (typeIsIncluded(PhraseTypeClassify.entityType, phrases.get(i).getTypeSet()) || phrases.get(i).getTypeSet().contains("r")) {
                    phrase.getTypeSet().remove("predicate");
                    newPhrase.add(phrase);
                    subjectHas = true;
                } else {
                    newPhrase.add(phrase);
                    continue;
                }
            } else {
                if (i + 1 < phrases.size()) {
                    if ((phrase.getTypeSet().contains("n") && phrases.get(i + 1).getTypeSet().contains("m"))
                            || (phrase.getTypeSet().contains("m") && phrases.get(i + 1).getTypeSet().contains("a"))
                            || ((phrase.getTypeSet().contains("a") || phrase.getTypeSet().contains("predicate")) && (typeIsIncluded(PhraseTypeClassify.entityType, phrases.get(i + 1).getTypeSet()) || phrases.get(i + 1).getTypeSet().contains("predicate")))) {
                        phrase.setValue(phrase.getValue() + phrases.get(i + 1).getValue());
                        Set<String> type = new HashSet<>();
                        type.add("predicate");
                        phrase.setTypeSet(type);
                        phrase.setFrequence(500);
                        i = i + 1;
                    } else {
                        if (phrase.getTypeSet().contains("a")) {
                            phrase.getTypeSet().remove("predicate");
                        }
                    }
                }
                newPhrase.add(phrase);
            }
        }
        return newPhrase;
    }

    /**
     * @param phrases
     * @return
     */
    public static List<Phrase> fixData(List<Phrase> phrases,String entityType) {
        if (phrases == null || phrases.size() == 0) return phrases;
        List<Phrase> newPhrase = new ArrayList<>();
        if (typeIsIncluded(PhraseTypeClassify.entityType, phrases.get(0).getTypeSet())) return phrases;
        if (phrases.size() == 2) {
            if ((phrases.get(0).getTypeSet().contains("n") && phrases.get(1).getTypeSet().contains("m"))
                    || (phrases.get(0).getTypeSet().contains("m") && phrases.get(1).getTypeSet().contains("a"))
                    || ((phrases.get(0).getTypeSet().contains("a") || phrases.get(0).getTypeSet().contains(entityType+"_pred")) && (typeIsIncluded(PhraseTypeClassify.entityType, phrases.get(1).getTypeSet()) || phrases.get(1).getTypeSet().contains("pred")))) {
                Phrase phrase = new Phrase();
                phrase.setValue(phrases.get(0).getValue() + phrases.get(1).getValue());
                Set<String> type = new HashSet<>();
                type.add(entityType+"_pred");
                phrase.setTypeSet(type);
                phrase.setFrequence(500);
                newPhrase.add(phrase);
                return newPhrase;
            }
        } else if (phrases.size() == 1) {
            if (phrases.get(0).getTypeSet().contains("a")) {
                phrases.get(0).getTypeSet().remove(entityType+"_pred");
                newPhrase.add(phrases.get(0));
                return newPhrase;
            }
        }
        return phrases;
    }


    public boolean hasSameTypeSet(Set<String> set1, Set<String> set2) {

        if (set1 == null && set2 == null) {
            return true;
        }
        if (set1 == null || set2 == null) {
            return false;
        }
        if (set1.size() == 0 && set2.size() == 0) {
            return true;
        }
        List<String> intersection = set1.stream().filter(item -> set2.contains(item)).collect(Collectors.toList());

        if ((intersection.size() == set1.size() && set1.size() == set2.size()) || intersection.size() >= 2) {
            return true;
        }
        intersection.remove("n");
        intersection.remove("a");
        if (intersection.size() >= 1) return true;
        return false;
    }

    /**
     * 实体类型匹配
     *
     * @param current
     * @param last
     * @return
     */
    public String getCombineResult(List<Phrase> current, List<Phrase> last) {
        List<Integer> index = new ArrayList<>();
        int currentIndex = -1;
        //找出上一轮词性和当前词性起始位置相同的地方
        for (int j = 0; j < current.size(); j++) {
            for (int i = 0; i < last.size(); i++) {
                if (hasSameTypeSet(current.get(j).getTypeSet(), last.get(i).getTypeSet())) {
                    index.add(i);
                }
            }
            if (index.size() > 0) {
                currentIndex = j;
                break;
            }
        }
        //如果从第二个开始就相同
        if (currentIndex == 1) {
            current.get(currentIndex).setValue(current.get(0).getValue() + current.get(1).getValue());
            current.remove(0);
        }
        if (index.size() == 0) return null;
        boolean isMatch = true;
        int hitIndex = -1;
        for (int i = 0; i < index.size(); i++) {
            hitIndex = index.get(i);
            if (hitIndex + current.size() > last.size()) {
                isMatch = false;
                break;
            }
            for (int j = hitIndex; j < hitIndex + current.size(); j++) {
                if (!hasSameTypeSet(current.get(j - hitIndex).getTypeSet(), last.get(j).getTypeSet())) {
                    isMatch = false;
                    break;
                } else {
                    isMatch = true;
                }
            }
            if (isMatch) break;
        }
        if (isMatch) {
            StringBuilder sb = new StringBuilder();
            if (last.size() >= 2) {
                Phrase lastPhrase = last.get(last.size() - 1);
                Phrase penultimatePhrase = last.get(last.size() - 2);
                if (lastPhrase.getTypeSet().contains("num") && penultimatePhrase.getTypeSet().contains("n")) {
                    penultimatePhrase.setValue(lastPhrase.getValue() + penultimatePhrase.getValue());
                    penultimatePhrase.getTypeSet().add("a");
                    last.remove(lastPhrase);
                } else if (lastPhrase.getTypeSet().contains("n") && penultimatePhrase.getTypeSet().contains("num")) {
                    penultimatePhrase.setValue(lastPhrase.getValue() + penultimatePhrase.getValue());
                    last.remove(lastPhrase);
                }
            }
            for (int i = 0; i < last.size(); i++) {
                if (i >= hitIndex && i < hitIndex + current.size() && i >= 0) {
                    sb.append(current.get(i - hitIndex).getValue());
                } else {
                    sb.append(last.get(i).getValue());
                }
            }
            if (sb.length() > 0) return sb.toString();
        }
        return null;
    }

    /**
     * 以谓语开头的,或者以代词开头的，这是缺失主语的情况,以人称代词开头，如果没有谓语，动词做谓语如果还是没有谓语，就根据同类词替换
     *
     * @param current
     * @param last
     * @return
     */
    // todo 需要修改
    public String predicateHandler(List<Phrase> current, List<Phrase> last, int entityIndex,String entityType) {
        if (current == null || current.size() == 0 || last == null || last.size() == 0) return null;
        StringBuilder queryNew = new StringBuilder();
        //第三人称代词的处理，
        List<Integer> preds = new ArrayList<>();
        // 是否有谓语词或者
        int hasAsk = -1, hasVorVn = -1, hasNocr = -1, pronoun = -1;
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).getTypeSet().contains(entityType+"_pred")) {
                preds.add(i);
                continue;
            }
            if (current.get(i).getTypeSet().contains("ask")) {
                hasAsk = i;
                continue;
            }
            if (current.get(i).getTypeSet().contains("nocr")) {
                hasNocr = i;
                continue;
            }
            //判断是否有第三人称代词
            if (current.get(i).getTypeSet().contains("tri_pronoun")) {
                pronoun = i;
                continue;
            }
            if (current.get(i).getTypeSet().contains("v") || current.get(i).getTypeSet().contains("vn")) {
                hasVorVn = i;
            }
        }
        // 根据谓语词进行拼接
        if ((preds.size() == 1 && hasAsk >= 0) || (preds.size() == 0 && hasAsk >= 0 && hasVorVn >= 0)) {
            if (pronoun>=0) current.set(pronoun,last.get(entityIndex));
            for (int i = 0; i < last.size(); i++) {
                if (last.get(i).getTypeSet().contains(entityType+"_pred") || last.get(i).getTypeSet().contains("v") || last.get(i).getTypeSet().contains("vn"))
                    break;
                queryNew.append(last.get(i).getValue());
            }
            if (!StringUtils.isEmpty(queryNew)) {
                int j = -1;
                if (preds.size() == 1) j = preds.get(0);
                else if (hasVorVn >= 0) j = hasVorVn;
                for (; j < current.size(); j++) {
                    queryNew.append(current.get(j).getValue());
                }
                return queryNew.toString();
            }
        } else if (preds.size() == 1 && hasAsk == -1) {
            for (int i = 0; i < last.size(); i++) {
                if (last.get(i).getTypeSet().contains(entityType+"_pred")) {
                    queryNew.append(current.get(preds.get(0)).getValue());
                    continue;
                }
                queryNew.append(last.get(i).getValue());
            }
            return queryNew.toString();
        } else if (preds.size() == 0 && hasVorVn == -1 && hasNocr >= 0 && current.size() <= 4) {
            if (hasAsk == -1) {
                boolean lastHasNocr = false;
                for (int j = entityIndex; j < last.size(); j++) {
                    if (last.get(j).getTypeSet().contains("nocr")) {
                        last.set(j, current.get(hasNocr));
                        lastHasNocr = true;
                        break;
                    }
                }
                if (!lastHasNocr) {
                    last.add(entityIndex + 1, current.get(hasNocr));
                }
                last.forEach(val -> queryNew.append(val.getValue()));
                return queryNew.toString();
            } else {
                queryNew.append(last.get(hasNocr).getValue());
                for (int x = hasNocr; x < current.size(); x++) {
                    queryNew.append(current.get(x).getValue());
                }
                return queryNew.toString();
            }

        }
        if (pronoun >= 0 && hasVorVn >= 0 && current.size() >= 4 && hasAsk == -1) {
            current.set(pronoun, last.get(entityIndex));
            current.forEach(val -> queryNew.append(val.getValue()));
            return queryNew.toString();
        }
        return null;
    }


    /**
     * 以谓语开头的,或者以代词开头的，这是确实主语的情况
     *
     * @param current
     * @param last
     * @return
     */
    public String predicateStart(List<Phrase> current, List<Phrase> last,String entityType) {
        if (current == null || current.size() == 0 || last == null || last.size() == 0) return null;
        int subjectIndex = -1, predicateIndex = -1;
        for (int i = 0; i < last.size(); i++) {
            if (typeIsIncluded(PhraseTypeClassify.entityType, last.get(i).getTypeSet())) {
                subjectIndex = i;
                break;
            }
        }
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).getTypeSet().contains("nocr") || current.get(i).getTypeSet().contains("v") || current.get(i).getTypeSet().contains("vn") || current.get(i).getTypeSet().contains("a") || current.get(i).getTypeSet().contains(entityType+"_pred")) {
                predicateIndex = i;
                break;
            }
        }
        if (subjectIndex >= 0 && predicateIndex >= 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= subjectIndex; i++) {
                sb.append(last.get(i).getValue());
            }
            if (subjectIndex < last.size() && last.get(subjectIndex + 1).getTypeSet().contains("u")) {
                sb.append("的");
            }
            boolean hasAsk = false;
            for (int i = predicateIndex; i < current.size(); i++) {
                sb.append(current.get(i).getValue());
                if (current.get(i).getTypeSet().contains("ask")) hasAsk = true;
            }
            if (current.get(current.size() - 1).getTypeSet().contains("ask")) return sb.toString();
            if (!hasAsk && typeIsIncluded(PhraseTypeClassify.endEntityType, last.get(last.size() - 1).getTypeSet()))
                sb.append("的" + last.get(last.size() - 1).getValue()).toString();
            if (!hasAsk && last.get(last.size() - 1).getTypeSet().contains("ask"))
                sb.append(last.get(last.size() - 1).getValue()).toString();
            return sb.toString();
        }
        return null;
    }

    private static boolean typeIsIncluded(String[] basicType, Collection<String> wordType) {
        if (basicType == null || wordType == null) return false;
        List<String> basicTypes = Arrays.asList(basicType);
        List<String> commonDataSet = wordType.stream().filter(val -> basicTypes.contains(val)).collect(Collectors.toList());
        if (commonDataSet.size() > 0) return true;
        return false;
    }
}
