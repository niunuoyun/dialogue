package com.example.dialogue.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Huihua Niu
 * on 2020/6/9 17:26
 */
public class Word {
    List<String> wordList;
    Word next;

    public Word() {
        this.wordList = new ArrayList<>();
    }
}
