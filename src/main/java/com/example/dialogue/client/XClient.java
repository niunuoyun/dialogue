package com.example.dialogue.client;

import com.example.dialogue.entity.KeyWordsResponse;
import com.example.dialogue.entity.NluResponse;
import com.example.dialogue.queryvo.KeyWordsQueryVo;
import com.example.dialogue.queryvo.NluQueryVo;
import com.example.dialogue.utils.JsonUtils;
import com.example.dialogue.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Huihua Niu
 * on 2020/6/3 15:36
 */
@Slf4j
public class XClient {
    /**
     * 关键词接口查询
     *
     * @param queryVo
     * @return
     */
    public static KeyWordsResponse queryKeyWords(KeyWordsQueryVo queryVo) {
        try {
            String q = JsonUtils.toString(queryVo);
            String result = WebUtils.midPost("http://10.12.6.39:3056", "/keywords/predict", q);
            KeyWordsResponse node = JsonUtils.toObject(result, KeyWordsResponse.class);
            return node;
        } catch (Exception e) {
            String info = String.format("error processing querying answer for query -- %s -- %s -- %s",
                    "/userprofile/info", queryVo.toString(), e.getMessage());
            log.info(info);
            KeyWordsResponse responseVo = new KeyWordsResponse();
            return responseVo;
        }
    }
    /**
     * 关键词接口查询
     *
     * @param queryVo
     * @return
     */
    public static NluResponse queryNLU(NluQueryVo queryVo) {
        try {
            String q = JsonUtils.toString(queryVo);
            String result = WebUtils.midPost("http://nlu.talkinggenie.com", "/api/v2/matchPattern", q);
            NluResponse node = JsonUtils.toObject(result, NluResponse.class);
            return node;
        } catch (Exception e) {
            String info = String.format("error processing querying answer for query -- %s -- %s -- %s",
                    "/api/v2/matchPattern", queryVo.toString(), e.getMessage());
            log.info(info);
            NluResponse responseVo = new NluResponse();
            return responseVo;
        }
    }

}
