package com.freelancer.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    // Um endpoint simples para testar se o código está chegando aqui
    @GetMapping("/api/ping")
    public String ping() {
        System.out.println("PING recebido!");   // log
        return "pong";
    }
}
