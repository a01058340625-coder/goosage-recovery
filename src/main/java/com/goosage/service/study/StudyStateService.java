package com.goosage.service.study;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.goosage.dao.study.StudyReadDao;
import com.goosage.dao.study.TodayRow;
import com.goosage.service.StudyStreakService;
import com.goosage.service.study.dto.StudyStateDto;

@Service
public class StudyStateService {

	private final StudyReadDao studyReadDao;
	private final StudyStreakService studyStreakService;

	private final com.goosage.dao.QuizResultDao quizResultDao;

	public StudyStateService(StudyReadDao studyReadDao, StudyStreakService studyStreakService,
			com.goosage.dao.QuizResultDao quizResultDao) {
		this.studyReadDao = studyReadDao;
		this.studyStreakService = studyStreakService;
		this.quizResultDao = quizResultDao;
	}

	public StudyStateDto getState(long userId) {

		TodayRow today = studyReadDao.findToday(userId).orElse(null);

		LocalDate ymd = (today != null) ? today.ymd() : LocalDate.now();
		int eventsCount = (today != null) ? today.eventsCount() : 0;
		int quizSubmits = (today != null) ? today.quizSubmits() : 0;
		int wrongReviews = (today != null) ? today.wrongReviews() : 0;
		LocalDateTime lastEventAt = (today != null) ? today.lastEventAt() : null;

		boolean studiedToday = eventsCount > 0;
		Long recentKnowledgeId = quizResultDao.findLatestKnowledgeIdByUser(userId);

		int streakDays = studyStreakService.getStreak(userId);

		return new StudyStateDto(ymd, studiedToday, streakDays, eventsCount, quizSubmits, wrongReviews, lastEventAt,
				recentKnowledgeId);

	}

}
