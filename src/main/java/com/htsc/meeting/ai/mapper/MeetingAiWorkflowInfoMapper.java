package com.htsc.meeting.ai.mapper;

import com.htsc.meeting.ai.entity.MeetingAiWorkflowInfoModel;
import org.apache.ibatis.annotations.Param;

/**
 * 会议流程 AI 化主表 Mapper
 */
public interface MeetingAiWorkflowInfoMapper {

    /**
     * 根据视频会议号查询主表记录
     *
     * @param videoMeetingNo 视频会议号（腾讯会议 meeting_code）
     * @return 主表记录，不存在返回 null
     */
    MeetingAiWorkflowInfoModel selectByVideoMeetingNo(@Param("videoMeetingNo") String videoMeetingNo);

    /**
     * 根据日程 ID + 视频会议号回写云文档 ID。
     * 仅当 CLOUD_DOC_ID 为空时更新，避免并发/重复消费覆盖已生成的数据；
     * 同时带 VIDEO_MEETING_NO 条件，避免同一日程下多次会议互相覆盖。
     *
     * @param scheduleId     日程 ID
     * @param videoMeetingNo 视频会议号
     * @param cloudDocId     云文档 ID
     * @return 影响行数
     */
    int updateCloudDocIdByScheduleId(@Param("scheduleId") String scheduleId,
                                     @Param("videoMeetingNo") String videoMeetingNo,
                                     @Param("cloudDocId") String cloudDocId);

}
