package com.htsc.meeting.ai.service.impl;

import com.htsc.meeting.ai.entity.MeetingAiWorkflowInfoModel;
import com.htsc.meeting.ai.mapper.MeetingAiWorkflowInfoMapper;
import com.htsc.meeting.ai.service.MeetingAiWorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会议流程 AI 化主表服务实现
 */
@Slf4j
@Service("meetingAiWorkflowService")
public class MeetingAiWorkflowServiceImpl implements MeetingAiWorkflowService {

    @Autowired
    private MeetingAiWorkflowInfoMapper meetingAiWorkflowInfoMapper;

    @Override
    public MeetingAiWorkflowInfoModel getByVideoMeetingNo(String videoMeetingNo) {
        return meetingAiWorkflowInfoMapper.selectByVideoMeetingNo(videoMeetingNo);
    }

    /**
     * 事务只包 DB 更新，外部远程调用均在本方法之前完成。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCloudDocId(String scheduleId, String videoMeetingNo, String cloudDocId) {
        if (scheduleId == null || scheduleId.trim().isEmpty()
                || videoMeetingNo == null || videoMeetingNo.trim().isEmpty()
                || cloudDocId == null || cloudDocId.trim().isEmpty()) {
            log.warn("skip update cloud doc id, scheduleId:{}, videoMeetingNo:{}, cloudDocId:{}",
                    scheduleId, videoMeetingNo, cloudDocId);
            return false;
        }
        int updated = meetingAiWorkflowInfoMapper.updateCloudDocIdByScheduleId(
                scheduleId, videoMeetingNo, cloudDocId);
        if (updated <= 0) {
            log.warn("cloud doc id not updated, scheduleId:{} maybe already processed", scheduleId);
            return false;
        }
        log.info("update cloud doc id success, scheduleId:{}, videoMeetingNo:{}, cloudDocId:{}",
                scheduleId, videoMeetingNo, cloudDocId);
        return true;
    }

}
