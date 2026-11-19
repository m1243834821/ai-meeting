package com.htsc.meeting.ai.entity;

import lombok.Data;

/**
 * 会议参会人表映射实体
 */
@Data
public class MeetingParticipantModel {

    private Long meetingId;
    private String meetingCenterId;
    private String empNo;
    private String name;
    private String dept;
    private String deptCode;
    private String position;
}
