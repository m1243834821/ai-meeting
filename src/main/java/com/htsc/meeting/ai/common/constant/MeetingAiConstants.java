package com.htsc.meeting.ai.common.constant;

/**
 * 会议 AI 业务常量
 */
public final class MeetingAiConstants {

    private MeetingAiConstants() {
    }

    /** 腾讯会议事件：云录制已完成 */
    public static final String EVENT_RECORDING_COMPLETED = "recording.completed";

    /** 会议文件类型：1-纪要 */
    public static final String FILE_TYPE_MINUTES = "1";

    /** 是否可看阅：1-是 */
    public static final String CAN_READ_YES = "1";

    /** 看阅范围：1-全员 */
    public static final String READ_SCOPE_ALL = "1";

    /** 是否可下载：1-是 */
    public static final String CAN_DOWNLOAD_YES = "1";

    /** 默认纪要文件类型 */
    public static final String DEFAULT_FILE_TYPE = "txt";

    /** 主表云文档 ID 分隔符 */
    public static final String CLOUD_DOC_ID_SEPARATOR = ",";

    /** 主表 CLOUD_DOC_ID 字段长度上限 */
    public static final int MAX_CLOUD_DOC_ID_LENGTH = 1000;

}
