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

    // 修正为 16 字节密钥（128 位）
    private static final byte[] KEY = "1234567890abcdef".getBytes(StandardCharsets.UTF_8); // 16 字节
    // 修正为 16 字节初始向量 (IV)
    private static final byte[] IV = "abcdef1234567890".getBytes(StandardCharsets.UTF_8); // 16 字节

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

        SM4Engine engine = new SM4Engine();
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        KeyParameter keyParam = new KeyParameter(KEY);
        ParametersWithIV params = new ParametersWithIV(keyParam, IV);

        cipher.init(true, params);

        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] outputBytes = new byte[cipher.getOutputSize(inputBytes.length)];

        int len = cipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);
        len += cipher.doFinal(outputBytes, len);

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

        SM4Engine engine = new SM4Engine();
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        KeyParameter keyParam = new KeyParameter(KEY);
        ParametersWithIV params = new ParametersWithIV(keyParam, IV);

        cipher.init(false, params);

        byte[] inputBytes = Hex.decode(encrypted);
        byte[] outputBytes = new byte[cipher.getOutputSize(inputBytes.length)];

        int len = cipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);
        len += cipher.doFinal(outputBytes, len);

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