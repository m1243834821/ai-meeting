package com.htsc.meeting.ai.dto.req;

import lombok.Data;

import java.util.List;

/**
 * 低代码纪要确认流程入参（本期占位）
 */
@Data
public class MinutesConfirmReq {

    /** 追踪 ID */
    private String traceId;

    /** 日程 ID */
    private String scheduleId;

    /** 会议中心 ID */
    private String meetingCenterId;

    /** 会议 code */
    private String meetingCode;

    /** 创建人工号 */
    private String creatorUserId;

    /** 会议主题 */
    private String subject;

    /** 云文档 ID 列表 */
    private List<String> cloudDocIds;

    /** 存储网关文件 ID 列表 */
    private List<String> fileStoreIds;

}
