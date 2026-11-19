package com.htsc.meeting.ai.common.util;

import com.htsc.meeting.ai.common.exception.MeetingAiException;
import com.htsc.meeting.ai.dto.event.MeetingEventMessage;
import com.htsc.meeting.ai.dto.event.MeetingRecordingEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 * 事件 JSON 解析测试
 */
public class EventJsonUtilsTest {

    @Test
    public void parseMessageShouldMapFields() {
        String raw = "{\"tenantId\":\"meeting-agent\",\"timestamp\":1722578582040,\"data\":\"abc\"}";
        MeetingEventMessage message = EventJsonUtils.parseMessage(raw);
        Assert.assertEquals("meeting-agent", message.getTenantId());
        Assert.assertEquals(Long.valueOf(1722578582040L), message.getTimestamp());
        Assert.assertEquals("abc", message.getData());
    }

    @Test
    public void parseEventShouldMapSnakeCaseFields() {
        String plain = "{\n"
                + "  \"event\":\"recording.completed\",\n"
                + "  \"trace_id\":\"trace-1\",\n"
                + "  \"payload\":[{\n"
                + "    \"operate_time\":1609313201465,\n"
                + "    \"recording_files\":[{\"record_file_id\":\"123242525\"}],\n"
                + "    \"meeting_info\":{\n"
                + "      \"meeting_id\":\"13339451618278424869\",\n"
                + "      \"meeting_code\":\"445999969\",\n"
                + "      \"subject\":\"tester的快速会议\",\n"
                + "      \"creator\":{\"userid\":\"021227\",\"user_name\":\"张三\"}\n"
                + "    }\n"
                + "  }]\n"
                + "}";

        MeetingRecordingEvent event = EventJsonUtils.parseEvent(plain);
        Assert.assertEquals("recording.completed", event.getEvent());
        Assert.assertEquals("trace-1", event.getTraceId());
        Assert.assertEquals(1, event.getPayload().size());
        Assert.assertEquals("123242525",
                event.getPayload().get(0).getRecordingFiles().get(0).getRecordFileId());
        Assert.assertEquals("445999969", event.getPayload().get(0).getMeetingInfo().getMeetingCode());
        Assert.assertEquals("021227", event.getPayload().get(0).getMeetingInfo().getCreator().getUserid());
        Assert.assertEquals(Long.valueOf(1609313201465L), event.getPayload().get(0).getOperateTime());
    }

    @Test(expected = MeetingAiException.class)
    public void parseInvalidJsonShouldThrow() {
        EventJsonUtils.parseMessage("{invalid json");
    }

}
