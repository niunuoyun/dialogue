package com.example.dialogue.constant;

public enum ResponseStatus {

    /**
     * 以前是通用的状态码返回
     */
    SUCESS(200,"sucess"),
    ERROR(201,"error"),
    FUNCTION_NOT_COMPLEMENT(202,"the skill is not finished　or your input is not correct"),
    DATA_NOT_FOUND(203,"the segment data not found"),
    /**
     * 以下状态码是与业务相关的
     */
    //用户没有传userid的时候会返回
    NO_USER_ID(302,"input param without user_id"),

    //用户没有传productId的时候会返回
    NO_PRODUCT_ID(303,"input param without product_id"),

    //用户没有传主题相关的字段时会返回
    LITERATURE(304,"input param without literature"),

    //用户没有传sessionId的时候会返回
    NO_SESSION_ID(305,"input param without session_id"),

    //用户没有传opration
    NO_OPRATION(306,"input param without opration"),

    //用户没有传入optional_term
    NO_OPTIONAL_TERM(307,"input param without optional_term"),

    //用户没有传入question_type
    NO_QUESTION_TYPE(308,"input param without question_type"),

    //用户传入的参数中没有question_subtype
    NO_QUESTION_SUBTYPE(309,"input param without question_subtype"),

    FROM_OR_SIZE_ERROR(310,"from or size must be int type size less than 500"),

    FROM_OR_SIZE_NOT_FOUND(311,"input param without from or size"),

    //用户没有传入request_body
    NO_REQUEST_BODY(312,"no request body"),
    //用户没有传sessionId的时候会返回
    NO_TYPE(313,"input param without type field"),
    TYPE_NOT_NULL(314,"the contents of the type field cannot be empty"),
    NO_CONTENTS(315,"input param without contents field or contents is null"),
    NO_CONTENTIDS(316,"input param without contentIds field or contentIds is null"),
    TYPE_NOT_SUPPORT(317,"this type is not supported"),
    DATA_ALREADY_EXISTS(318,"all the data already exists or no data can be inserted"),
    FILEDS_MISSING_OR_FORMAT_WRONG(319,"某些字段缺失或者传递的数据格式有误");

    private String message;

    private Integer code;

    ResponseStatus(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }

    public void setCode(Integer code){
      this.code = code;
    }

    public void setMessage(String message){
        this.message = message;
    }

}
