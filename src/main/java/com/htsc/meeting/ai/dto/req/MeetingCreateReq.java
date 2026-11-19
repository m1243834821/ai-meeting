package com.htsc.meeting.ai.dto.req;

import lombok.Data;

import java.util.List;

/**
 * 会议创建请求参数
 */
@Data
public class MeetingCreateReq {

    private String meetingName;
    private String type;
    /** yyyy-MM-dd HH:mm:ss */
    private String createTime;
    /** TENCENT / FEISHU */
    private String meetingType;
    private String creatorEmpNo;
    private String scheduleId;
    private String meetingCenterId;
    private String sessionId;
    private String meetingContent;
    private String videoMeetingNo;
    private String creatorDeptCode;
    private String meetingRoom;
    /** yyyy-MM-dd HH:mm:ss */
    private String startTime;
    /** yyyy-MM-dd HH:mm:ss */
    private String endTime;
    private String creatorName;
    private String creatorDept;
    private String creatorPosition;
    private List<ParticipantReq> participantList;

    @Data
    public static class ParticipantReq {
        private String empNo;
        private String name;
        private String dept;
        /** 需求概述要求参会人部门编号；建表字段允许为空 */
        private String deptCode;
        private String position;
    }
}
