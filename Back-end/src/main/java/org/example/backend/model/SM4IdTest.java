package org.example.backend.model;

import org.example.backend.model.SM4Util;

public class SM4IdTest {
    public static void main(String[] args) {
        try {
            // 测试数据
            String publicIdNumber = "330123199001011234"; // 公众预约身份证号
            String officialIdNumber = "330123198501011234"; // 公务预约身份证号

            // 加密身份证号
            String encryptedPublicId = SM4Util.encrypt(publicIdNumber);
            String encryptedOfficialId = SM4Util.encrypt(officialIdNumber);

            // 脱敏身份证号
            String maskedPublicId = SM4Util.maskIdNumber(publicIdNumber);
            String maskedOfficialId = SM4Util.maskIdNumber(officialIdNumber);

            // 输出结果
            System.out.println("Public ID - Encrypted: " + encryptedPublicId);
            System.out.println("Public ID - Masked: " + maskedPublicId);
            System.out.println("Official ID - Encrypted: " + encryptedOfficialId);
            System.out.println("Official ID - Masked: " + maskedOfficialId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}