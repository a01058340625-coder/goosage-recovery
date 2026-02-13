package com.goosage.app;

import org.springframework.stereotype.Service;

import com.goosage.domain.study.StudyStreakPort;

@Service
public class StudyStreakService {

    private final StudyStreakPort studyStreakPort;

    public StudyStreakService(StudyStreakPort studyStreakPort) {
        this.studyStreakPort = studyStreakPort;
    }

    public int getStreak(long userId) {
        return studyStreakPort.countStreak(userId);
    }
}
