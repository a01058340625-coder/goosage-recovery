package com.goosage.academy.workspace;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/academy/workspaces")
public class AcademyWorkspaceController {

    private final AcademyWorkspaceService service;

    public AcademyWorkspaceController(AcademyWorkspaceService service) {
        this.service = service;
    }

    private long requireUserId(HttpSession session) {
        Object v = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (v == null) throw new RuntimeException("UNAUTHORIZED");

        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();

        throw new RuntimeException("UNAUTHORIZED");
    }


    @PostMapping
    public Map<String, Object> create(@RequestBody WorkspaceCreateRequest req, HttpSession session) {
        long userId = requireUserId(session);
        WorkspaceResponse w = service.createWorkspace(userId, req);
        return Map.of("success", true, "message", "CREATED", "data", w);
    }

    @GetMapping("/me")
    public Map<String, Object> my(HttpSession session) {
        long userId = requireUserId(session);
        List<WorkspaceResponse> list = service.myWorkspaces(userId);
        return Map.of("success", true, "message", "OK", "data", list);
    }
}
