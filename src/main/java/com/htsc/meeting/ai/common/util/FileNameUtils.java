package com.htsc.meeting.ai.common.util;

import com.htsc.meeting.ai.common.constant.MeetingAiConstants;

/**
 * 纪要文件名处理工具
 */
public final class FileNameUtils {

    private static final int MAX_BASE_LENGTH = 80;
    private static final String ILLEGAL_CHARS = "[\\\\/:*?\"<>|\\r\\n]";

    private FileNameUtils() {
    }

    /**
     * 生成带后缀的纪要文件名，例如：周例会_AI纪要_1.docx
     *
     * @param subject  会议主题
     * @param fileType 文件类型（txt/pdf/docx）
     * @param index    同一录制文件内的纪要序号，从 0 开始
     */
    public static String buildFileName(String subject, String fileType, int index) {
        String base = sanitize(subject);
        if (base.length() > MAX_BASE_LENGTH) {
            base = base.substring(0, MAX_BASE_LENGTH);
        }
        if (base.isEmpty()) {
            base = "会议";
        }
        String suffix = fileType == null || fileType.trim().isEmpty()
                ? MeetingAiConstants.DEFAULT_FILE_TYPE : fileType.trim();
        return base + "_AI纪要_" + (index + 1) + "." + suffix;
    }

    /**
     * 去掉后缀，得到云文档名称
     */
    public static String removeExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    private static String sanitize(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll(ILLEGAL_CHARS, "").trim();
    }

}
