package com.htsc.meeting.ai.service;

/**
 * 腾讯会议事件处理编排
 */
public interface MeetingEventProcessService {

    /**
     * 处理一条 Kafka 原始消息
     *
     * @param rawMessage Kafka 消息体
     */
    void process(String rawMessage);

}
