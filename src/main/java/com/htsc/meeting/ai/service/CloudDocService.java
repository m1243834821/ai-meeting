package com.htsc.meeting.ai.service;

import com.htsc.meeting.ai.dto.resp.CloudDocCreateResp;
import com.htsc.meeting.ai.dto.resp.CloudDocUploadResp;

/**
 * 云文档服务
 */
public interface CloudDocService {

    /**
     * 上传文件到云文档存储网关
     */
    CloudDocUploadResp upload(byte[] content, String fileName, String contentType);

    /**
     * 创建云文档
     *
     * @param fileStoreId 上传返回的文件存储 ID
     * @param fileName    带后缀的文件名
     * @param createdBy   创建人工号
     */
    CloudDocCreateResp createDocument(String fileStoreId, String fileName, String createdBy);

}
