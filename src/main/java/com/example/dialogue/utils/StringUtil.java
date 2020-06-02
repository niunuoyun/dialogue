package com.example.dialogue.utils;

/**
 * Created by yincx1 on 2019/4/29.
 */

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by yincx1 on 2018/4/13.
 */
public class StringUtil {
    public static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{P}");
    public static final Pattern SPECIAL_CHARACTER = Pattern.compile("[^0-9a-zA-Z\\u4e00-\\u9fa5]");
    public static final Pattern INTEGER = Pattern.compile("^[-\\+]?[\\d]*$");

    //仅支持中文
    public static String rmPunctuationAndSpace(String str){
//        return str.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5]","");
        return SPECIAL_CHARACTER.matcher(str.toLowerCase()).replaceAll("");
        //return str.toLowerCase().replaceAll("[\\\\|·《》@\\(（）\\)\\*\\$_`!~#%^&\\*\\+=\\{\\}':;',\\[\\]\\.<>/\\?~！￥%…&\\*（）—\\+\\{\\}【】‘；：”“’。，、？\"\\-\\t\\n\\r\\s]","");
    }

    public static List<Character> str2CharList(String str){
        char[] charTest = str.toLowerCase().toCharArray();
        List<Character> list = new ArrayList<>();
        for (char element:charTest) {
            list.add(element);
        }
        return list;
    }

    public static Set<Character> str2CharSet(String str){
        char[] charTest = str.toLowerCase().toCharArray();
        Set<Character> set = new HashSet<>();
        for (char element:charTest) {
            set.add(element);
        }
        return set;
    }

    public static Set<Character> str2CharSet(Collection<String> strList){
        Set<Character> set = new HashSet<>();
        for (String str : strList) {
            char[] charTest = str.toLowerCase().toCharArray();

            for (char element:charTest) {
                set.add(element);
            }
        }
        return set;
    }

    public static boolean hasSpecialChar(String line){
        return SPECIAL_CHARACTER.matcher(line).find();
    }

    public static boolean isZH_UTF8(String str){
        if(str == null) return false;
        return str.getBytes().length == str.length()*3;
    }

    public static boolean isInteger(String str) {
        return INTEGER.matcher(str).matches();
    }

    public static boolean has2CharType(String str){
        int isChinese = 0;
        int isDigit = 0;
        int isLetter = 0;
        //判断是否包含汉字
        //英文字符串和中文字符串最大的区别在于每一个英文字符占用一个字节，而每一个中文字符占用两个字节。
        //知道这一点，就为我们检测字符串中是否有中文提供了实现思路，那就是将字符串先转换为字节数组，并获取字节数组的长度，然后与原字符串的长度作比较，如果字节数组的长度大于字符串的长度，那么，目标字符串中就含有中文字符。
        if(!(str.length()==str.getBytes().length)) isChinese = 1;

        for (char c : str.toCharArray()) {
            //判断是否包含数字
            if(Character.isDigit(c)) {
                isDigit = 1;
            }
            //判断是否包含英文
            if(c>='A' && c<='Z'  ||  c>='a' && c<='z'){
                isLetter = 1;
            }
        }
        return (isChinese+isDigit+isLetter)>1;
    }
}