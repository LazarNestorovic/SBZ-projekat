package com.ludo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LudoApp {
    public static void main(String[] args) {
        SpringApplication.run(LudoApp.class, args);
        System.out.println("\n=== Ne Ljuti Se Covječe — Web server pokrenut ===");
        System.out.println("=== Otvori: http://localhost:8080               ===\n");
    }
}
