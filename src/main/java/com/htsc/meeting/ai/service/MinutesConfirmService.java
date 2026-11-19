package com.htsc.meeting.ai.service;

import com.htsc.meeting.ai.dto.req.MinutesConfirmReq;

/**
 * 低代码纪要确认流程服务（本期占位，待低代码接口确定后实现）
 */
public interface MinutesConfirmService {

    /**
     * 创建纪要确认低代码流程
     *
     * @return 纪要确认低代码 ID，本期返回 null
     */
    String createConfirmFlow(MinutesConfirmReq confirmReq);

}
