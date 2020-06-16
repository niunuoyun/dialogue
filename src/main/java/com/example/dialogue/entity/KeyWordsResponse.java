package com.example.dialogue.entity;

import lombok.Data;

import java.util.List;

/**
 * @author Huihua Niu
 * on 2020/6/3 15:37
 */
@Data
public class KeyWordsResponse {
    String type;
    int[] indexs;
    List<String> keywords;
    List<String> label;
    List<String> token;
    List<String> pos;
    List<String> token_all;
    List<String> pos_all;
    String time;
    String version;


}
