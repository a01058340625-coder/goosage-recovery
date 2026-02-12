package com.goosage.app;

import org.springframework.stereotype.Service;

import com.goosage.domain.study.StudyDebugPort;

@Service
public class StudyDebugService {

    private final StudyDebugPort studyDebugPort;

    public StudyDebugService(StudyDebugPort studyDebugPort) {
        this.studyDebugPort = studyDebugPort;
    }

    public void recordPing(Long userId) {
        studyDebugPort.recordPing(userId);
    }
}
