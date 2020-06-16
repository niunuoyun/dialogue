package com.example.dialogue.tries;

import java.io.*;
import java.util.*;

public class SensitiveWordFilter {

    private static Map sensitiveWordMap;

    public boolean isContaintSensitiveWord(String txt,int matchType){
        boolean flag = false;
        for(int i = 0 ; i < txt.length() ; i++){
            int matchFlag = this.checkSensitiveWord(txt, i, matchType); //判断是否包含敏感字符
            if(matchFlag > 0){    //大于0存在，返回true
                flag = true;
                break;
            }
        }
        return flag;
    }

    private String getReplaceChars(String replaceChar,int length){
        String resultReplace = replaceChar;
        for(int i = 1 ; i < length ; i++){
            resultReplace += replaceChar;
        }

        return resultReplace;
    }

    public String replaceSensitiveWord(String txt,int matchType,String replaceChar){
        String resultTxt = txt;
        Set<String> set = getSensitiveWord(txt, matchType);     //获取所有的敏感词
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    public Set<String> getSensitiveWord(String txt , int matchType){
        Set<String> sensitiveWordList = new HashSet<>();

        for(int i = 0 ; i < txt.length() ; i++){
            int length = checkSensitiveWord(txt, i, matchType);    //判断是否包含敏感字符
            if(length > 0){    //存在,加入list中
                sensitiveWordList.add(txt.substring(i, i+length));
                i = i + length - 1;    //减1的原因，是因为for会自增
            }
        }

        return sensitiveWordList;
    }


    public int checkSensitiveWord(String txt,int beginIndex,int matchType){
        boolean  flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况
        int matchFlag = 0;     //匹配标识数默认为0
        char word = 0;
        Map nowMap = sensitiveWordMap;
        for(int i = beginIndex; i < txt.length() ; i++){
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);     //获取指定key
            if(nowMap != null){     //存在，则判断是否为最后一个
                matchFlag++;     //找到相应key，匹配标识+1
                if("1".equals(nowMap.get("isEnd"))){       //如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag = true;       //结束标志位为true
                    if(1 == matchType){    //最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            } else{     //不存在，直接返回
                break;
            }
        }
        if(!flag){        //长度必须大于等于1，为词
            matchFlag = 0;
        }
        return matchFlag;
    }

   public void init() {
       InputStream in = this.getClass().getResourceAsStream("/SensitiveWord.txt");
       InputStreamReader read = null;
       Set<String> keyWordSet = new HashSet<>();
       try {
           read = new InputStreamReader(in, "UTF-8");
           BufferedReader bufferedReader = new BufferedReader(read);
           String txt = null;
           while ((txt = bufferedReader.readLine()) != null) {    //读取文件，将文件内容放入到set中
               keyWordSet.add(txt);
           }
           sensitiveWordMap = new HashMap(keyWordSet.size());     //初始化敏感词容器，减少扩容操作

           Map nowMap = null;
           Map<String, String> newWorMap = null;
           //迭代keyWordSet
            for(String key : keyWordSet){
               nowMap = sensitiveWordMap;
               for(int i = 0 ; i < key.length() ; i++){
                   char keyChar = key.charAt(i);       //转换成char型
                   Object wordMap = nowMap.get(keyChar);       //获取

                   if(wordMap != null){        //如果存在该key，直接赋值
                       nowMap = (Map) wordMap;
                   }
                   else{     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                       newWorMap = new HashMap<String,String>();
                       newWorMap.put("isEnd", "0");     //不是最后一个
                       nowMap.put(keyChar, newWorMap);
                       nowMap = newWorMap;
                   }

                   if(i == key.length() - 1){
                       nowMap.put("isEnd", "1");    //最后一个
                   }
               }
           }
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try {
               read.close();
               in.close();     //关闭文件流
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }


}
