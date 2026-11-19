package com.htsc.meeting.ai.service;

import com.alibaba.fastjson.JSONObject;
import com.htsc.meeting.ai.dto.req.LowCodeCreateDataReq;
import com.htsc.meeting.ai.dto.req.LowCodeWorkflowQueryReq;

public interface LowCodeService {
    JSONObject updateData(JSONObject request);
    JSONObject createData(LowCodeCreateDataReq request);
    JSONObject queryWorkflowInstances(LowCodeWorkflowQueryReq request);
}

