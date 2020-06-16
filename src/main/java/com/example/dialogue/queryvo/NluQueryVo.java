package com.example.dialogue.queryvo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

/**
 * @author Huihua Niu
 * on 2020/6/10 18:09
 */
@Data
public class NluQueryVo {
    String projectId;
    String env;
    List<String> resources;
    List<String> intents;
    String sentence;
    List<String> tokens;
}
