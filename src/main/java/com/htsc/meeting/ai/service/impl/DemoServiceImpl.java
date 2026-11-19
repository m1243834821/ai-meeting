package com.htsc.meeting.ai.service.impl;

import com.htsc.meeting.ai.dto.req.DemoReq;
import com.htsc.meeting.ai.dto.resp.DemoResp;
import com.htsc.meeting.ai.entity.DemoModel;
import com.htsc.meeting.ai.mapper.DemoMapper;
import com.htsc.meeting.ai.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * DemoServiceImpl
 */
@Slf4j
@Service("demoService")
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoMapper demoMapper;

    @Override
    public DemoResp query(DemoReq demoReq) {
        DemoResp demoResp = new DemoResp();
        if (demoReq.getId() == null) {
            return demoResp;
        }
        // 查询数据库
        DemoModel demoModel = demoMapper.queryById(demoReq.getId());
        if (demoModel == null) {
            return demoResp;
        }
        demoResp.setName(demoModel.getName());
        return demoResp;
    }
}
