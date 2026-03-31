package com.goosage.app.predict;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.study.StudySnapshot;

@Component
public class PredictionEngine {

    private final List<PredictionRule> rules;

    public PredictionEngine(List<PredictionRule> rules) {
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(PredictionRule::priority))
                .toList();

        System.out.println("=== PREDICTION RULE ORDER ===");
        for (PredictionRule r : this.rules) {
            System.out.println(r.getClass().getName() + " / priority=" + r.priority());
        }
        System.out.println("=== END RULE ORDER ===");
    }

    public Prediction predict(StudySnapshot s) {
        System.out.println("=== PREDICT START ===");
        System.out.println("studiedToday=" + s.studiedToday()
                + ", streakDays=" + s.streakDays()
                + ", recentEventCount3d=" + s.recentEventCount3d()
                + ", daysSinceLastEvent=" + s.daysSinceLastEvent()
                + ", eventsCount=" + (s.state() != null ? s.state().eventsCount() : -1)
                + ", quizSubmits=" + (s.state() != null ? s.state().quizSubmits() : -1)
                + ", wrongReviews=" + (s.state() != null ? s.state().wrongReviews() : -1)
                + ", wrongReviewDoneCount=" + (s.state() != null ? s.state().wrongReviewDoneCount() : -1));

        for (PredictionRule r : rules) {
            boolean matched = r.matches(s);
            System.out.println("[RULE] " + r.getClass().getName()
                    + " / priority=" + r.priority()
                    + " / matched=" + matched);

            if (matched) {
                Prediction p = r.apply(s);
                System.out.println("[APPLY] " + r.getClass().getName()
                        + " => level=" + p.level()
                        + ", reason=" + p.reasonCode());
                System.out.println("=== PREDICT END ===");
                return p;
            }
        }

        throw new IllegalStateException("No PredictionRule matched. DefaultFallbackRule missing?");
    }
}