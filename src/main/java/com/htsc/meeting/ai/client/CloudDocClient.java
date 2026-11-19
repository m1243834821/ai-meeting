package com.htsc.meeting.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.htsc.meeting.ai.common.config.MeetingAiProperties;
import com.htsc.meeting.ai.common.exception.MeetingAiException;
import com.htsc.meeting.ai.dto.req.CloudDocCreateReq;
import com.htsc.meeting.ai.dto.resp.CloudDocCreateResp;
import com.htsc.meeting.ai.dto.resp.CloudDocUploadResp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
 * 云文档开放接口客户端（先上传文件，再创建云文档）
 */
@Slf4j
@Component
public class CloudDocClient {

    private static final MediaType MEDIA_TYPE_JSON =
            MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_OCTET_STREAM =
            MediaType.parse("application/octet-stream");

    private static final String HEADER_APP_ID = "CD-APP-ID";
    private static final String HEADER_APP_SECRET = "CD-APP-SECRET";

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private MeetingAiProperties properties;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 上传纪要文件到云文档存储网关
     *
     * @param content     文件内容
     * @param fileName    带后缀的文件名
     * @param contentType 文件 MIME 类型，可为空
     */
    public CloudDocUploadResp uploadFile(byte[] content, String fileName, String contentType) {
        MeetingAiProperties.CloudDoc cloudDoc = properties.getCloudDoc();
        String url = joinUrl(cloudDoc.getBaseUrl(), cloudDoc.getUploadPath());

        MediaType mediaType = isBlank(contentType) ? MEDIA_TYPE_OCTET_STREAM : MediaType.parse(contentType);
        RequestBody fileBody = RequestBody.create(mediaType, content);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader(HEADER_APP_ID, nullToEmpty(cloudDoc.getAppId()))
                .addHeader(HEADER_APP_SECRET, nullToEmpty(cloudDoc.getAppSecret()))
                .post(body)
                .build();

        JsonNode root = execute(request, "云文档文件上传");
        String fileStoreId = extractField(root, "fileStoreId");
        if (isBlank(fileStoreId)) {
            throw new MeetingAiException("10020",
                    "云文档文件上传未返回 fileStoreId，response:" + truncate(String.valueOf(root)));
        }
        CloudDocUploadResp resp = new CloudDocUploadResp();
        resp.setFileStoreId(fileStoreId);
        log.info("cloud doc upload file success, fileName:{}, fileStoreId:{}", fileName, fileStoreId);
        return resp;
    }

    /**
     * 创建云文档
     *
     * @return 云文档唯一 ID
     */
    public CloudDocCreateResp createDocument(CloudDocCreateReq createReq) {
        MeetingAiProperties.CloudDoc cloudDoc = properties.getCloudDoc();
        String url = joinUrl(cloudDoc.getBaseUrl(), cloudDoc.getCreatePath());
        Request request;
        try {
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON,
                    objectMapper.writeValueAsString(createReq).getBytes(StandardCharsets.UTF_8));
            request = new Request.Builder()
                    .url(url)
                    .addHeader(HEADER_APP_ID, nullToEmpty(cloudDoc.getAppId()))
                    .addHeader(HEADER_APP_SECRET, nullToEmpty(cloudDoc.getAppSecret()))
                    .post(body)
                    .build();
        } catch (IOException e) {
            throw new MeetingAiException("10021", "云文档创建请求序列化失败", e);
        }

        JsonNode root = execute(request, "云文档创建");
        String fileCrdcId = extractField(root, "fileCrdcId");
        if (isBlank(fileCrdcId)) {
            throw new MeetingAiException("10022",
                    "云文档创建未返回 fileCrdcId，response:" + truncate(String.valueOf(root)));
        }
        CloudDocCreateResp resp = new CloudDocCreateResp();
        resp.setFileCrdcId(fileCrdcId);
        log.info("cloud doc create success, fileName:{}, fileCrdcId:{}", createReq.getFileName(), fileCrdcId);
        return resp;
    }

    private JsonNode execute(Request request, String action) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String bodyText = responseBody == null ? "" : responseBody.string();
            if (!response.isSuccessful()) {
                throw new MeetingAiException("10023",
                        action + "失败，httpStatus:" + response.code() + ", body:" + truncate(bodyText));
            }
            if (isBlank(bodyText)) {
                throw new MeetingAiException("10024", action + "返回内容为空");
            }
            JsonNode root = objectMapper.readTree(bodyText);
            checkBusinessCode(root, action, bodyText);
            return root;
        } catch (MeetingAiException e) {
            throw e;
        } catch (IOException e) {
            throw new MeetingAiException("10025", action + "调用异常", e);
        }
    }

    /**
     * HTTP 200 也可能是业务失败，提前把业务码与信息暴露到异常里，便于排查
     */
    private void checkBusinessCode(JsonNode root, String action, String bodyText) {
        if (root == null) {
            return;
        }
        JsonNode codeNode = root.get("code");
        if (codeNode == null || codeNode.isNull()) {
            return;
        }
        String code = codeNode.asText();
        boolean success = "0".equals(code) || "200".equals(code) || "success".equalsIgnoreCase(code);
        if (!success) {
            JsonNode msgNode = root.get("msg");
            String msg = msgNode == null || msgNode.isNull() ? truncate(bodyText) : msgNode.asText();
            throw new MeetingAiException("10027", action + "业务失败，code:" + code + ", msg:" + msg);
        }
    }

    /**
     * 兼容直接返回字段与 {data:{...}} / {resultData:{...}} 两种响应结构
     */
    private String extractField(JsonNode root, String field) {
        if (root == null) {
            return null;
        }
        JsonNode direct = root.get(field);
        if (direct != null && !direct.isNull()) {
            return direct.asText();
        }
        String[] wrappers = {"data", "resultData", "result"};
        for (String wrapper : wrappers) {
            JsonNode wrapperNode = root.get(wrapper);
            if (wrapperNode != null && wrapperNode.isObject()) {
                JsonNode value = wrapperNode.get(field);
                if (value != null && !value.isNull()) {
                    return value.asText();
                }
            }
        }
        return null;
    }

    private String joinUrl(String baseUrl, String path) {
        if (isBlank(baseUrl)) {
            throw new MeetingAiException("10026", "云文档服务地址 meeting.ai.cloud-doc.base-url 未配置");
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
