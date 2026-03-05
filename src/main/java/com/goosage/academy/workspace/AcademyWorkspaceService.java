package com.goosage.academy.workspace;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AcademyWorkspaceService {

    private final AcademyWorkspaceDao dao;

    public AcademyWorkspaceService(AcademyWorkspaceDao dao) {
        this.dao = dao;
    }

    @Transactional
    public WorkspaceResponse createWorkspace(long userId, WorkspaceCreateRequest req) {
        if (req == null || !StringUtils.hasText(req.getName())) {
            throw new RuntimeException("name은 필수");
        }

        String name = req.getName().trim();
        if (name.length() > 100) throw new RuntimeException("name이 너무 김(최대 100)");

        // 일단 v0.1은 FREE 고정
        long workspaceId = dao.insertWorkspace(name, "FREE");

        // 생성자는 ADMIN 자동 등록
        dao.insertMember(workspaceId, userId, "ADMIN");

        // 생성 직후 응답은 다시 조회 안 하고 즉시 구성 (createdAt은 NOW 근사로 필요 없으면 null 가능)
        // 단, 너 스타일로 createdAt 주고 싶으면 findById를 추가하면 됨.
        return new WorkspaceResponse(workspaceId, name, "FREE", null);
    }

    public List<WorkspaceResponse> myWorkspaces(long userId) {
        return dao.findMyWorkspaces(userId);
    }
}
