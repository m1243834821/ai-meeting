package com.htsc.meeting.ai.common.enums;

/**
 * 统一错误码枚举
 */
public enum ErrorEnum {

    SYSTEM_ERROR("10001", "当前服务暂不可用，请稍后重试。"),
    PARAM_ERROR("10002", "参数错误"),
    ;

    private String code;
    private String msg;

    private ErrorEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
