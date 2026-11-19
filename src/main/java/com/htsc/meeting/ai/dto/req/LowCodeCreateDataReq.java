package com.htsc.meeting.ai.dto.req;

import lombok.Data;
import java.util.Map;

@Data
public class LowCodeCreateDataReq {
    private String modelCode;
    private String userId;
    private Boolean submit = Boolean.TRUE;
    private Map<String, Object> data;
}
