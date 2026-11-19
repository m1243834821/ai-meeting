package com.htsc.meeting.ai.service.impl;

import com.htsc.boot.common.model.Response;
import com.htsc.link.calendar.core.openapi.api.OpenApiTencentMeetingRecordService;
import com.htsc.link.calendar.core.openapi.dto.tencentmeeting.record.OpenApiTencentMeetingRecordDetailRequest;
import com.htsc.link.calendar.core.openapi.dto.tencentmeeting.record.OpenApiTencentMeetingRecordDetailResponse;
import com.htsc.meeting.ai.common.config.MeetingAiProperties;
import com.htsc.meeting.ai.common.exception.MeetingAiException;
import com.htsc.meeting.ai.service.TencentMeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 腾讯会议录制详情服务实现（dubbo 调用日程开放平台）
 */
@Slf4j
@Service("tencentMeetingRecordService")
public class TencentMeetingRecordServiceImpl implements TencentMeetingRecordService {

    @Autowired
    private OpenApiTencentMeetingRecordService openApiTencentMeetingRecordService;

    @Autowired
    private MeetingAiProperties properties;

    @Override
    public OpenApiTencentMeetingRecordDetailResponse detail(String recordFileId, String userId) {
        if (openApiTencentMeetingRecordService == null) {
            throw new MeetingAiException("10040", "录制详情 dubbo 服务未就绪，请检查 dubbo-consumer.xml 与注册中心");
        }

        OpenApiTencentMeetingRecordDetailRequest request = new OpenApiTencentMeetingRecordDetailRequest();
        request.setTenantId(properties.getTencent().getTenantId());
        request.setSecret(properties.getTencent().getSecret());
        request.setRecord_file_id(recordFileId);
        request.setUserid(userId);

        Response<OpenApiTencentMeetingRecordDetailResponse> response =
                openApiTencentMeetingRecordService.detail(request);
        if (response == null) {
            throw new MeetingAiException("10041", "查询录制详情返回为空，recordFileId:" + recordFileId);
        }
        if (response.hasError()) {
            throw new MeetingAiException("10042",
                    "查询录制详情失败，recordFileId:" + recordFileId
                            + ", code:" + response.getCode() + ", msg:" + response.getMsg());
        }
        OpenApiTencentMeetingRecordDetailResponse detail = response.getResultData();
        if (detail == null) {
            throw new MeetingAiException("10043", "查询录制详情无数据，recordFileId:" + recordFileId);
        }
        return detail;
    }

}
