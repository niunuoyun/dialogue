package com.example.dialogue.segment;


import com.example.dialogue.DialogueApplication;
import com.example.dialogue.entity.Phrase;
import com.example.dialogue.genetate.QueryGenerate;
import com.example.dialogue.handler.SentenceHandler;
import com.example.dialogue.utils.StringUtil;
import lombok.val;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Huihua Niu
 * on 2019/8/30 14:57
 */
@Component
public class Tokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tokenizer.class);

    private static Forest forest;
    public static Map<String,Forest> forestMap = new HashMap<>();
    private static StopRecognition stopRecognition = new StopRecognition();

    static {

            List<File> files = new ArrayList<>();
            File file = new File(DialogueApplication.class.getClassLoader().getResource("library").getPath());
            getPath(file,files);
            files.stream().forEach(val-> {
                try {
                    forestMap.put(val.getName().replaceAll(".dic",""),Library.makeForest(val.getPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        try {
            InputStreamReader isr  = new InputStreamReader(DialogueApplication.class.getClassLoader().getResourceAsStream("stopWord.dic"));
            BufferedReader bf = new BufferedReader(isr);

            String stopWord = null;
            while ((stopWord = bf.readLine()) != null) {
                stopWord = stopWord.trim();
                //   StopWords.add(stopWord);
                stopRecognition.insertStopWords(stopWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public List<Phrase> segment(String text, boolean isRemoveStopWord) {
        Result result = DicAnalysis.parse(SentenceHandler.getStandardSentence(text),forest);
        if (isRemoveStopWord) {
            stopRecognition.recognition(result);
        }
        List<Term> list = result.getTerms();
        List<Phrase> phrases = new ArrayList<>();
        list.forEach(val -> phrases.add(new Phrase(val.getName(), val.getNatureStr(), val.getOffe())));
        return phrases;
    }
    public static List<Phrase> segmentByEntityType(String text,String entityType,boolean isRemoveStopWord) {
        if (StringUtils.isEmpty(entityType)|StringUtils.isEmpty(text)) return null;
        Result result = DicAnalysis.parse(SentenceHandler.getStandardSentence(text),forestMap.get(entityType),forestMap.get("default"));
        if (isRemoveStopWord) {
            stopRecognition.recognition(result);
        }
        List<Term> list = result.getTerms();
        List<Phrase> phrases = new ArrayList<>();
        list.forEach(val -> {
            if (!"c".equals(val.getNatureStr()) && !"w".equals(val.getNatureStr()) && !"e".equals(val.getNatureStr())){

                phrases.add(new Phrase(val.getName(), val.getNatureStr(), val.getOffe()));
            }
        });
        return phrases;
    }


    public static void getPath(File dir,List<File> paths){
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                getPath(new File(dir, children[i]),paths);
            }
        }else {
            paths.add(dir);
        }
    }

    public static void main(String[] args) {
        try {
            QueryGenerate queryCombine = new QueryGenerate();
            Result result = DicAnalysis.parse("杨幂演过哪些电影", forestMap.get("person"));

            List<Term> list = result.getTerms();
            System.out.println(list.toString());
            List<Phrase> wordsSh1 = segmentByEntityType(StringUtil.rmPunctuationAndSpace("谢霆锋的爸爸是谁"),"person",false);
            List<Phrase> wordsSh2 = segmentByEntityType(StringUtil.rmPunctuationAndSpace("他是谁的爸爸"),"person",false);
            String sentence = queryCombine.CombineQuery(wordsSh2,wordsSh1,"person");
            System.out.println(sentence);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
