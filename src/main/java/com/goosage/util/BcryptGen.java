package com.goosage.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptGen {
    public static void main(String[] args) {
        var enc = new BCryptPasswordEncoder();
        System.out.println(enc.encode("1234"));
    }
}
