package org.example.backend.utils.SchoolAdmin;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SchoolAdminManagementUtils {
    private static final String SM3_KEY = "SM3_KEY"; // 需替换为实际 SM3 密钥

    public static String sm3Encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SM3"); // 假设支持 SM3
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}