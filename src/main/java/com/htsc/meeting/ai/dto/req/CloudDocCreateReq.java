package com.htsc.meeting.ai.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 创建云文档请求
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudDocCreateReq {

    /** 存储网关 / S3 文件 ID */
    private String fileStoreId;

    /** 1-存储网关；2-S3 */
    private Integer fileChannel;

    /** 文件名称，带后缀 */
    private String fileName;

    /** 文档名称，不带后缀 */
    private String documentName;

    /** 创建人唯一标识 */
    private String createdBy;

    /** 文档默认访问权限：W-可编辑；R-可预览；D-无权限 */
    private String defaultRoleCode;

    /** 协作人列表 */
    private List<CloudDocCollaborator> collaborators;

}
