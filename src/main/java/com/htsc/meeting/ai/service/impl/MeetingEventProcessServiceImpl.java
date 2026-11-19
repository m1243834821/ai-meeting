package com.htsc.meeting.ai.service.impl;

import com.htsc.link.calendar.core.openapi.dto.tencentmeeting.common.Minutes;
import com.htsc.link.calendar.core.openapi.dto.tencentmeeting.record.OpenApiTencentMeetingRecordDetailResponse;
import com.htsc.meeting.ai.client.FileDownloadClient;
import com.htsc.meeting.ai.common.config.MeetingAiProperties;
import com.htsc.meeting.ai.common.constant.MeetingAiConstants;
import com.htsc.meeting.ai.common.util.AesUtil;
import com.htsc.meeting.ai.common.util.EventJsonUtils;
import com.htsc.meeting.ai.common.util.FileNameUtils;
import com.htsc.meeting.ai.common.util.MinutesExtractor;
import com.htsc.meeting.ai.dto.event.MeetingCreator;
import com.htsc.meeting.ai.dto.event.MeetingEventMessage;
import com.htsc.meeting.ai.dto.event.MeetingEventPayload;
import com.htsc.meeting.ai.dto.event.MeetingRecordingEvent;
import com.htsc.meeting.ai.dto.event.RecordingFileItem;
import com.htsc.meeting.ai.dto.event.TencentMeetingInfo;
import com.htsc.meeting.ai.dto.req.MeetingFileItem;
import com.htsc.meeting.ai.dto.req.MinutesConfirmReq;
import com.htsc.meeting.ai.dto.resp.CloudDocCreateResp;
import com.htsc.meeting.ai.dto.resp.CloudDocUploadResp;
import com.htsc.meeting.ai.entity.MeetingAiWorkflowInfoModel;
import com.htsc.meeting.ai.service.CloudDocService;
import com.htsc.meeting.ai.service.MeetingAiWorkflowService;
import com.htsc.meeting.ai.service.MeetingCenterService;
import com.htsc.meeting.ai.service.MeetingEventProcessService;
import com.htsc.meeting.ai.service.MinutesConfirmService;
import com.htsc.meeting.ai.service.TencentMeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯会议录制完成事件处理编排。
 * <p>
 * 处理链路：解密 → 过滤 → 汇总同一会议 → 查主表 → 查录制详情 → 下载纪要 → 上传云文档 → 写入会议中心 → 回写主表。
 * 各层级独立 try-catch：单条消息失败只记录日志，不阻塞批次；部分录制文件失败时不回写主表，避免"假完成"。
 */
@Slf4j
@Service("meetingEventProcessService")
public class MeetingEventProcessServiceImpl implements MeetingEventProcessService {

    @Autowired
    private MeetingAiProperties properties;

    @Autowired
    private MeetingAiWorkflowService meetingAiWorkflowService;

    @Autowired
    private TencentMeetingRecordService tencentMeetingRecordService;

    @Autowired
    private CloudDocService cloudDocService;

    @Autowired
    private MeetingCenterService meetingCenterService;

    @Autowired
    private MinutesConfirmService minutesConfirmService;

    @Autowired
    private FileDownloadClient fileDownloadClient;

    @Override
    public void process(String rawMessage) {
        MeetingEventMessage message = EventJsonUtils.parseMessage(rawMessage);
        if (!properties.getTencent().getTenantId().equals(message.getTenantId())) {
            log.info("skip message of other tenant, tenantId:{}", message.getTenantId());
            return;
        }

        String plainText = AesUtil.decrypt(message.getData(), properties.getTencent().getSecret());
        MeetingRecordingEvent event = EventJsonUtils.parseEvent(plainText);
        if (!MeetingAiConstants.EVENT_RECORDING_COMPLETED.equals(event.getEvent())) {
            log.info("skip unsupported event, event:{}, traceId:{}", event.getEvent(), event.getTraceId());
            return;
        }
        if (event.getPayload() == null || event.getPayload().isEmpty()) {
            log.warn("recording.completed payload is empty, traceId:{}", event.getTraceId());
            return;
        }

        // 同一事件内可能有多条 payload 属于同一个会议，合并后统一处理
        Map<String, List<MeetingEventPayload>> meetingPayloadMap = groupByMeetingCode(event.getPayload());
        for (Map.Entry<String, List<MeetingEventPayload>> entry : meetingPayloadMap.entrySet()) {
            try {
                processMeeting(entry.getKey(), entry.getValue(), event.getTraceId());
            } catch (Exception e) {
                log.error("process meeting failed, traceId:{}, meetingCode:{}",
                        event.getTraceId(), entry.getKey(), e);
            }
        }
    }

    private Map<String, List<MeetingEventPayload>> groupByMeetingCode(List<MeetingEventPayload> payloads) {
        Map<String, List<MeetingEventPayload>> meetingPayloadMap = new LinkedHashMap<>();
        for (MeetingEventPayload payload : payloads) {
            if (payload == null || payload.getMeetingInfo() == null) {
                log.warn("meeting info is empty, skip payload");
                continue;
            }
            String meetingCode = payload.getMeetingInfo().getMeetingCode();
            if (!StringUtils.hasText(meetingCode)) {
                log.warn("meeting code is empty, skip payload");
                continue;
            }
            meetingPayloadMap.computeIfAbsent(meetingCode.trim(), key -> new ArrayList<>()).add(payload);
        }
        return meetingPayloadMap;
    }

