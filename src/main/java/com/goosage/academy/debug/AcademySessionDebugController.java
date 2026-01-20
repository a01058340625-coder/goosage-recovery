package com.goosage.academy.debug;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class AcademySessionDebugController {

    @GetMapping("/academy/session-debug")
    public Map<String, Object> debug(HttpSession session) {
        Map<String, Object> out = new LinkedHashMap<>();
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String k = names.nextElement();
            Object v = session.getAttribute(k);
            out.put(k, v == null ? null : (v.getClass().getName() + " :: " + v.toString()));
        }
        return Map.of("success", true, "message", "OK", "data", out);
    }
}
