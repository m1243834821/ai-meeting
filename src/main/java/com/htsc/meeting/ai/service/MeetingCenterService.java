package com.htsc.meeting.ai.service;

import com.htsc.meeting.ai.dto.req.MeetingFileItem;

import java.util.List;

/**
 * 会议中心服务
 */
public interface MeetingCenterService {

    /**
     * 上传会议纪要到会议中心
     *
     * @param meetingCenterId 会议中心 ID
     * @param userCode        操作人工号
     * @param files           纪要文件列表
     */
    void uploadMeetingFiles(String meetingCenterId, String userCode, List<MeetingFileItem> files);

}
