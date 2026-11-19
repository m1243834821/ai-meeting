package com.htsc.meeting.ai.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 解密后的腾讯会议事件
 */
@Data
public class MeetingRecordingEvent {

    /** 事件名，例如 recording.completed */
    private String event;

    /** 事件唯一序列值 */
    @JsonProperty("trace_id")
    private String traceId;

    /** 事件内容列表 */
    private List<MeetingEventPayload> payload;

}
