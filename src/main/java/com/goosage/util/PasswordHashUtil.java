package com.goosage.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashUtil {

    public static void main(String[] args) {
        String raw = (args != null && args.length > 0) ? args[0] : "1234";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 해시만 출력
        System.out.println(encoder.encode(raw));
    }
}
