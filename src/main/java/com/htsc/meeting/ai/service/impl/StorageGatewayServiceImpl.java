package com.htsc.meeting.ai.service.impl;

import com.htsc.meeting.ai.service.StorageGatewayService;
import com.htsc.meeting.ai.utils.StorageCommonUtil;
 import com.htsc.paas.sdk.storage.response.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 存储网关服务实现
 * 通过 StorageCommonUtil 获取存储网关客户端，实现文件上传、获取文件流、文件下载等功能。
 */
@Slf4j
@Service("storageGatewayService")
public class StorageGatewayServiceImpl implements StorageGatewayService {

    /**
     * 存储网关公共工具类，提供客户端初始化和 token 获取能力
     */
    @Autowired
    private StorageCommonUtil storageCommonUtil;

    /**
     * 上传文件
     *
     * @param bytes    文件字节流
     * @param fileName 文件名
     * @return 存储网关返回的文件ID
     * @throws Exception 上传异常
     */
    @Override
    public String upload(byte[] bytes, String fileName) throws Exception {
        // 获取文件访问 token
        String fileToken = storageCommonUtil.findFileToken();
        // 获取默认用户名
        String defaultUsername = storageCommonUtil.getDefaultUsername();
        // 调用存储网关客户端上传文件
        String fileID = storageCommonUtil.getClient().upload(bytes, fileName, defaultUsername, fileToken);
        return fileID;
    }

    /**
     * 获取文件二进制流
     *
     * @param fileID 文件ID
     * @return 文件二进制字节流
     * @throws Exception 获取异常
     */
    @Override
    public byte[] getBytes(String fileID) throws Exception {
        // 获取文件访问 token
        String token = storageCommonUtil.findFileToken();
        // 调用存储网关客户端获取文件字节流
        return storageCommonUtil.getClient().getBytes(fileID, token);
    }

    /**
     * 获取原始文件
     *
     * @param fileID 文件ID
     * @return 文件实体（FileEntity），包含文件字节流和文件名
     * @throws Exception 获取异常
     */
    @Override
    public FileEntity downloadFile(String fileID) throws Exception {
        // 获取文件访问 token
        String token = storageCommonUtil.findFileToken();
        // 调用存储网关客户端获取文件实体
        return storageCommonUtil.getClient().getFile(fileID, token);
    }
}