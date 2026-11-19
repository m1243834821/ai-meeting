package com.htsc.meeting.ai.service.impl;

import com.htsc.meeting.ai.dto.req.MinutesConfirmReq;
import com.htsc.meeting.ai.service.MinutesConfirmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 低代码纪要确认流程占位实现。
 * <p>
 * TODO 低代码接口确定后补齐：入参云文档链接、创建人工号、模型编码，出参纪要确认低代码 ID。
 */
@Slf4j
@Service("minutesConfirmService")
public class MinutesConfirmServiceImpl implements MinutesConfirmService {

    @Override
    public String createConfirmFlow(MinutesConfirmReq confirmReq) {
        log.info("minutes confirm flow not implemented yet, traceId:{}, scheduleId:{}",
                confirmReq == null ? null : confirmReq.getTraceId(),
                confirmReq == null ? null : confirmReq.getScheduleId());
        return null;
    }

}
