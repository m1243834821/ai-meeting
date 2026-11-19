package com.htsc.meeting.ai.entity;

import lombok.Data;

/**
 * 会议流程 AI 化主表 meeting_ai_workflow_info 映射实体
 */
@Data
public class MeetingAiWorkflowInfoModel {

    /** 主键 ID */
    private Long id;

    /** 会议名称 */
    private String meetingName;

    /** 日程 ID */
    private String scheduleId;

    /** 会议中心 ID */
    private String meetingCenterId;

    /** 视频会议号（对应腾讯会议 meeting_code） */
    private String videoMeetingNo;

    /** 云文档 ID，多个以逗号分隔 */
    private String cloudDocId;

    /** 创建人工号 */
    private String creatorEmpNo;

}
