package com.htsc.meeting.ai.utils;

import com.htsc.paas.sdk.storage.heimdall.AuthStorage;
import com.htsc.paas.sdk.storage.heimdall.SimpleAuthClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;



@Component
@Slf4j
public class StorageCommonUtil {

    @Getter
    private SimpleAuthClient client;

    @Value("${storage.gateway.url}")
    private String storageGatewayUrl;

    @Value("${storage.gateway.heimdall.url}")
    private String heimdallUrl;

    @Value("${storage.gateway.identity.type}")
    private String identityType;

    @Value("${storage.gateway.resource.id}")
    private String resourceId;

    @Value("${storage.gateway.username}")
    private String defaultUsername;

    @Value("${storage.gateway.password}")
    private String defaultPassword;



    @PostConstruct
    private void clientInitial() {
        client = AuthStorage.create(storageGatewayUrl, heimdallUrl, identityType, resourceId);
    }

    /**
     * 获取访问文件服务的token
     *
     * @return
     */
    public String findFileToken() {
        try {
            String token = getClient().getToken(defaultUsername, defaultPassword);
            return token;
        } catch (Exception e) {
            log.error("findFileToken error:{}", e);
            return "";
        }

    }

    public String getDefaultUsername() {
        return defaultUsername;
    }
}
