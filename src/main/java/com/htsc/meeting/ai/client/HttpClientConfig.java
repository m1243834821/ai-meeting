package com.htsc.meeting.ai.client;

import com.htsc.meeting.ai.common.config.MeetingAiProperties;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * HTTP 客户端配置。
 * <p>
 * 关闭连接失败自动重试，避免云文档创建等非幂等接口被重复调用。
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public OkHttpClient okHttpClient(MeetingAiProperties properties) {
        long connectTimeout = Math.max(properties.getCloudDoc().getConnectTimeoutMs(),
                properties.getMeetingCenter().getConnectTimeoutMs());
        long readTimeout = Math.max(properties.getCloudDoc().getReadTimeoutMs(),
                properties.getMeetingCenter().getReadTimeoutMs());
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }

}
