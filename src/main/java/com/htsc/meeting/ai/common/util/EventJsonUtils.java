package com.htsc.meeting.ai.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htsc.meeting.ai.common.exception.MeetingAiException;
import com.htsc.meeting.ai.dto.event.MeetingEventMessage;
import com.htsc.meeting.ai.dto.event.MeetingRecordingEvent;

/**
 * 腾讯会议事件 JSON 解析工具
 */
public final class EventJsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private EventJsonUtils() {
    }

    /**
     * 解析 Kafka 外层信封：{tenantId, timestamp, data}
     */
    public static MeetingEventMessage parseMessage(String rawMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            throw new MeetingAiException("10006", "Kafka 消息内容为空");
        }
        try {
            MeetingEventMessage message = OBJECT_MAPPER.readValue(rawMessage, MeetingEventMessage.class);
            if (message == null) {
                throw new MeetingAiException("10007", "腾讯会议事件外层报文解析结果为空");
            }
            return message;
        } catch (MeetingAiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeetingAiException("10007", "腾讯会议事件外层报文解析失败", e);
        }
    }

    /**
     * 解析解密后的内层事件：{event, trace_id, payload}
     */
    public static MeetingRecordingEvent parseEvent(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            throw new MeetingAiException("10008", "腾讯会议事件明文内容为空");
        }
        try {
            MeetingRecordingEvent event = OBJECT_MAPPER.readValue(plainText, MeetingRecordingEvent.class);
            if (event == null) {
                throw new MeetingAiException("10009", "腾讯会议事件内容解析结果为空");
            }
            return event;
        } catch (MeetingAiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeetingAiException("10009", "腾讯会议事件内容解析失败", e);
        }
    }

}
