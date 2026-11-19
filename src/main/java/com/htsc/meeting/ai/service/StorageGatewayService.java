package com.htsc.meeting.ai.service;

import com.htsc.paas.sdk.storage.response.FileEntity;


/**
 * 存储网关服务
 * 提供文件上传、获取文件二进制流、文件下载等能力，
 * 底层通过 paas-sdk-storage 存储网关 SDK 与存储服务交互。
 */
public interface StorageGatewayService {


    /**
     * 上传文件
     * <p>
     * 将文件字节流上传至存储网关，返回存储网关生成的 fileID。
     *
     * @param bytes    文件字节流
     * @param fileName 文件名
     * @return 存储网关返回的文件ID
     * @throws Exception 上传异常
     */
    String upload(byte[] bytes, String fileName) throws Exception;

    /**
     * 获取文件二进制流
     * <p>
     * 根据 fileID 从存储网关获取文件的二进制字节流。
     *
     * @param fileID 文件ID
     * @return 文件二进制字节流
     * @throws Exception 获取异常
     */
    byte[] getBytes(String fileID) throws Exception;

    /**
     * 获取原始文件
     * <p>
     * 根据 fileID 从存储网关获取原始文件实体，包含文件字节流和文件名。
     *
     * @param fileID 文件ID
     * @return 文件实体（FileEntity）
     * @throws Exception 获取异常
     */
    FileEntity downloadFile(String fileID) throws Exception;
}