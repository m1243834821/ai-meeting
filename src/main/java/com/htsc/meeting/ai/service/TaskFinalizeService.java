package com.htsc.meeting.ai.service;

import com.alibaba.fastjson.JSONObject;
import com.htsc.meeting.ai.dto.req.LowCodeCreateDataReq;
import com.htsc.meeting.ai.dto.req.TaskFinalizeReq;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.*;

/** 第二阶段：更新总任务，逐条新增或更新待办。不会自动推进流程。 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class TaskFinalizeService {
    private final LowCodeService lowCodeService;

    public JSONObject update(TaskFinalizeReq req) {
        require(req.getModelCode(), "modelCode");
        require(req.getObjectId(), "objectId");
        require(req.getUserId(), "userId");
        String choice = req.getSplitTasks();
        if (choice != null && !Arrays.asList("split", "skip", "拆解待办", "不拆解待办").contains(choice))
            bad("split_tasks必须为平台选项值：split/skip或拆解待办/不拆解待办");
        boolean skip = "skip".equals(choice) || "不拆解待办".equals(choice);
        if (!skip) {
            require(req.getSubtaskModelCode(), "subtaskModelCode");
            if (req.getSubtasks() == null) bad("拆解待办时subtasks必须提供，允许空数组");
        }
        if (skip && req.getSubtasks() != null && !req.getSubtasks().isEmpty())
            bad("不拆解待办时subtasks应为空，避免忽略传入任务");
        List<TaskFinalizeReq.Subtask> tasks = skip ? Collections.emptyList() : req.getSubtasks();
        for (TaskFinalizeReq.Subtask task : tasks) {
            if (task == null) bad("subtasks不能包含null");
            require(task.getContent(), "content");
            if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                try { LocalDate.parse(task.getDeadline()); }
                catch (RuntimeException e) { bad("deadline必须为yyyy-MM-dd"); }
            }
        }
        JSONObject main = new JSONObject();
        main.put("id", req.getObjectId());
        put(main, "meeting_center_id", req.getMeetingCenterId());
        put(main, "cloud_document_link", req.getCloudDocumentLink());
        put(main, "split_tasks", choice);
        put(main, "meeting_id", req.getMeetingId());
        put(main, "task_name", req.getTaskName());
        put(main, "task_content", req.getTaskContent());
        // 流程共用primary_owner，不再写入已从表单移除的task_creator。
        put(main, "creator_department_code", req.getCreatorDepartmentCode());
        JSONObject result = new JSONObject();
        result.put("mainResult", lowCodeService.updateData(body(req.getModelCode(), req.getUserId(), main)));
        List<JSONObject> rows = new ArrayList<>();
        result.put("rows", rows);
        result.put("skipTasks", skip);
        if (!success(result.getJSONObject("mainResult"))) return result;
        for (int i = 0; i < tasks.size(); i++) {
            TaskFinalizeReq.Subtask task = tasks.get(i);
            JSONObject row = new JSONObject();
            row.put("parentId", req.getObjectId());
            row.put("content", task.getContent());
            put(row, "owner_job_id", task.getOwnerJobId());
            put(row, "owner_department_code", task.getOwnerDepartmentCode());
            // 平台日期格式；空日期省略，保留原值，不用于清空已有日期。
            if (task.getDeadline() != null && !task.getDeadline().isEmpty())
                row.put("deadline", task.getDeadline() + " 00:00:00");
            JSONObject platform;
            if (task.getId() != null && !task.getId().trim().isEmpty()) {
                row.put("id", task.getId());
                platform = lowCodeService.updateData(body(req.getSubtaskModelCode(), req.getUserId(), row));
            } else {
                LowCodeCreateDataReq create = new LowCodeCreateDataReq();
                create.setModelCode(req.getSubtaskModelCode());
                create.setUserId(req.getUserId());
                create.setSubmit(false);
                create.setData(row);
                platform = lowCodeService.createData(create);
            }
            JSONObject item = new JSONObject();
            item.put("index", i);
            item.put("existingId", task.getId());
            item.put("platformResult", platform);
            rows.add(item);
            if (!success(platform)) break;
        }
        return result;
    }

    private static JSONObject body(String model, String user, JSONObject data) {
        JSONObject body = new JSONObject();
        body.put("modelCode", model); body.put("userId", user); body.put("data", data);
        return body;
    }
    private static boolean success(JSONObject json) {
        return json != null && json.containsKey("errcode") && json.getIntValue("errcode") == 0;
    }
    private static void put(JSONObject data, String key, String value) {
        if (value != null) data.put(key, value);
    }
    private static void require(String value, String name) {
        if (value == null || value.trim().isEmpty()) bad(name + "不能为空");
    }
    private static void bad(String message) {
        throw new InvalidRequestException(message);
    }
    public static class InvalidRequestException extends IllegalArgumentException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }
}
