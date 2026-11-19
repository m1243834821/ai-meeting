package com.htsc.meeting.ai.common.util;

import com.htsc.meeting.ai.common.exception.MeetingAiException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 日程开放平台事件内容加解密工具。
 * <p>
 * 算法：AES-128-CBC / PKCS5Padding；key 为 Base64 字符串，IV 取 key 前 16 字节。
 */
public final class AesUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 16;

    private AesUtil() {
    }

    /**
     * 解密事件内容
     *
     * @param encryptedText Base64 密文
     * @param base64Key     Base64 密钥
     * @return 明文 JSON
     */
    public static String decrypt(String encryptedText, String base64Key) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, base64Key);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (MeetingAiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeetingAiException("10003", "腾讯会议事件内容解密失败", e);
        }
    }

    /**
     * 加密明文，主要用于单元测试与联调构造数据
     */
    public static String encrypt(String plainText, String base64Key) {
        try {
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, base64Key);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (MeetingAiException e) {
            throw e;
        } catch (Exception e) {
            throw new MeetingAiException("10004", "腾讯会议事件内容加密失败", e);
        }
    }

    private static Cipher initCipher(int mode, String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != KEY_LENGTH) {
            throw new MeetingAiException("10005", "AES key 解码后长度必须为 16 字节，实际：" + keyBytes.length);
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes, 0, KEY_LENGTH);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(mode, secretKeySpec, ivParameterSpec);
        return cipher;
    }

}
