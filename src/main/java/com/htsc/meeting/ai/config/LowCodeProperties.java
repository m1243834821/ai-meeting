package com.htsc.meeting.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lowcode")
public class LowCodeProperties {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String tokenPath = "/api/oauth/token";
    private String createDataPath = "/api/openapi/v3/runtime/data/create";
    private String workflowInstanceListPath = "/api/openapi/v3/runtime/workflow/instance/list";
    private String defaultModelCode = "open_api_model";
    private long connectTimeoutSeconds = 10;
    private long readTimeoutSeconds = 30;
}
