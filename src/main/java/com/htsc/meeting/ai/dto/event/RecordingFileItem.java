package com.htsc.meeting.ai.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 云录制文件
 */
@Data
public class RecordingFileItem {

    /** 云录制文件 ID */
    @JsonProperty("record_file_id")
    private String recordFileId;

}
