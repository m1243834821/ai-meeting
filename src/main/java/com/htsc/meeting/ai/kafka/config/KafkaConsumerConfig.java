package com.htsc.meeting.ai.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 腾讯会议事件 Kafka 消费者配置。
 * <p>
 * 通过 meeting.ai.kafka.enable 控制是否启用，关闭时不创建任何消费者 Bean，应用可在无 Kafka 环境启动。
 */
@Slf4j
@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "meeting.ai.kafka", name = "enable", havingValue = "true")
public class KafkaConsumerConfig {

    @Value("${spring.kafka.tencent.bootstrap-servers:}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.tencent.group-id:htsc-meeting-ai-service}")
    private String consumerGroupId;

    @Value("${spring.kafka.consumer.auto-offset-reset:latest}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.max.poll.records:5}")
    private Integer pollRecords;

    @Value("${spring.kafka.consumer.max.poll.interval.ms:600000}")
    private Integer maxPollIntervalMs;

    @Value("${spring.kafka.consumer.session.timeout.ms:30000}")
    private Integer sessionTimeoutMs;

    @Value("${spring.kafka.consumer.heartbeat.interval.ms:10000}")
    private Integer heartbeatIntervalMs;

    @Value("${spring.kafka.consumer.auto.startup.enable:true}")
    private Boolean enableStartUp;

    @Value("${spring.kafka.security.protocol:PLAINTEXT}")
    private String securityProtocol;

    @Value("${spring.kafka.sasl.mechanism:SCRAM-SHA-512}")
    private String saslMechanism;

    @Value("${spring.kafka.sasl.jaas.config:}")
    private String saslJaasConfig;

    @Bean
    @ConditionalOnMissingBean(name = "tencentConsumerFactory")
    public ConsumerFactory<String, String> tencentConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, pollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // 未配置 jaas 时按明文集群连接，避免写入空值导致客户端初始化失败
        if (StringUtils.hasText(saslJaasConfig)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
            props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        } else {
            if (!"PLAINTEXT".equalsIgnoreCase(securityProtocol)) {
                log.warn("spring.kafka.sasl.jaas.config is empty, connect kafka with PLAINTEXT although "
                        + "spring.kafka.security.protocol={}", securityProtocol);
            }
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        }
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    @ConditionalOnMissingBean(name = "tencentContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> tencentContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tencentConsumerFactory());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setAutoStartup(enableStartUp);
        return factory;
    }

}
