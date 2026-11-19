package com.htsc.meeting.ai.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htsc.meeting.ai.config.LowCodeProperties;
import com.htsc.meeting.ai.dto.req.LowCodeCreateDataReq;
import com.htsc.meeting.ai.dto.req.LowCodeWorkflowQueryReq;
import com.htsc.meeting.ai.service.LowCodeService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LowCodeServiceImpl implements LowCodeService {
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final LowCodeProperties properties;
    private final OkHttpClient httpClient;
    private volatile String accessToken;
    private volatile long tokenExpireAt;

    public LowCodeServiceImpl(LowCodeProperties properties) {
        this.properties = properties;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
                .readTimeout(properties.getReadTimeoutSeconds(), TimeUnit.SECONDS)
                .build();
    }

    @Override
    public JSONObject updateData(JSONObject request) {
        return execute("PATCH", "/api/openapi/v3/runtime/data/update", request, true);
    }

    @Override
    public JSONObject createData(LowCodeCreateDataReq request) {
        JSONObject body = new JSONObject();
        body.put("modelCode", request.getModelCode());
        body.put("userId", request.getUserId());
        if (request.getSubmit() != null) {
            body.put("submit", request.getSubmit());
        }
        body.put("data", request.getData());
        return post(properties.getCreateDataPath(), body, true);
    }

    @Override
    public JSONObject queryWorkflowInstances(LowCodeWorkflowQueryReq request) {
        JSONObject body = (JSONObject) JSON.toJSON(request);
        return post(properties.getWorkflowInstanceListPath(), body, true);
    }

    private JSONObject post(String path, JSONObject body, boolean retryOnUnauthorized) {
        return execute("POST", path, body, retryOnUnauthorized);
    }

    private JSONObject execute(String method, String path, JSONObject body, boolean retryOnUnauthorized) {
        try {
            String token = getAccessToken(false);
            Request request = new Request.Builder()
                    .url(joinUrl(properties.getBaseUrl(), path))
                    .method(method, RequestBody.create(JSON_TYPE, body.toJSONString()))
                    .header("Authorization", "Bearer " + token)
                    .header("client_id", properties.getClientId())
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() == null ? "" : response.body().string();
                if (response.code() == 401 && retryOnUnauthorized) {
                    accessToken = null;
                    return execute(method, path, body, false);
                }
                JSONObject result = parseResponse(response, responseBody);
                if (retryOnUnauthorized && (response.code() == 401 || result.getIntValue("errcode") == 401)) {
                    accessToken = null;
                    return execute(method, path, body, false);
                }
                return result;
            }
        } catch (IOException e) {
            throw new IllegalStateException("调用低代码平台失败: " + path, e);
        }
    }

    private String getAccessToken(boolean forceRefresh) throws IOException {
        long now = System.currentTimeMillis();
        if (!forceRefresh && accessToken != null && now < tokenExpireAt) {
            return accessToken;
        }
        synchronized (this) {
            if (!forceRefresh && accessToken != null && System.currentTimeMillis() < tokenExpireAt) {
                return accessToken;
            }
            HttpUrl url = HttpUrl.parse(joinUrl(properties.getBaseUrl(), properties.getTokenPath()))
                    .newBuilder()
                    .addQueryParameter("client_id", properties.getClientId())
                    .addQueryParameter("client_secret", properties.getClientSecret())
                    .addQueryParameter("grant_type", "client_credentials")
                    .build();
            Request request = new Request.Builder().url(url).post(RequestBody.create(null, new byte[0])).build();
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() == null ? "" : response.body().string();
                if (!response.isSuccessful()) {
                    throw new IllegalStateException("获取低代码 token 失败: HTTP " + response.code());
                }
                JSONObject json = JSON.parseObject(responseBody);
                String token = json.getString("access_token");
                if (token == null) {
                    token = json.getString("accessToken");
                }
                if (token == null) {
                    throw new IllegalStateException("低代码 token 响应中没有 access_token");
                }
                long expiresIn = json.getLongValue("expires_in");
                accessToken = token;
                tokenExpireAt = System.currentTimeMillis() + Math.max(60, expiresIn - 60) * 1000L;
                return token;
            }
        }
    }

    private JSONObject parseResponse(Response response, String body) {
        if (body.isEmpty()) {
            throw new IllegalStateException("低代码平台返回空响应，HTTP " + response.code());
        }
        JSONObject result = JSON.parseObject(body);
        if (!response.isSuccessful()) {
            throw new IllegalStateException("低代码平台调用失败: HTTP " + response.code() + ", " + result);
        }
        return result;
    }

    private String joinUrl(String baseUrl, String path) {
        return baseUrl.replaceAll("/+$", "") + "/" + path.replaceAll("^/+", "");
    }
}
