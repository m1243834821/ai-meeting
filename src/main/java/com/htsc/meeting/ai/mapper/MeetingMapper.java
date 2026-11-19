package com.htsc.meeting.ai.mapper;

import com.htsc.meeting.ai.entity.MeetingInfoModel;
import com.htsc.meeting.ai.entity.MeetingParticipantModel;

import java.util.List;

/**
 * 会议创建数据访问
 */
public interface MeetingMapper {

    int insertMeeting(MeetingInfoModel meeting);

    int insertParticipants(@org.apache.ibatis.annotations.Param("list") List<MeetingParticipantModel> participants);
}
