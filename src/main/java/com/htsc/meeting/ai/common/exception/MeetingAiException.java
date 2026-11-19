package com.htsc.meeting.ai.common.exception;

import com.htsc.meeting.ai.common.enums.ErrorEnum;

/**
 * 会议 AI 业务异常
 */
public class MeetingAiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public MeetingAiException(String msg) {
        super(msg);
        this.code = ErrorEnum.SYSTEM_ERROR.getCode();
    }

    public MeetingAiException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public MeetingAiException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
