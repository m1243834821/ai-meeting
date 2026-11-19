package com.htsc.meeting.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htsc.meeting.ai.common.config.MeetingAiProperties;
import com.htsc.meeting.ai.common.exception.MeetingAiException;
import com.htsc.meeting.ai.dto.req.MeetingFileUploadReq;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 会议中心接口客户端
 */
@Slf4j
@Component
public class MeetingCenterClient {

    private static final MediaType MEDIA_TYPE_JSON =
            MediaType.parse("application/json;charset=UTF-8");

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private MeetingAiProperties properties;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 上传会议纪要到会议中心
     */
    public void uploadMeetingFile(MeetingFileUploadReq uploadReq) {
        MeetingAiProperties.MeetingCenter meetingCenter = properties.getMeetingCenter();
        String url = joinUrl(meetingCenter.getBaseUrl(), meetingCenter.getUploadPath());

        Request request;
        try {
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON,
                    objectMapper.writeValueAsString(uploadReq).getBytes(StandardCharsets.UTF_8));
            request = new Request.Builder()
                    .url(url)
                    .addHeader("token", nullToEmpty(meetingCenter.getToken()))
                    .addHeader("appid", nullToEmpty(meetingCenter.getAppId()))
                    .addHeader("userid", nullToEmpty(meetingCenter.getUserId()))
                    .post(body)
                    .build();
        } catch (IOException e) {
            throw new MeetingAiException("10030", "会议中心上传请求序列化失败", e);
        }

        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String bodyText = responseBody == null ? "" : responseBody.string();
            if (!response.isSuccessful()) {
                throw new MeetingAiException("10031",
                        "会议中心上传纪要失败，httpStatus:" + response.code() + ", body:" + truncate(bodyText));
            }
            checkBusinessCode(bodyText);
            log.info("meeting center upload success, meetingId:{}, fileSize:{}",
                    uploadReq.getMeetingId(), uploadReq.getMeetingFile() == null ? 0 : uploadReq.getMeetingFile().size());
        } catch (MeetingAiException e) {
            throw e;
        } catch (IOException e) {
            throw new MeetingAiException("10032", "会议中心上传纪要调用异常", e);
        }
    }

    /**
     * 会议中心返回结构未最终确定：仅当响应中明确带 code 且非成功值时判为失败，
     * 避免因返回结构差异误判为异常。
     */
    private void checkBusinessCode(String bodyText) {
        if (bodyText == null || bodyText.trim().isEmpty()) {
            return;
        }
        try {
            JsonNode root = objectMapper.readTree(bodyText);
            JsonNode codeNode = root.get("code");
            if (codeNode == null || codeNode.isNull()) {
                return;
            }
            String code = codeNode.asText();
            boolean success = "0".equals(code) || "200".equals(code) || "success".equalsIgnoreCase(code);
            if (!success) {
                throw new MeetingAiException("10033",
                        "会议中心上传纪要业务失败，code:" + code + ", body:" + truncate(bodyText));
            }
        } catch (MeetingAiException e) {
            throw e;
        } catch (Exception e) {
            // 非 JSON 响应视为成功，仅记录日志
            log.warn("meeting center response is not json, body:{}", truncate(bodyText));
        }
    }

    private String joinUrl(String baseUrl, String path) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new MeetingAiException("10034", "会议中心服务地址 meeting.ai.meeting-center.base-url 未配置");
        }
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }

    private String truncate(String text) {
        if (text == null || text.length() <= 500) {
            return text;
        }
        return text.substring(0, 500);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

}
