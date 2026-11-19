package com.htsc.meeting.ai.service;

import com.htsc.meeting.ai.dto.req.DemoReq;
import com.htsc.meeting.ai.dto.resp.DemoResp;

/**
 * DemoService
 */
public interface DemoService {

    /**
     * 查询接口
     *
     * @param demoReq 请求参数
     * @return 响应参数
     */
    DemoResp query(DemoReq demoReq);


}
