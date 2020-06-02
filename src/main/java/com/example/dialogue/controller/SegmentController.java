package com.example.dialogue.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.dialogue.constant.ResponseStatus;
import com.example.dialogue.entity.Phrase;
import com.example.dialogue.genetate.QueryGenerate;
import com.example.dialogue.segment.Tokenizer;

import com.example.dialogue.utils.ResponseUtil;
import com.example.dialogue.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Huihua Niu
 * on 2019/7/19 15:16
 */
@RestController
@RequestMapping(value = "query")
public class SegmentController {
    @Autowired
    Tokenizer tokenizer;
    @Autowired
    QueryGenerate queryCombine;
    @PostMapping(value = "segment")
    public JSONObject query(@RequestBody JSONObject requestBody){
        String query = requestBody.getString("query");
        boolean stopWord = requestBody.getBooleanValue("removeStopWord");
        if (StringUtils.isEmpty(query))return ResponseUtil.status(ResponseStatus.DATA_NOT_FOUND);
        List<Phrase> segResult = tokenizer.segment(query,stopWord);
        return ResponseUtil.okWithData(segResult);
    }
    @PostMapping(value = "combineSentence")
    public JSONObject combineSentence(@RequestBody JSONObject requestBody){
        String first = requestBody.getString("first");
        String firstType = requestBody.getString("firstEntityType");
        String second = requestBody.getString("second");
        String secondType = requestBody.getString("secondEntityType");
        List<Phrase> wordsSh1 = tokenizer.segment(StringUtil.rmPunctuationAndSpace(first.trim()),false);
        List<Phrase> wordsSh2 = tokenizer.segment(StringUtil.rmPunctuationAndSpace(second.trim()),true);
        String sentence = queryCombine.CombineQuery(wordsSh2,wordsSh1,"");
        return ResponseUtil.okWithData(sentence);
    }
    @PostMapping(value = "combine")
    public JSONObject combine(@RequestBody JSONObject requestBody){
        String first = requestBody.getString("first");
        String second = requestBody.getString("second");
        List<Phrase> wordsSh1 = tokenizer.segment(StringUtil.rmPunctuationAndSpace(first.trim()),false);
        List<Phrase> wordsSh2 = tokenizer.segment(StringUtil.rmPunctuationAndSpace(second.trim()),true);
        String sentence = queryCombine.CombineQuery(wordsSh2,wordsSh1,"");
        return ResponseUtil.okWithData(sentence);
    }
}
