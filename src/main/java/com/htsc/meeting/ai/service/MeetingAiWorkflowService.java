package com.htsc.meeting.ai.service;

import com.htsc.meeting.ai.entity.MeetingAiWorkflowInfoModel;

/**
 * 会议流程 AI 化主表服务
 */
public interface MeetingAiWorkflowService {

    /**
     * 根据视频会议号查询主表记录
     */
    MeetingAiWorkflowInfoModel getByVideoMeetingNo(String videoMeetingNo);

    /**
     * 根据日程 ID 与视频会议号回写云文档 ID
     *
     * @return 是否更新成功
     */
    boolean updateCloudDocId(String scheduleId, String videoMeetingNo, String cloudDocId);

}
