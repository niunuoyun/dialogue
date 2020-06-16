package com.example.dialogue.handler;


import org.nlpcn.commons.lang.util.StringUtil;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Huihua Niu
 * on 2019/11/1 10:39
 */
public class SentenceHandler {
   public static List<String>  nonEssential = new ArrayList<>();
    static {
        InputStream stream = SentenceHandler.class.getClassLoader().getResourceAsStream("library/nonEssential.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream,"utf-8"))) {
            br.lines().forEach(l->{
                nonEssential.add(l);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String getStandardSentence(String query){
        if (!StringUtils.isEmpty(query)){
            return query.replaceAll(String.join("|",nonEssential),"");
        }
        return null;
    }

    public static void main(String[] args) {
        String query = "";
        System.out.println(getStandardSentence(query));
    }

    // 实体词替换,使用一个容器存储对应的位置存放的数据
    public static String entityReplace(List<String> label, int[] indexs, String sentence, List<String> tokens, Map<String,List<String>> listMap){
        if (Objects.isNull(label) || Objects.isNull(indexs) || StringUtil.isBlank(sentence)) return null;
        if (label.size()!=indexs.length || tokens.size()!=indexs.length) return sentence;
        StringBuilder sb = new StringBuilder(sentence);
        for (int i=0;i<label.size();i++){
            if ("其它".equals(label.get(i))) continue;
            if (listMap.get(label.get(i)) == null){
                listMap.put(label.get(i), Arrays.asList(tokens.get(i)));
            }else {
                listMap.get(label.get(i)).add(tokens.get(i));
            }
            sb.replace(indexs[i],tokens.get(i).length(),label.get(i));
        }
        return sb.toString();
    }
}
