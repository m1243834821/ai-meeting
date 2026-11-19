package com.htsc.meeting.ai.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 腾讯会议信息
 */
@Data
public class TencentMeetingInfo {

    /** 会议 ID */
    @JsonProperty("meeting_id")
    private String meetingId;

    /** 会议 code（9 位会议号），用于关联主表 VIDEO_MEETING_NO */
    @JsonProperty("meeting_code")
    private String meetingCode;

    /** 媒体类型：1-外部会议，2-内部会议 */
    @JsonProperty("media_set_type")
    private String mediaSetType;

    /** 会议主题 */
    private String subject;

    /** 创建人信息 */
    private MeetingCreator creator;

    /** 会议类型 */
    @JsonProperty("meeting_type")
    private Integer meetingType;

    /** 秒级会议开始时间戳 */
    @JsonProperty("start_time")
    private Long startTime;

    /** 秒级会议结束时间戳 */
    @JsonProperty("end_time")
    private Long endTime;

}
