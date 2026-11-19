package com.htsc.meeting.ai.service;

import com.htsc.meeting.ai.BaseTest;
import com.htsc.meeting.ai.dto.req.DemoReq;
import com.htsc.meeting.ai.dto.resp.DemoResp;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 示例 Service 测试，需先在测试库中创建 demo 表并插入 id=1 的数据
 */
public class DemoServiceTest extends BaseTest {

    @Autowired
    private DemoService demoService;

    @Test
    public void test() {
        DemoReq demoReq = new DemoReq();
        demoReq.setId(1L);
        DemoResp demoResp = demoService.query(demoReq);
        Assert.assertNotNull(demoResp.getName());
    }

}
