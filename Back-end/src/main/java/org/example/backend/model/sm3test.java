package org.example.backend.model;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;

public class sm3test {
    public static void main(String[] args) {
        String password = "123456"; // 待加密的密码
        byte[] passwordBytes = password.getBytes(); // 转换为字节数组

        // 创建 SM3 摘要实例
        SM3Digest digest = new SM3Digest();
        digest.update(passwordBytes, 0, passwordBytes.length); // 更新数据

        // 生成加密结果
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0); // 完成摘要计算

        // 转换为十六进制字符串
        String encryptedPassword = Hex.toHexString(hash).toLowerCase();
        System.out.println("SM3 Encrypted Password for '123456': " + encryptedPassword);
    }
}