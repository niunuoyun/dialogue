package com.example.dialogue.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Huihua Niu
 * on 2019/8/29 18:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Phrase {
    /**
     * 词语内容
     */
    private String value;

    /**
     * 词性
     */
    private Set<String> typeSet;

    /**
     * 词频
     */
    private double frequence;
    /** 短语位置和长度 */
    public int position, length;
    public Phrase(String value, String type, double frequence) {
        this.value = value;
        this.typeSet = typeToSet(type);
        this.frequence = frequence;
    }

    /**
     * 获取词性
     *
     * @return
     */
    public Set<String> typeToSet(String type) {
        if (!StringUtils.isEmpty(type)) {
            String[] typeArr = type.split(",");
            return new HashSet<>(Arrays.asList(typeArr));
        }
        return new HashSet<>();
    }

}
