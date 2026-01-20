package com.goosage.academy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AcademyHealthController {

    @GetMapping("/academy/health")
    public Map<String, Object> health() {
        return Map.of(
                "success", true,
                "message", "ACADEMY UP",
                "data", Map.of("module", "academy", "status", "UP")
        );
    }
}