    private void processMeeting(String meetingCode, List<MeetingEventPayload> payloads, String traceId) {
        TencentMeetingInfo meetingInfo = payloads.get(0).getMeetingInfo();
        MeetingCreator creator = meetingInfo.getCreator();
        if (creator == null || !StringUtils.hasText(creator.getUserid())) {
            log.warn("creator userid is empty, traceId:{}, meetingCode:{}", traceId, meetingCode);
            return;
        }
        String userId = creator.getUserid().trim();

        List<RecordingFileItem> recordingFiles = collectRecordingFiles(payloads);
        if (recordingFiles.isEmpty()) {
            log.warn("recording files is empty, traceId:{}, meetingCode:{}", traceId, meetingCode);
            return;
        }

        MeetingAiWorkflowInfoModel workflowInfo = meetingAiWorkflowService.getByVideoMeetingNo(meetingCode);
        if (workflowInfo == null) {
            log.warn("workflow info not found, traceId:{}, meetingCode:{}", traceId, meetingCode);
            return;
        }
        // 已生成过云文档直接跳过
        if (StringUtils.hasText(workflowInfo.getCloudDocId())) {
            log.info("cloud doc already generated, skip. traceId:{}, meetingCode:{}, cloudDocId:{}",
                    traceId, meetingCode, workflowInfo.getCloudDocId());
            return;
        }
        if (!StringUtils.hasText(workflowInfo.getScheduleId())) {
            log.warn("schedule id is empty, traceId:{}, meetingCode:{}", traceId, meetingCode);
            return;
        }
        if (!StringUtils.hasText(workflowInfo.getMeetingCenterId())) {
            log.warn("meeting center id is empty, traceId:{}, meetingCode:{}", traceId, meetingCode);
            return;
        }

        List<MeetingFileItem> meetingFiles = new ArrayList<>();
        List<String> fileStoreIds = new ArrayList<>();
        List<String> cloudDocIds = new ArrayList<>();
        int[] minutesIndex = {0};
        int failedCount = 0;

        for (RecordingFileItem recordingFile : recordingFiles) {
            String recordFileId = recordingFile.getRecordFileId().trim();
            try {
                processRecordFile(recordFileId, meetingInfo, userId, minutesIndex,
                        meetingFiles, fileStoreIds, cloudDocIds);
            } catch (Exception e) {
                failedCount++;
                // 单个录制文件失败不影响其他录制文件，但成功后也不会回写主表
                log.error("process record file failed, traceId:{}, meetingCode:{}, recordFileId:{}",
                        traceId, meetingCode, recordFileId, e);
            }
        }

        if (meetingFiles.isEmpty()) {
            log.warn("no minutes file generated, traceId:{}, meetingCode:{}", traceId, meetingCode);
            return;
        }

        // 会议中心上传失败直接抛出，由上层记录日志，且不回写主表，保留人工重放机会
        meetingCenterService.uploadMeetingFiles(workflowInfo.getMeetingCenterId(), userId, meetingFiles);

        createConfirmFlow(traceId, workflowInfo, meetingInfo, userId, cloudDocIds, fileStoreIds);

        // 存在失败录制文件时不回写主表，避免"部分成功"被误判为已完成而永久跳过
        if (failedCount > 0) {
            log.error("partial record files failed, skip cloud doc id update for manual replay. "
                            + "traceId:{}, meetingCode:{}, failedCount:{}, successFileCount:{}",
                    traceId, meetingCode, failedCount, meetingFiles.size());
            return;
        }

        String cloudDocId = buildCloudDocId(cloudDocIds);
        meetingAiWorkflowService.updateCloudDocId(workflowInfo.getScheduleId(), meetingCode, cloudDocId);
    }

    /**
     * 合并同一会议下所有 payload 的录制文件，按 record_file_id 去重
     */
    private List<RecordingFileItem> collectRecordingFiles(List<MeetingEventPayload> payloads) {
        Map<String, RecordingFileItem> recordingFileMap = new LinkedHashMap<>();
        for (MeetingEventPayload payload : payloads) {
            if (payload.getRecordingFiles() == null) {
                continue;
            }
            for (RecordingFileItem recordingFile : payload.getRecordingFiles()) {
                if (recordingFile == null || !StringUtils.hasText(recordingFile.getRecordFileId())) {
                    continue;
                }
                recordingFileMap.putIfAbsent(recordingFile.getRecordFileId().trim(), recordingFile);
            }
        }
        return new ArrayList<>(recordingFileMap.values());
    }

