package com.example.dialogue.entity;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Huihua Niu
 * on 2020/6/11 11:34
 */
@Data
public class TriadNode {
    // 主语
    String subject;
    //对应的主语内容
    List<String> subjectContent;
    // 谓语
    String predicate;

    //下一个三元组信息
    TriadNode child;

    public TriadNode initChild() {
        child = new TriadNode();
        return child;
    }

    @Override
    public String toString() {
        return "TriadNode{" +
                "subject='" + subject + '\'' +
                ", subjectContent=" + subjectContent +
                ", predicate='" + predicate + '\'' +
                ", child=" + child +
                '}';
    }
}
