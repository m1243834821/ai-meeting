package com.htsc.meeting.ai.controller;

import com.htsc.boot.common.model.Response;
import com.htsc.meeting.ai.common.enums.ErrorEnum;
import com.htsc.meeting.ai.dto.req.MeetingCreateReq;
import com.htsc.meeting.ai.dto.resp.MeetingCreateResp;
import com.htsc.meeting.ai.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会议接口
 */
@RestController
@RequestMapping("/meeting")
@Slf4j
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    /**
     * 创建会议及参会人
     *
     * @param request 创建请求
     * @return 创建结果，成功响应中的 code 为 200
     */
    @PostMapping("/create")
    public Response<MeetingCreateResp> create(@RequestBody(required = false) MeetingCreateReq request) {
        Response<MeetingCreateResp> response = new Response<>();
        try {
            response.setResultData(meetingService.create(request));
            response.setCodeAndMsg("200", "成功");
        } catch (IllegalArgumentException e) {
            log.warn("create meeting parameter error: {}", e.getMessage());
            response.setCodeAndMsg(ErrorEnum.PARAM_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("create meeting error", e);
            response.setCodeAndMsg(ErrorEnum.SYSTEM_ERROR.getCode(), ErrorEnum.SYSTEM_ERROR.getMsg());
        }
        return response;
    }

}
