package com.htsc.meeting.ai.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 事件 payload，一个事件可能携带多条会议操作记录
 */
@Data
public class MeetingEventPayload {

    /** 毫秒级事件操作时间戳 */
    @JsonProperty("operate_time")
    private Long operateTime;

    /** 云录制文件列表 */
    @JsonProperty("recording_files")
    private List<RecordingFileItem> recordingFiles;

    /** 会议信息 */
    @JsonProperty("meeting_info")
    private TencentMeetingInfo meetingInfo;

}
