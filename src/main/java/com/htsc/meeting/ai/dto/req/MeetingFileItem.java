package com.htsc.meeting.ai.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 会议中心上传的会议文件
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingFileItem {

    /** 会议文件类型：0-决议 1-纪要 2-记录 */
    private String fileType;

    /** 上传人工号 */
    private String uploaderUserCode;

    /** 上传人部门编码，不传默认取主岗 */
    private String uploaderDeptCode;

    /** 文件名称 */
    private String fileName;

    /** 存储网关 ID */
    private String gatewayId;

    /** 上传时间，yyyy-MM-dd HH:mm:ss，不传取当前时间 */
    private String uploadTime;

    /** 是否可看阅：0-否 1-是 */
    private String canRead;

    /** 看阅范围：0-非全员 1-全员 */
    private String readScope;

    /** 是否可下载：0-否 1-是 */
    private String canDownload;

}
