package com.goosage.app.predict;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.predict.vector.BehaviorPattern;
import com.goosage.domain.predict.vector.ObservationVector;
import com.goosage.domain.predict.vector.VectorConverter;
import com.goosage.domain.predict.vector.VectorMatcher;
import com.goosage.domain.recovery.RecoverySnapshot;

@Component
public class PredictionEngine {

    private final List<PredictionRule> rules;
    private final VectorConverter vectorConverter;
    private final VectorMatcher vectorMatcher;

    public PredictionEngine(List<PredictionRule> rules,
                            VectorConverter vectorConverter,
                            VectorMatcher vectorMatcher) {
        this.vectorConverter = vectorConverter;
        this.vectorMatcher = vectorMatcher;

        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(PredictionRule::priority))
                .toList();

        System.out.println("=== PREDICTION RULE ORDER ===");
        for (PredictionRule r : this.rules) {
            System.out.println(r.getClass().getName() + " / priority=" + r.priority());
        }
        System.out.println("=== END RULE ORDER ===");
    }

    public Prediction predict(RecoverySnapshot s) {
        System.out.println("=== PREDICT START ===");
        System.out.println("studiedToday=" + s.studiedToday()
                + ", streakDays=" + s.streakDays()
                + ", recentEventCount3d=" + s.recentEventCount3d()
                + ", daysSinceLastEvent=" + s.daysSinceLastEvent()
                + ", eventsCount=" + (s.state() != null ? s.state().eventsCount() : -1)
                + ", urgeLogs=" + (s.state() != null ? s.state().urgeLogs() : -1)
                + ", betAttempts=" + (s.state() != null ? s.state().betAttempts() : -1)
                + ", betBlockedCount=" + (s.state() != null ? s.state().betBlockedCount() : -1)
                + ", recoveryActionCount=" + (s.state() != null ? s.state().recoveryActionCount() : -1)
                + ", relapseSignalCount=" + (s.state() != null ? s.state().relapseSignalCount() : -1));

        ObservationVector vector = vectorConverter.from(s);
        BehaviorPattern pattern = vectorMatcher.match(vector);
        double distance = vectorMatcher.distanceTo(vector, pattern);

        System.out.println("[VECTOR] activity=" + vector.activity()
                + ", urgeRatio=" + vector.urgeRatio()
                + ", attemptRatio=" + vector.attemptRatio()
                + ", blockedRatio=" + vector.blockedRatio()
                + ", recoveryRatio=" + vector.recoveryRatio()
                + ", relapseRatio=" + vector.relapseRatio()
                + ", recentScore=" + vector.recentScore()
                + ", streakScore=" + vector.streakScore()
                + ", recencyPenalty=" + vector.recencyPenalty());

        System.out.println("[VECTOR] nearestPattern=" + pattern
                + ", distance=" + distance);

        for (PredictionRule r : rules) {
            boolean matched = r.matches(s);
            System.out.println("[RULE] " + r.getClass().getName()
                    + " / priority=" + r.priority()
                    + " / matched=" + matched);

            if (matched) {
                Prediction base = r.apply(s);

                Map<String, Object> mergedEvidence = new LinkedHashMap<>();
                if (base.evidence() != null) {
                    mergedEvidence.putAll(base.evidence());
                }

                mergedEvidence.put("nearestPattern", pattern.name());
                mergedEvidence.put("distance", distance);

                Prediction enriched = base.withEvidence(mergedEvidence);

                System.out.println("[APPLY] " + r.getClass().getName()
                        + " => level=" + enriched.level()
                        + ", reason=" + enriched.reasonCode());
                System.out.println("=== PREDICT END ===");

                return enriched;
            }
        }

        throw new IllegalStateException("No PredictionRule matched. MinimumActionRule missing?");
    }
}