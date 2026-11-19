package com.htsc.meeting.ai.client;

import com.htsc.meeting.ai.common.config.MeetingAiProperties;
import com.htsc.meeting.ai.common.exception.MeetingAiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯会议纪要文件下载客户端
 */
@Slf4j
@Component
public class FileDownloadClient {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private MeetingAiProperties properties;

    /**
     * 下载文件内容。
     * <p>
     * 腾讯返回的下载地址有效期为 5 分钟，拿到地址后应立即调用。
     */
    public byte[] download(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new MeetingAiException("10010", "纪要下载地址为空");
        }
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) {
                throw new MeetingAiException("10011", "纪要文件下载失败，httpStatus:" + response.code());
            }
            long maxSize = properties.getCloudDoc().getMaxFileSizeBytes();
            long contentLength = body.contentLength();
            if (contentLength > maxSize) {
                throw new MeetingAiException("10012", "纪要文件超过大小限制，contentLength:" + contentLength);
            }
            // 未知长度时边读边计数，避免一次性缓冲造成 OOM
            byte[] bytes = readWithLimit(body.byteStream(), maxSize);
            log.info("download minutes file success, size:{}", bytes.length);
            return bytes;
        } catch (MeetingAiException e) {
            throw e;
        } catch (IOException e) {
            throw new MeetingAiException("10013", "纪要文件下载异常", e);
        }
    }

    private byte[] readWithLimit(InputStream inputStream, long maxSize) throws IOException {
        try (InputStream in = inputStream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            long total = 0;
            int read;
            while ((read = in.read(buffer)) != -1) {
                total += read;
                if (total > maxSize) {
                    throw new MeetingAiException("10012", "纪要文件超过大小限制，已读取:" + total);
                }
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

}
