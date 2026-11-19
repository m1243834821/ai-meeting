package com.htsc.meeting.ai.controller;

import com.htsc.boot.common.model.Response;
import com.htsc.meeting.ai.common.enums.ErrorEnum;
import com.htsc.meeting.ai.dto.req.DemoReq;
import com.htsc.meeting.ai.dto.resp.DemoResp;
import com.htsc.meeting.ai.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 示例接口，接入新业务时可参考本类的分层写法，验证完可删除
 */
@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {

    @Autowired
    private DemoService demoService;

    /**
     * 根据ID查询名称示例接口
     *
     * @param req 请求参数
     * @return 统一响应
     */
    @PostMapping("/queryName")
    public Response<DemoResp> queryName(@RequestBody DemoReq req) {
        Response<DemoResp> response = new Response<>();
        try {
            DemoResp result = demoService.query(req);
            response.setResultData(result);
        } catch (Exception e) {
            log.error("queryName error", e);
            response.setCodeAndMsg(ErrorEnum.SYSTEM_ERROR.getCode(), ErrorEnum.SYSTEM_ERROR.getMsg());
        }
        return response;
    }

}
