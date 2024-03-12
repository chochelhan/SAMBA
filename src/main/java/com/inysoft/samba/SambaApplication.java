package com.inysoft.samba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class SambaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SambaApplication.class, args);
    }

    @RequestMapping("/")
    public String home() {
        return "index.html";
    }
}
