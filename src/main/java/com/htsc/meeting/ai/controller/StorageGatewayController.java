package com.htsc.meeting.ai.controller;

import com.htsc.meeting.ai.service.StorageGatewayService;
import com.htsc.paas.sdk.storage.response.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

/**
 * 存储网关接口
 * 提供文件的上传、获取文件二进制流、文件下载等功能，
 */
@RestController
@RequestMapping("/storage")
@Slf4j
public class StorageGatewayController {

    /**
     * 存储网关服务
     */
    @Autowired
    private StorageGatewayService storageGatewayService;

    /**
     * 上传文件接口

     * @param fileName 自定义文件名（可选），为空时使用原始文件名
     * @param file     上传的文件（MultipartFile）
     * @return 存储网关返回的文件ID
     */
    @RequestMapping(path = "/uploadFile", method = {RequestMethod.POST})
    public String uploadFile(String fileName, @RequestParam(name = "file") MultipartFile file) {

        log.info("文件大小:" + file.getSize());
        log.info("原始文件名:" + file.getOriginalFilename());
        String storageName = "";
        String fileID = "";

        try {
            // 判断是否传入自定义文件名，为空则使用原始文件名，否则进行 URL 解码
            if (StringUtils.isBlank(fileName)) {
                storageName = file.getOriginalFilename();
            } else {
                storageName = URLDecoder.decode(fileName, "UTF-8");
            }
            byte[] bytes = file.getBytes();
            log.info("storageName:{}", storageName);
            // 调用服务层上传文件
            fileID = storageGatewayService.upload(bytes,storageName);
        } catch (Exception e) {
            log.error("upload error:", e);
        }
        return fileID;
    }

    /**
     * 获取文件二进制流接口
     * 根据 fileID 从存储网关获取文件的二进制字节流。
     * @param fileID 文件ID
     * @return 文件二进制字节流
     */
    @RequestMapping(path = "/getBytes", method = {RequestMethod.POST, RequestMethod.GET})
    public byte[] getBytes(String fileID) {
        try {
            return storageGatewayService.getBytes(fileID);
        } catch (Exception e) {
            log.error("getBytes error:", e);
        }
        return null;
    }

    /**
     * 下载文件接口
     * 根据 fileID 从存储网关获取原始文件，并通过 HttpServletResponse
     * 以附件形式将文件流输出到客户端，实现文件下载。
     *
     * @param fileID 文件ID
     */
    @RequestMapping(path = "/downloadFile", method = {RequestMethod.POST, RequestMethod.GET})
    public void downloadFile(String fileID) {
        try {
            // 调用服务层获取文件实体
            FileEntity response = storageGatewayService.downloadFile(fileID);
            if (response == null) {
                return;
            }
            byte[] fileBytes = response.getBytes();
            String fileName = response.getFileName();
            // 获取当前请求的 HttpServletResponse
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes != null) {
                HttpServletResponse httpServletResponse = servletRequestAttributes.getResponse();
                if (httpServletResponse != null) {
                    // 重置响应，设置下载响应头，将文件字节流写入输出流
                    httpServletResponse.reset();
                    httpServletResponse.setContentType("multipart/form-data");
                    httpServletResponse.addHeader("Content-Disposition", "attachment;filename=" + fileName);
                    OutputStream outputStream = new BufferedOutputStream(httpServletResponse.getOutputStream());
                    outputStream.write(fileBytes);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            log.warn("downloadFile exception,fileID:{},exception:{}", fileID, e);
        }
    }
}