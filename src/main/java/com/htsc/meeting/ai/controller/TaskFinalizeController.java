package com.htsc.meeting.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.htsc.boot.common.model.Response;
import com.htsc.meeting.ai.dto.req.TaskFinalizeReq;
import com.htsc.meeting.ai.service.TaskFinalizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lowcode/tasks")
public class TaskFinalizeController {
    private final TaskFinalizeService taskFinalizeService;

    @PostMapping("/update")
    public Response<JSONObject> update(@RequestBody TaskFinalizeReq req) {
        Response<JSONObject> response = new Response<>();
        try {
            response.setResultData(taskFinalizeService.update(req));
        } catch (TaskFinalizeService.InvalidRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return response;
    }
}
