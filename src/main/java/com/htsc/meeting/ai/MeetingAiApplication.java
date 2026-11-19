package com.htsc.meeting.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 会议 AI 服务启动类
 */
@SpringBootApplication
@MapperScan({"com.htsc.meeting.ai.mapper"})
@ImportResource(value = "classpath:conf/dubbo*.xml")
public class MeetingAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingAiApplication.class, args);
    }

}
