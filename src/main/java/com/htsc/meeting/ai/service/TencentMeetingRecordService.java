package com.htsc.meeting.ai.service;

import com.htsc.link.calendar.core.openapi.dto.tencentmeeting.record.OpenApiTencentMeetingRecordDetailResponse;

/**
 * 腾讯会议录制详情服务
 */
public interface TencentMeetingRecordService {

    /**
     * 查询单个录制详情
     *
     * @param recordFileId 云录制文件 ID
     * @param userId       创建人工号
     * @return 录制详情（含 ai_minutes）
     */
    OpenApiTencentMeetingRecordDetailResponse detail(String recordFileId, String userId);

}
