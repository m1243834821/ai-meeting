package com.htsc.meeting.ai.kafka.listener;

import com.htsc.meeting.ai.service.MeetingEventProcessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 腾讯会议事件 Kafka 监听器。
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "meeting.ai.kafka.enable", havingValue = "true")
public class KafkaAiMettingListener {

    @Autowired
    private MeetingEventProcessService meetingEventProcessService;

    @KafkaListener(id = "meetingAiTencentListener",
            topics = "${meeting.ai.kafka.topic}",
            containerFactory = "tencentContainerFactory")
    public void listen(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        log.info("batch consume tencent meeting event start, size:{}", records.size());
        try {
            for (ConsumerRecord<String, String> record : records) {
                try {
                    meetingEventProcessService.process(record.value());
                } catch (Exception e) {
                    // 单条消息隔离点：失败仅记录日志，不抛出，避免整批重投
                    log.error("consume tencent meeting event failed, topic:{}, partition:{}, offset:{}, key:{}",
                            record.topic(), record.partition(), record.offset(), record.key(), e);
                }
            }
        } finally {
            try {
                ack.acknowledge();
            } catch (Exception e) {
                log.error("ack tencent meeting event failed", e);
            }
        }
        log.info("batch consume tencent meeting event finish, size:{}", records.size());
    }

}
