package com.example.dialogue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dialogue.client.XClient;
import com.example.dialogue.doublearra.AhoCorasickDoubleArrayTrie;
import com.example.dialogue.entity.KeyWordsResponse;
import com.example.dialogue.entity.NluResponse;
import com.example.dialogue.entity.Phrase;
import com.example.dialogue.entity.TriadNode;
import com.example.dialogue.genetate.QueryGenerate;
import com.example.dialogue.handler.NluResponseHandle;
import com.example.dialogue.handler.SentenceHandler;
import com.example.dialogue.queryvo.KeyWordsQueryVo;
import com.example.dialogue.queryvo.NluQueryVo;
import com.example.dialogue.segment.Tokenizer;

import com.example.dialogue.utils.JsonUtils;
import com.example.dialogue.utils.StringUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DialogueApplicationTests {

    @Test
    public void contextLoads() {
        try {
            QueryGenerate queryCombine = new QueryGenerate();
            Result result = DicAnalysis.parse("肖战从艺之前叫什么", Tokenizer.forestMap.get("person"));

            List<Term> list = result.getTerms();
            System.out.println(list.toString());
            List<Phrase> wordsSh1 = Tokenizer.segmentByEntityType(StringUtil.rmPunctuationAndSpace("谢霆锋的爸爸是谁"),"person",false);
            List<Phrase> wordsSh2 = Tokenizer.segmentByEntityType(StringUtil.rmPunctuationAndSpace("他是谁的爸爸"),"person",false);
            String sentence = queryCombine.CombineQuery(wordsSh2,wordsSh1,"person");
            System.out.println(sentence);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void loadKeyWords(){
        KeyWordsQueryVo keyWordsQueryVo = new KeyWordsQueryVo();
        keyWordsQueryVo.setTokens("刘恺威身高是多少");
        KeyWordsResponse keyWordsResponse = XClient.queryKeyWords(keyWordsQueryVo);
        Map<String,List<String>> firstMap = new HashMap<>();
        //句式处理
        String sentence = SentenceHandler.entityReplace(keyWordsResponse.getLabel(),keyWordsResponse.getIndexs(),keyWordsQueryVo.getTokens(),keyWordsResponse.getKeywords(),firstMap);
        //规则查询
        NluQueryVo nluQueryVo = new NluQueryVo();
        nluQueryVo.setEnv("sandbox");
        nluQueryVo.setResources(Arrays.asList("customIntent"));
        nluQueryVo.setProjectId("914000001");
        nluQueryVo.setSentence(sentence);
        NluResponse nluResponse = XClient.queryNLU(nluQueryVo);
        keyWordsQueryVo.setTokens("他老婆在哪工作");
        KeyWordsResponse keyWordsResponse1 = XClient.queryKeyWords(keyWordsQueryVo);
        Map<String,List<String>> listMap = new HashMap<>();
        String sentence1 = SentenceHandler.entityReplace(keyWordsResponse1.getLabel(),keyWordsResponse1.getIndexs(),keyWordsQueryVo.getTokens(),keyWordsResponse1.getKeywords(),listMap);
        nluQueryVo.setSentence(sentence1);

        NluResponse nluResponse1 = XClient.queryNLU(nluQueryVo);
        //上一轮的结果
        List<NluResponse.SlotList> preSlotList = NluResponseHandle.getQueryContent(nluResponse);
        //当前轮的结果
        List<NluResponse.SlotList> currentSlotList = NluResponseHandle.getQueryContent(nluResponse1);
        TriadNode triadNode = NluResponseHandle.triadRewrite(preSlotList,firstMap,currentSlotList,listMap);
        //单实体处理
        StringBuilder sb = new StringBuilder();
        if (triadNode!=null && triadNode.getSubjectContent()!=null && triadNode.getSubjectContent().size()==1){
            TriadNode child = triadNode.getChild();
            sb.append(triadNode.getSubjectContent().get(0)).append("的").append(triadNode.getPredicate());
            while (child != null) {
                if (Strings.isNotBlank(child.getPredicate())){
                    sb.append("的");
                    sb.append(child.getPredicate());
                    child = child.getChild();
                }else {
                    break;
                }
            }
        }

        System.out.println(sb.toString());
        System.out.println(triadNode.toString());

    }

    @Test
    public void loadTires() {
        // Collect test data set
        TreeMap<String, String> map = new TreeMap<String, String>();
        String[] keyArray = new String[]
                {
                        "人名",
                        "有多高",
                        "多高",
                        "是多少",
                        "身高"
                };
        for (String key : keyArray)
        {
            map.put(key, key);
        }
        // Build an AhoCorasickDoubleArrayTrie
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<String>();
        acdat.build(map);
        // Test it
        final String text = "我想知道姚明的身高是多少呢";
        List<AhoCorasickDoubleArrayTrie.Hit<String>> wordList = acdat.parseText(text);
        System.out.println(wordList);
    }



}
