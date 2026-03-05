package com.goosage.api.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalPingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}