package com.htsc.meeting.ai.dto.req;

import lombok.Data;

/** 第三阶段任务中心链接回填；objectId为第一阶段创建的主表数据ID。 */
@Data
public class TaskCenterLinkReq {
    private String modelCode;
    private String objectId;
    private String userId;
    private String taskCenterLink;
}
