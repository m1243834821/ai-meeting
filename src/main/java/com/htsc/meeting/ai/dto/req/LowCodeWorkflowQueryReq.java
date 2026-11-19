package com.htsc.meeting.ai.dto.req;


import lombok.Data;
import java.util.List;
@Data
public class LowCodeWorkflowQueryReq {
    private List<String> workflowInstanceIds;
    private List<String> modelCodes;
    private List<String> status;
    private Integer page = 1;
    private Integer size = 100;
}