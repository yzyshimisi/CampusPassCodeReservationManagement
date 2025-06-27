package org.example.backend.model;

import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.Security;

public class SM4Util {
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // 固定密钥（16 字节，128 位），生产环境中应动态生成或从配置文件读取
    private static final byte[] KEY = "1234567890abcdef1234567890abcdef".getBytes(StandardCharsets.UTF_8);
    // 固定初始向量 IV（16 字节），仅在 CBC 模式下使用
    private static final byte[] IV = "abcdef1234567890abcdef1234567890".getBytes(StandardCharsets.UTF_8);

    /**
     * SM4 加密（CBC 模式）
     * @param input 明文
     * @return 加密后的十六进制字符串
     * @throws Exception
     */
    public static String encrypt(String input) throws Exception {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("输入不能为空");
        }

        // 创建 SM4 引擎
        SM4Engine engine = new SM4Engine();
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        // 设置密钥和 IV
        KeyParameter keyParam = new KeyParameter(KEY);
        ParametersWithIV params = new ParametersWithIV(keyParam, IV);

        // 初始化加密模式
        cipher.init(true, params);

        // 将输入转换为字节
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] outputBytes = new byte[cipher.getOutputSize(inputBytes.length)];

        // 加密
        int len = cipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);
        len += cipher.doFinal(outputBytes, len);

        // 转换为十六进制字符串
        return Hex.toHexString(outputBytes, 0, len);
    }

    /**
     * SM4 解密（CBC 模式）
     * @param encrypted 加密后的十六进制字符串
     * @return 解密后的明文
     * @throws Exception
     */
    public static String decrypt(String encrypted) throws Exception {
        if (encrypted == null || encrypted.trim().isEmpty()) {
            throw new IllegalArgumentException("输入不能为空");
        }

        // 创建 SM4 引擎
        SM4Engine engine = new SM4Engine();
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        // 设置密钥和 IV
        KeyParameter keyParam = new KeyParameter(KEY);
        ParametersWithIV params = new ParametersWithIV(keyParam, IV);

        // 初始化解密模式
        cipher.init(false, params);

        // 将十六进制字符串转换为字节
        byte[] inputBytes = Hex.decode(encrypted);
        byte[] outputBytes = new byte[cipher.getOutputSize(inputBytes.length)];

        // 解密
        int len = cipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);
        len += cipher.doFinal(outputBytes, len);

        // 转换为字符串
        return new String(outputBytes, 0, len, StandardCharsets.UTF_8);
    }

    /**
     * 脱敏姓名
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String maskName(String name) {
        if (name == null || name.length() < 2) return name;
        if (name.length() == 2) {
            return name.charAt(0) + "*" + name.charAt(1);
        }
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    /**
     * 脱敏身份证号
     * @param idNumber 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdNumber(String idNumber) {
        if (idNumber == null || idNumber.length() < 8) return idNumber;
        return idNumber.substring(0, 4) + "****" + idNumber.substring(idNumber.length() - 4);
    }
}