    private void processRecordFile(String recordFileId, TencentMeetingInfo meetingInfo, String userId,
                                   int[] minutesIndex, List<MeetingFileItem> meetingFiles,
                                   List<String> fileStoreIds, List<String> cloudDocIds) {
        OpenApiTencentMeetingRecordDetailResponse detail =
                tencentMeetingRecordService.detail(recordFileId, userId);
        List<Minutes> minutesList = MinutesExtractor.extract(detail);
        if (minutesList.isEmpty()) {
            log.info("no downloadable ai minutes, recordFileId:{}", recordFileId);
            return;
        }

        for (Minutes minutes : minutesList) {
            // 使用跨录制文件递增的序号，避免多个录制文件生成同名纪要
            String fileName = FileNameUtils.buildFileName(
                    meetingInfo.getSubject(), minutes.getFile_type(), minutesIndex[0]++);
            // 下载地址 5 分钟有效，拿到后立即下载
            byte[] content = fileDownloadClient.download(minutes.getDownload_address());
            CloudDocUploadResp uploadResp = cloudDocService.upload(
                    content, fileName, resolveContentType(minutes.getFile_type()));
            CloudDocCreateResp createResp = cloudDocService.createDocument(
                    uploadResp.getFileStoreId(), fileName, userId);

            fileStoreIds.add(uploadResp.getFileStoreId());
            cloudDocIds.add(createResp.getFileCrdcId());
            meetingFiles.add(buildMeetingFileItem(uploadResp.getFileStoreId(), fileName, userId));
            log.info("minutes processed, recordFileId:{}, fileName:{}, fileStoreId:{}, fileCrdcId:{}",
                    recordFileId, fileName, uploadResp.getFileStoreId(), createResp.getFileCrdcId());
        }
    }

    private void createConfirmFlow(String traceId, MeetingAiWorkflowInfoModel workflowInfo,
                                   TencentMeetingInfo meetingInfo, String userId,
                                   List<String> cloudDocIds, List<String> fileStoreIds) {
        // 低代码流程本期占位，失败不影响主流程
        try {
            MinutesConfirmReq confirmReq = new MinutesConfirmReq();
            confirmReq.setTraceId(traceId);
            confirmReq.setScheduleId(workflowInfo.getScheduleId());
            confirmReq.setMeetingCenterId(workflowInfo.getMeetingCenterId());
            confirmReq.setMeetingCode(meetingInfo.getMeetingCode());
            confirmReq.setCreatorUserId(userId);
            confirmReq.setSubject(meetingInfo.getSubject());
            confirmReq.setCloudDocIds(cloudDocIds);
            confirmReq.setFileStoreIds(fileStoreIds);
            String confirmFlowId = minutesConfirmService.createConfirmFlow(confirmReq);
            if (StringUtils.hasText(confirmFlowId)) {
                // TODO 低代码接口确定后，与 CLOUD_DOC_ID 在同一事务中更新 MINUTES_CONFIRM_LC_ID
                log.info("minutes confirm flow created, traceId:{}, confirmFlowId:{}", traceId, confirmFlowId);
            }
        } catch (Exception e) {
            log.error("create minutes confirm flow failed, ignored, traceId:{}", traceId, e);
        }
    }

    /**
     * 拼接云文档 ID，超过字段长度时保留完整 ID 并告警，避免截断产生脏数据
     */
    private String buildCloudDocId(List<String> cloudDocIds) {
        StringBuilder builder = new StringBuilder();
        for (String cloudDocId : cloudDocIds) {
            int neededLength = builder.length() == 0
                    ? cloudDocId.length()
                    : builder.length() + MeetingAiConstants.CLOUD_DOC_ID_SEPARATOR.length() + cloudDocId.length();
            if (neededLength > MeetingAiConstants.MAX_CLOUD_DOC_ID_LENGTH) {
                log.warn("cloud doc id exceeds max length {}, totalCount:{}, keep:{}",
                        MeetingAiConstants.MAX_CLOUD_DOC_ID_LENGTH, cloudDocIds.size(),
                        builder.length() == 0 ? 0 : builder.toString().split(",").length);
                break;
            }
            if (builder.length() > 0) {
                builder.append(MeetingAiConstants.CLOUD_DOC_ID_SEPARATOR);
            }
            builder.append(cloudDocId);
        }
        return builder.toString();
    }

    private MeetingFileItem buildMeetingFileItem(String gatewayId, String fileName, String userId) {
        MeetingFileItem item = new MeetingFileItem();
        item.setFileType(MeetingAiConstants.FILE_TYPE_MINUTES);
        item.setUploaderUserCode(userId);
        item.setUploaderDeptCode(properties.getMeetingCenter().getUploaderDeptCode());
        item.setFileName(fileName);
        item.setGatewayId(gatewayId);
        item.setCanRead(MeetingAiConstants.CAN_READ_YES);
        item.setReadScope(MeetingAiConstants.READ_SCOPE_ALL);
        item.setCanDownload(MeetingAiConstants.CAN_DOWNLOAD_YES);
        return item;
    }

    private String resolveContentType(String fileType) {
        if (!StringUtils.hasText(fileType)) {
            return "application/octet-stream";
        }
        switch (fileType.trim().toLowerCase()) {
            case "txt":
                return "text/plain";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "application/octet-stream";
        }
    }

}
