package com.example.dialogue.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

/**
 * @author Huihua Niu
 * on 2020/6/10 18:09
 */
@Data
public class NluResponse {
    int code;
    int elpasedTime;
    String replyStatus;
    String message;
    ObjectNode result;
    JsonNode requestInfo;
    JsonNode dubugInfo;

    @Data
    public static class SlotList{
        String name;
        int start;
        int end;
        String value;
        ObjectNode attributes;
    }
}
