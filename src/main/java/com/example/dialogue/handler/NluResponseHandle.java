package com.example.dialogue.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dialogue.entity.NluResponse;
import com.example.dialogue.entity.TriadNode;
import com.example.dialogue.utils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

/**
 * @author Huihua Niu
 * on 2020/6/11 15:49
 */
public class NluResponseHandle {

    public static List<String> getEntity(List<String> label){
        List<String> entityLabel = new ArrayList<>();
        if (Objects.isNull(label)) return entityLabel;
        label.forEach(val->{if (!"其它".equals(val)){entityLabel.add(val);}});
        return entityLabel;
    }

    public static List<NluResponse.SlotList> getQueryContent(NluResponse nluResponse){
        if (nluResponse == null || nluResponse.getResult() == null) return null;
        if (nluResponse.getResult().get("customIntent")!=null && nluResponse.getResult().get("customIntent").size()>0){
            try {
                JSONArray jsonArray = JSON.parseArray(JsonUtils.toString(nluResponse.getResult().get("customIntent")));
                if (jsonArray.size()==1){
                    JsonNode slotListJson = JsonUtils.toJsonNode(jsonArray.get(0)).get("slotList");
                    if (slotListJson!=null){
                        List<NluResponse.SlotList> slotList = JSONObject.parseArray(slotListJson.toString(),NluResponse.SlotList.class);
                        return slotList;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

        }
        return null;
    }

    public static List<TriadNode> getTriadNode(List<NluResponse.SlotList> slotList){
        List<TriadNode> triadNodes = new ArrayList<>(slotList.size());
        for (NluResponse.SlotList slot : slotList){
            if (Strings.isBlank(slot.getValue())) break;
            String[] triadStr = slot.getValue().split("=>");
            TriadNode triadNode = new TriadNode();
            boolean isFirst = true;
            for (String str : triadStr){
                List<String> triadInfo = new ArrayList<>(Arrays.asList(str.split("_")));
                if (triadInfo.contains("request") && isFirst){
                    triadInfo.remove("request");
                    triadNode.setSubject(triadInfo.get(0));
                    if (triadInfo.size()==2){
                        triadNode.setPredicate(triadInfo.get(1));
                    }else {
                        break;
                    }
                    isFirst = false;
                    continue;
                }else if(!isFirst){
                    TriadNode triadNodeChild = triadNode.initChild();
                    triadNodeChild.setSubject(triadInfo.get(0));
                    if (triadInfo.size() == 2) {
                        triadNodeChild.setPredicate(triadInfo.get(1));
                    } else {
                       break;
                    }
                }
            }
            triadNodes.add(triadNode);
        }

        return triadNodes;
    }

    public static TriadNode triadRewrite(List<NluResponse.SlotList> preSlotList, Map<String,List<String>> preMap, List<NluResponse.SlotList> currentSlotList,Map<String,List<String>> currentMap){
        if (Objects.isNull(preSlotList) || Objects.isNull(currentSlotList)) return null;
        List<TriadNode> preTriadNodes = getTriadNode(preSlotList);
        List<TriadNode> currentTriadNodes = getTriadNode(currentSlotList);
        // 如果只牵扯到一跳的情况处理
        if (preSlotList.size() == 1 && currentTriadNodes.size()>=1){
            TriadNode current = currentTriadNodes.get(0);
            TriadNode pre = preTriadNodes.get(0);
            if (Strings.isNotBlank(current.getSubject()) && Strings.isNotBlank(pre.getSubject())) {
                current.setSubjectContent(currentMap.get(current.getSubject()) == null?preMap.get(current.getSubject()):currentMap.get(current.getSubject()));
                pre.setSubjectContent(preMap.get(pre.getSubject()));
                return getValidTriadNode(current, pre);
            }
        }
        return null;
    }




    public static TriadNode getValidTriadNode(TriadNode current, TriadNode pre){
        // 主语一样的情况下的处理
        if (current.getSubject().equals(pre.getSubject()) ){
            TriadNode currentChild = current.getChild();
            TriadNode preChild = pre;
            if (currentChild!=null){
                while (currentChild!=null){
                    // 如果说当前轮次的下一跳的三元组的主语，和上一轮的主语一致
                    if ( preChild!=null && currentChild.getSubject().equals(preChild.getSubject())){
                        if (Strings.isBlank(currentChild.getPredicate())){
                            currentChild.setPredicate(pre.getPredicate());
                            break;
                        }else {
                            currentChild = currentChild.getChild();
                            preChild = preChild.getChild();
                            continue;
                        }
                    }
                }
            }else {
                if (Strings.isBlank(current.getPredicate()) && Strings.isNotBlank(pre.getPredicate())){
                    current.setPredicate(pre.getPredicate());
                }
            }
        }

        return current;
    }



























  /*  public static void sentenceRewrite(List<NluResponse.SlotList> preSlotList,List<NluResponse.SlotList> currentSlotList,boolean lostSubject){
        // 缺失主语的情况
        StringBuilder queryContent= new StringBuilder();
        if (lostSubject && currentSlotList.size()==1 && preSlotList.size()==1){
            //告知获取的是上一轮的主语
            String currentContent = currentSlotList.get(0).getValue();
            String preContent = preSlotList.get(0).getValue();
            // 确定是查询信息
            if (currentContent.startsWith("request_") && preContent.startsWith("request_")) {
                List<String> preTriedList = Arrays.asList(preContent.split("=>"));
                List<String> currentTriedList = Arrays.asList(currentContent.split("=>"));
                TriadNode triadNode = new TriadNode();
                preSlotList.forEach(val->{
                    String[] triadStr = val.s
                });
                // 如果两个相等，询问的是同一类实体
                if (preTriedList.size() == currentTriedList.size() && currentTriedList.size()==1){
                    queryContent.append(currentTriedList.get(0));
                    //返回对应的数据
                    return;
                }
                boolean isFirst = true;
                for (String current : currentTriedList){
                    if (isFirst){
                        queryContent.append(current);
                        isFirst = false;
                    }else {
                        String[] nextPred =  current.split("_");
                        String[]
                        if ()
                    }
                }
            }
        }
    }*/
}
