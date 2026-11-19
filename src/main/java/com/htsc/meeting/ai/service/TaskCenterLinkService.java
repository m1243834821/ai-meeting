package com.htsc.meeting.ai.service;

import com.alibaba.fastjson.JSONObject;
import com.htsc.meeting.ai.dto.req.TaskCenterLinkReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.net.URI;

/** 第三阶段链接回填，返回低代码平台原始处理结果。 */
@Service
@RequiredArgsConstructor
public class TaskCenterLinkService {
    private final LowCodeService lowCodeService;

    public JSONObject updateLink(TaskCenterLinkReq req) {
        if (req == null) {
            throw new InvalidRequestException("请求不能为空");
        }
        require(req.getModelCode(), "modelCode");
        require(req.getObjectId(), "objectId");
        require(req.getUserId(), "userId");
        require(req.getTaskCenterLink(), "taskCenterLink");
        String link = req.getTaskCenterLink().trim();
        try {
            URI uri = URI.create(link);
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null || uri.getUserInfo() != null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("taskCenterLink必须是有效的HTTP或HTTPS链接");
        }
        JSONObject data = new JSONObject();
        data.put("id", req.getObjectId());
        data.put("task_center_link", link);
        JSONObject body = new JSONObject();
        body.put("modelCode", req.getModelCode());
        body.put("userId", req.getUserId());
        body.put("data", data);
        return lowCodeService.updateData(body);
    }

    private static void require(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidRequestException(name + "不能为空");
        }
    }

    public static class InvalidRequestException extends IllegalArgumentException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }
}
