package com.htsc.meeting.ai.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会议主表映射实体
 */
@Data
public class MeetingInfoModel {

    private Long id;
    private String meetingName;
    private String type;
    private LocalDateTime createTime;
    private String meetingType;
    private String creatorEmpNo;
    private String scheduleId;
    private String meetingCenterId;
    private String sessionId;
    private String meetingContent;
    private String videoMeetingNo;
    private String meetingRoom;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String creatorDeptCode;
    private String creatorName;
    private String creatorDept;
    private String creatorPosition;
}
