package com.example.dialogue.handler;


import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
}
