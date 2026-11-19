package com.htsc.meeting.ai.dto.event;

import lombok.Data;

/**
 * Kafka 外层消息体
 */
@Data
public class MeetingEventMessage {

    /** 租户 ID */
    private String tenantId;

    /** 发送时间戳 */
    private Long timestamp;

    /** AES 加密后的事件内容 */
    private String data;

}
