package com.goosage.academy.debug;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/academy/debug")
public class AcademySessionDebugController {

    @GetMapping("/session")
    public Map<String, Object> sessionDump(HttpSession session) {
        Map<String, Object> m = new HashMap<>();
        m.put("sessionId", session.getId());

        Map<String, Object> attrs = new HashMap<>();
        Enumeration<String> names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String k = names.nextElement();
            attrs.put(k, session.getAttribute(k));
        }
        m.put("attrs", attrs);
        return m;
    }
}
