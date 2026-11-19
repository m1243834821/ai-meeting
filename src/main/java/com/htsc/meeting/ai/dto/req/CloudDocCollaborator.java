package com.htsc.meeting.ai.dto.req;

import lombok.Data;

/**
 * 云文档协作人
 */
@Data
public class CloudDocCollaborator {

    /** 协作人唯一标识 */
    private String collaboratorId;

    /** 协作人类型：1-个人用户；4-组织架构 */
    private Integer collaboratorType;

    /** 协作人展示名称 */
    private String collaboratorName;

    /** 访问权限：W-可编辑；R-可预览；D-无权限 */
    private String roleCode;

}
