package com.htsc.meeting.ai.dto.req;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
public class TaskFinalizeReq {
    private String modelCode;
    private String objectId;
    private String userId;
    /** 平台子表实际编码，需核实是否就是subtasks。 */
    private String subtaskModelCode;
    @JsonProperty("meeting_center_id") private String meetingCenterId;
    @JsonProperty("cloud_document_link") private String cloudDocumentLink;
    /** 平台单选选项值；省略时保持平台原值。 */
    @JsonProperty("split_tasks") private String splitTasks;
    @JsonProperty("meeting_id") private String meetingId;
    @JsonProperty("task_name") private String taskName;
    @JsonProperty("task_content") private String taskContent;
    @JsonProperty("creator_job_id") private String creatorJobId;
    @JsonProperty("creator_department_code") private String creatorDepartmentCode;
    private List<Subtask> subtasks;

    @Data
    public static class Subtask {
        /** 已有子表行的数据ID；首次新增留空，后续更新必须提供。 */
        private String id;
        private String content;
        @JsonProperty("owner_job_id") private String ownerJobId;
        @JsonProperty("owner_department_code") private String ownerDepartmentCode;
        private String deadline;
    }
}
