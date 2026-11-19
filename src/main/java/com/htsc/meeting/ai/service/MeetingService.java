package com.htsc.meeting.ai.service;

import com.htsc.meeting.ai.dto.req.MeetingCreateReq;
import com.htsc.meeting.ai.dto.resp.MeetingCreateResp;

/**
 * 会议服务
 */
public interface MeetingService {

    MeetingCreateResp create(MeetingCreateReq request);

}
