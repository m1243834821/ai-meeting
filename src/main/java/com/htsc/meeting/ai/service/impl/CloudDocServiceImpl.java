package com.htsc.meeting.ai.service.impl;

import com.htsc.meeting.ai.client.CloudDocClient;
import com.htsc.meeting.ai.common.config.MeetingAiProperties;
import com.htsc.meeting.ai.common.util.FileNameUtils;
import com.htsc.meeting.ai.dto.req.CloudDocCreateReq;
import com.htsc.meeting.ai.dto.resp.CloudDocCreateResp;
import com.htsc.meeting.ai.dto.resp.CloudDocUploadResp;
import com.htsc.meeting.ai.service.CloudDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 云文档服务实现
 */
@Slf4j
@Service("cloudDocService")
public class CloudDocServiceImpl implements CloudDocService {

    @Autowired
    private CloudDocClient cloudDocClient;

    @Autowired
    private MeetingAiProperties properties;

    @Override
    public CloudDocUploadResp upload(byte[] content, String fileName, String contentType) {
        return cloudDocClient.uploadFile(content, fileName, contentType);
    }

    @Override
    public CloudDocCreateResp createDocument(String fileStoreId, String fileName, String createdBy) {
        CloudDocCreateReq createReq = new CloudDocCreateReq();
        createReq.setFileStoreId(fileStoreId);
        createReq.setFileChannel(properties.getCloudDoc().getFileChannel());
        createReq.setFileName(fileName);
        createReq.setDocumentName(FileNameUtils.removeExtension(fileName));
        createReq.setCreatedBy(createdBy);

        String defaultRoleCode = properties.getCloudDoc().getDefaultRoleCode();
        if (defaultRoleCode != null && !defaultRoleCode.trim().isEmpty()) {
            createReq.setDefaultRoleCode(defaultRoleCode);
        }
        return cloudDocClient.createDocument(createReq);
    }

}
