package com.goosage.api.internal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalPingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}