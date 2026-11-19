package com.htsc.meeting.ai.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 会议创建人
 */
@Data
public class MeetingCreator {

    /** 创建人工号 */
    private String userid;

    /** 创建人名称 */
    @JsonProperty("user_name")
    private String userName;

}
