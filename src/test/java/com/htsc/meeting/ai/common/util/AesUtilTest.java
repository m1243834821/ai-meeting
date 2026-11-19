package com.htsc.meeting.ai.common.util;

import com.htsc.meeting.ai.common.exception.MeetingAiException;
import org.junit.Assert;
import org.junit.Test;

/**
 * AES 解密工具测试，不依赖 Spring 环境
 */
public class AesUtilTest {

    private static final String KEY = "E6CUaZbuRt37PfkH5JWP4g==";

    @Test
    public void decryptShouldReturnOriginText() {
        String plain = "{\"event\":\"recording.completed\",\"trace_id\":\"trace-1\"}";
        String cipher = AesUtil.encrypt(plain, KEY);
        Assert.assertEquals(plain, AesUtil.decrypt(cipher, KEY));
    }

    @Test(expected = MeetingAiException.class)
    public void decryptInvalidBase64ShouldThrow() {
        AesUtil.decrypt("not-base64!!!", KEY);
    }

    @Test(expected = MeetingAiException.class)
    public void decryptInvalidKeyLengthShouldThrow() {
        AesUtil.decrypt("YWJj", "dGVzdA==");
    }

    @Test
    public void encrypt(){
        String cipher = AesUtil.encrypt("1", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgAfP+FV8c3QKF44kQLZMKBVh2cGxD+2xysQf5YEbGdhDpQYlh7YKS4TtVdM9ElZ8Hucbpfr/w8APNZ2UOM2o29Zh+pt/VVhoEXD1iSn2e32XvxeTkNhRKzbBRazbW/KCQcp2/SCCD9eHWb9MGpt3SH+6MzZL6yAik6K83E7+W5q2KTE90TLb1RV9nEh1QTRADe/CeQZU9bsgn2H9R/zA7G/qWUXJXf8oEO8iFTd8ai7ShRUllSiwmkAmDzs/8ODV2FLVStZL8IS0yqPc31YI8sr/9xYUvBS39sl4upbE/yK1paZbKGUNkVsVnlZoCQ0fLKtz3OmG42M0yBF0f3s2tQIDAQAB");
       System.out.println(cipher);
    }

}
