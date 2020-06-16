package com.example.dialogue.tries;

import com.example.dialogue.entity.AskData;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

/**
 * @author Huihua Niu
 * on 2020/6/8 11:54
 */
@Data
public class TreeNode {
    Integer minlen = 100;
    Integer maxlen = 1;
    Map<String, TreeNode> children;
    String atrb;
    String featureWord; //词性特征
    String standardWord; //标准词

    public TreeNode() {
        children = new HashMap<>();
    }
    public TreeNode(TreeNode treeNode) {
        children = new HashMap<>(treeNode.getChildren());
    }
    /**
     * 插入字典树
     *
     * @param patn
     * @param atrb
     */
    static void insert(String patn, String atrb, TreeNode ptr) {
        if (patn == null || patn.length() == 0 || atrb == null || atrb.length() == 0) {
            return;
        }
        String[] words = patn.trim().split("    ");
        for (String word : words) {
            if (ptr.getChildren().get(word) == null) {
                ptr.getChildren().put(word, new TreeNode());
            }
            if (ptr.getMinlen() > word.length()) {
                ptr.setMinlen(word.length());
            }
            if (ptr.getMaxlen() < word.length()) {
                ptr.setMaxlen(word.length());
            }
            ptr = ptr.getChildren().get(word);
        }
        ptr.setAtrb(atrb);
    }
    /**
     * 插入字典树
     *
     * @param rule
     */
    static void insertRule(String rule, TreeNode ptr, Map<String,List<AskData>> askWords) {
        if (Strings.isBlank(rule)) {
            return;
        }
        String atrb=null,featureWord,standardWord;
        String[] words = rule.split(" ");
        if (rule.contains("->")){
            atrb = words[words.length-1];
        }
        List<AskData> askWordList = null;TreeNode askNode=null;
        for (String word : words) {
            if (Objects.nonNull(askWords.get(word))) {
                askWordList = askWords.get(word);
                askNode = new TreeNode(ptr);
            }

            if (word.startsWith("[")){
                String[] vocabulary = word.replaceAll("[\\[\\]]","").split("\\|");
                List<String> synonymList = Arrays.asList(vocabulary);
                for (String synonym : synonymList){
                    wordHandle(synonym,ptr);
                }
                continue;
            }
            wordHandle(word,ptr);
            ptr.setAtrb(atrb);
            ptr = ptr.getChildren().get(word);
        }
        if (Objects.nonNull(askWordList)){
            ptr = askNode;
            for (TreeNode treeNode :ptr.getChildren().values()){

                for (AskData ask : askWordList){
                    featureWord = ask.getFeatureWord();
                    standardWord = ask.getStandardWord();
                    List<String> wordList = Arrays.asList(ask.getKeyWords().split("\\|"));
                    for (String askStr : wordList){
                        wordHandle(askStr,treeNode);
                        treeNode.getChildren().get(askStr).setFeatureWord(featureWord);
                        treeNode.getChildren().get(askStr).setStandardWord(standardWord);
                        if (Strings.isNotBlank(ptr.atrb)) treeNode.getChildren().get(askStr).setAtrb(ptr.atrb);
                    }
                    continue;
                }
            }
        }


    }


    private static void wordHandle(String word,TreeNode ptr) {
        if (ptr.getChildren().get(word) == null) {
            ptr.getChildren().put(word, new TreeNode());
        }
        if (ptr.getMinlen() > word.length()) {
            ptr.setMinlen(word.length());
        }
        if (ptr.getMaxlen() < word.length()) {
            ptr.setMaxlen(word.length());
        }
    }
    public static void main(String[] args) {
        String patn1 = "人名 [享年|挂掉时间|挂的|驾崩|死的|去世|逝世|病逝|病死|战死|过世|去世|死亡] 询问_时间 人名->享年";//人名  [享年|挂掉时间|挂的|驾崩|死的|去世|逝世|病逝|病死|战死|过世|去世|死亡]  询问_时间   人名->享年
        TreeNode ptr = new TreeNode();
        Map<String,List<AskData>> askDataMap = new HashMap<>();
        AskData askData = new AskData();
        askData.setKeyWords("哪年哪月|哪天哪月|哪天哪月哪年");
        askData.setFeatureWord("询问_时间");
        askData.setStandardWord("具体年月");
        askDataMap.put("询问_时间",Arrays.asList(askData));
        insertRule(patn1,ptr,askDataMap);

        System.out.println(ptr.getChildren().get("人名"));
    }
}
