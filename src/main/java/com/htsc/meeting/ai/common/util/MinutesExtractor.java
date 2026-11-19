package com.htsc.meeting.ai.common.util;

import com.htsc.link.calendar.core.openapi.dto.tencentmeeting.common.Minutes;
import com.htsc.link.calendar.core.openapi.dto.tencentmeeting.record.OpenApiTencentMeetingRecordDetailResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 从录制详情中提取可下载的混元章节纪要
 */
@Slf4j
public final class MinutesExtractor {

    private MinutesExtractor() {
    }

    /**
     * 过滤掉没有下载地址的纪要（未开启“允许下载”或账号类型不支持时返回空地址）
     */
    public static List<Minutes> extract(OpenApiTencentMeetingRecordDetailResponse detail) {
        if (detail == null || detail.getAi_minutes() == null || detail.getAi_minutes().isEmpty()) {
            return Collections.emptyList();
        }
        return detail.getAi_minutes().stream()
                .filter(Objects::nonNull)
                .filter(minutes -> {
                    if (minutes.getDownload_address() == null || minutes.getDownload_address().trim().isEmpty()) {
                        log.warn("ai minutes download address is empty, fileType:{}", minutes.getFile_type());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

}
