package com.goosage.academy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.support.web.ApiResponse;

@RestController
@RequestMapping("/academy")
public class AcademyHealthController {

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.ok("OK", "academy");
    }
}
