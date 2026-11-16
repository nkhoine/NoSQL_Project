package com.cafe.qlcafe;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MaHoa {

    public static String sha256(String input) {
        if (input == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encoded) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Loi ma hoa SHA-256", e);
        }
    }

    public static boolean kiemTraMatKhau(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) return false;
        String hashed = sha256(rawPassword);
        return hashed.equalsIgnoreCase(storedPassword) || rawPassword.equals(storedPassword);
    }
}
