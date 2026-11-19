package com.htsc.meeting.ai.dto.req;

import lombok.Data;

import java.util.List;

/**
 * 会议中心上传会议纪要请求
 */
@Data
public class MeetingFileUploadReq {

    /** 当前操作人工号 */
    private String userCode;

    /** 会议中心 ID，tyhy 开头 */
    private String meetingId;

    /** 上传的会议纪要列表 */
    private List<MeetingFileItem> meetingFile;

}
