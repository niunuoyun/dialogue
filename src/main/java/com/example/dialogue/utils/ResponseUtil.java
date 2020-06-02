package com.example.dialogue.utils;

import com.alibaba.fastjson.JSONObject;
import com.example.dialogue.constant.ResponseStatus;

public class ResponseUtil {

    //成功时返回数据
    public static JSONObject ok(){
        JSONObject response = new JSONObject();
        response.put("code",ResponseStatus.SUCESS.getCode());
        response.put("message",ResponseStatus.SUCESS.getMessage());
        return response;
    }

    //错误时候返回的数据
    public static JSONObject error(){
        JSONObject response = new JSONObject();
        response.put("code",ResponseStatus.ERROR.getCode());
        response.put("message",ResponseStatus.ERROR.getMessage());
        return response;
    }
    //错误时候返回的数据
    public static JSONObject hasExists(){
        JSONObject response = new JSONObject();
        response.put("code",ResponseStatus.DATA_ALREADY_EXISTS.getCode());
        response.put("message",ResponseStatus.DATA_ALREADY_EXISTS.getMessage());
        return response;
    }

    //成功时返回数据
    public static JSONObject okWithData(Object o){
        JSONObject response = new JSONObject();
        response.put("code",ResponseStatus.SUCESS.getCode());
        response.put("message",ResponseStatus.SUCESS.getMessage());
        response.put("result",o);
        return response;
    }

    //返回结果中只有状态码和信息
    public static JSONObject status(ResponseStatus status){
        JSONObject response  = new JSONObject();
        response.put("code",status.getCode());
        response.put("message",status.getMessage());
        return response;
    }

}
