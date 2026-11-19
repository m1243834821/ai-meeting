package com.htsc.meeting.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.htsc.boot.common.model.Response;
import com.htsc.meeting.ai.dto.req.LowCodeCreateDataReq;
import com.htsc.meeting.ai.dto.req.LowCodeWorkflowQueryReq;
import com.htsc.meeting.ai.service.LowCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lowcode")
public class LowCodeController {
    private final LowCodeService lowCodeService;

    /** 创建数据并按 submit 参数决定是否发起流程。 */
    @PostMapping("/test/create")
    public Response<JSONObject> create(@RequestBody LowCodeCreateDataReq request) {
        Response<JSONObject> response = new Response<>();
        response.setResultData(lowCodeService.createData(request));
        return response;
    }

    /** 按流程实例 ID 查询流程状态。 */
    @PostMapping("/test/workflow-instances")
    public Response<JSONObject> query(@RequestBody LowCodeWorkflowQueryReq request) {
        Response<JSONObject> response = new Response<>();
        response.setResultData(lowCodeService.queryWorkflowInstances(request));
        return response;
    }
}
