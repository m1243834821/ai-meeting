package com.htsc.meeting.ai.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 会议 AI 业务配置，对应 application-*.properties 中的 meeting.ai.* 配置项。
 */
@Data
@Component
@ConfigurationProperties(prefix = "meeting.ai")
public class MeetingAiProperties {

    private Kafka kafka = new Kafka();
    private Tencent tencent = new Tencent();
    private CloudDoc cloudDoc = new CloudDoc();
    private MeetingCenter meetingCenter = new MeetingCenter();

    @Data
    public static class Kafka {
        /** 是否启用消费 */
        private boolean enable;
        /** 订阅 topic */
        private String topic;
    }

    @Data
    public static class Tencent {
        /** 日程开放平台租户 ID */
        private String tenantId;
        /** 日程开放平台租户 secret */
        private String secret;
    }

    @Data
    public static class CloudDoc {
        private String baseUrl;
        private String uploadPath;
        private String createPath;
        private String appId;
        private String appSecret;
        private int fileChannel;
        private String defaultRoleCode;
        private long maxFileSizeBytes;
        private long connectTimeoutMs;
        private long readTimeoutMs;
    }

    @Data
    public static class MeetingCenter {
        private String baseUrl;
        private String uploadPath;
        private String token;
        private String appId;
        private String userId;
        /** 事件中无部门信息，暂由配置提供 */
        private String uploaderDeptCode;
        private long connectTimeoutMs;
        private long readTimeoutMs;
    }

}
