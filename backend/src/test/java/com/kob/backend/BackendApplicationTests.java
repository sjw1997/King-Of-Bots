package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("psjw"));
        System.out.println(passwordEncoder.matches("psjw", "$2a$10$w0nse/aR7bTikR5J.jTHUuTHD0.a0lpQcE15E97G5yUA63FKLuxRS"));
    }

}
