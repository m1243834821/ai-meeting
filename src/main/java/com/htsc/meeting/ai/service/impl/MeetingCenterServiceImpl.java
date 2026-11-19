package com.htsc.meeting.ai.service.impl;

import com.htsc.meeting.ai.client.MeetingCenterClient;
import com.htsc.meeting.ai.dto.req.MeetingFileItem;
import com.htsc.meeting.ai.dto.req.MeetingFileUploadReq;
import com.htsc.meeting.ai.service.MeetingCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会议中心服务实现
 */
@Slf4j
@Service("meetingCenterService")
public class MeetingCenterServiceImpl implements MeetingCenterService {

    @Autowired
    private MeetingCenterClient meetingCenterClient;

    @Override
    public void uploadMeetingFiles(String meetingCenterId, String userCode, List<MeetingFileItem> files) {
        MeetingFileUploadReq uploadReq = new MeetingFileUploadReq();
        uploadReq.setMeetingId(meetingCenterId);
        uploadReq.setUserCode(userCode);
        uploadReq.setMeetingFile(files);
        meetingCenterClient.uploadMeetingFile(uploadReq);
    }

}
