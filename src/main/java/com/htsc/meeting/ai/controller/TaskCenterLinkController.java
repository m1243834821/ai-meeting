package com.htsc.meeting.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.htsc.boot.common.model.Response;
import com.htsc.meeting.ai.dto.req.TaskCenterLinkReq;
import com.htsc.meeting.ai.service.TaskCenterLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** 更新链接本身不会自动审批或推进流程节点。 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/lowcode/tasks")
public class TaskCenterLinkController {
    private final TaskCenterLinkService taskCenterLinkService;

    @PostMapping("/link")
    public Response<JSONObject> updateLink(@RequestBody TaskCenterLinkReq req) {
        JSONObject platformResult;
        try {
            platformResult = taskCenterLinkService.updateLink(req);
        } catch (TaskCenterLinkService.InvalidRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        Response<JSONObject> response = new Response<>();
        response.setResultData(platformResult);
        if (platformResult == null || !platformResult.containsKey("errcode")
                || platformResult.getIntValue("errcode") != 0) {
            response.setCodeAndMsg("10001", "低代码平台更新失败，请查看resultData");
        }
        return response;
    }

}
