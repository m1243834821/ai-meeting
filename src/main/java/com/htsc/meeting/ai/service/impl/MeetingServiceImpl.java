package com.htsc.meeting.ai.service.impl;

import com.htsc.meeting.ai.dto.req.MeetingCreateReq;
import com.htsc.meeting.ai.dto.resp.MeetingCreateResp;
import com.htsc.meeting.ai.entity.MeetingInfoModel;
import com.htsc.meeting.ai.entity.MeetingParticipantModel;
import com.htsc.meeting.ai.mapper.MeetingMapper;
import com.htsc.meeting.ai.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 会议服务实现
 */
@Slf4j
@Service("meetingService")
public class MeetingServiceImpl implements MeetingService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MeetingMapper meetingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MeetingCreateResp create(MeetingCreateReq request) {
        validate(request);

        MeetingInfoModel meeting = new MeetingInfoModel();
        meeting.setMeetingName(request.getMeetingName().trim());
        meeting.setType(request.getType().trim());
        meeting.setCreateTime(parseDateTime(request.getCreateTime(), "createTime", true));
        meeting.setMeetingType(request.getMeetingType().trim());
        meeting.setCreatorEmpNo(request.getCreatorEmpNo().trim());
        meeting.setScheduleId(request.getScheduleId().trim());
        meeting.setMeetingCenterId(request.getMeetingCenterId().trim());
        meeting.setSessionId(request.getSessionId().trim());
        meeting.setMeetingContent(request.getMeetingContent());
        meeting.setVideoMeetingNo(request.getVideoMeetingNo().trim());
        meeting.setCreatorDeptCode(request.getCreatorDeptCode().trim());
        meeting.setMeetingRoom(request.getMeetingRoom());
        meeting.setStartTime(parseDateTime(request.getStartTime(), "startTime", false));
        meeting.setEndTime(parseDateTime(request.getEndTime(), "endTime", false));
        meeting.setCreatorName(request.getCreatorName());
        meeting.setCreatorDept(request.getCreatorDept());
        meeting.setCreatorPosition(request.getCreatorPosition());

        if (meetingMapper.insertMeeting(meeting) != 1 || meeting.getId() == null) {
            throw new IllegalStateException("会议记录创建失败");
        }

        if (request.getParticipantList() != null && !request.getParticipantList().isEmpty()) {
            List<MeetingParticipantModel> participants = new ArrayList<>();
            for (MeetingCreateReq.ParticipantReq item : request.getParticipantList()) {
                MeetingParticipantModel participant = new MeetingParticipantModel();
                participant.setMeetingId(meeting.getId());
                participant.setMeetingCenterId(meeting.getMeetingCenterId());
                participant.setEmpNo(item.getEmpNo().trim());
                participant.setName(item.getName());
                participant.setDept(item.getDept());
                participant.setDeptCode(item.getDeptCode());
                participant.setPosition(item.getPosition());
                participants.add(participant);
            }
            if (meetingMapper.insertParticipants(participants) != participants.size()) {
                throw new IllegalStateException("参会人记录创建失败");
            }
        }

        MeetingCreateResp response = new MeetingCreateResp();
        response.setId(meeting.getId());
        return response;
    }

    private void validate(MeetingCreateReq request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        require(request.getMeetingName(), "meetingName");
        require(request.getType(), "type");
        require(request.getCreateTime(), "createTime");
        require(request.getMeetingType(), "meetingType");
        require(request.getCreatorEmpNo(), "creatorEmpNo");
        require(request.getCreatorDeptCode(), "creatorDeptCode");
        require(request.getScheduleId(), "scheduleId");
        require(request.getMeetingCenterId(), "meetingCenterId");
        require(request.getSessionId(), "sessionId");
        require(request.getVideoMeetingNo(), "videoMeetingNo");
        if (!"TENCENT".equalsIgnoreCase(request.getMeetingType())
                && !"FEISHU".equalsIgnoreCase(request.getMeetingType())) {
            throw new IllegalArgumentException("meetingType 仅支持 TENCENT 或 FEISHU");
        }
        parseDateTime(request.getCreateTime(), "createTime", true);
        parseDateTime(request.getStartTime(), "startTime", false);
        parseDateTime(request.getEndTime(), "endTime", false);
        if (request.getParticipantList() != null) {
            for (int i = 0; i < request.getParticipantList().size(); i++) {
                MeetingCreateReq.ParticipantReq participant = request.getParticipantList().get(i);
                if (participant == null) {
                    throw new IllegalArgumentException("participantList[" + i + "] 不能为空");
                }
                require(participant.getEmpNo(), "participantList[" + i + "].empNo");
                //require(participant.getDept(), "participantList[" + i + "].dept");
                require(participant.getDeptCode(), "participantList[" + i + "].deptCode");
            }
        }
    }

    private void require(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " 不能为空");
        }
    }

    private LocalDateTime parseDateTime(String value, String field, boolean required) {
        if (value == null || value.trim().isEmpty()) {
            if (required) {
                throw new IllegalArgumentException(field + " 不能为空");
            }
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(field + " 格式必须为 yyyy-MM-dd HH:mm:ss");
        }
    }

}
