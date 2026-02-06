package com.goosage.service.study.predict;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionEvidence;
import com.goosage.service.study.predict.model.PredictionInput;
import com.goosage.service.study.predict.model.PredictionLevel;
import com.goosage.service.study.predict.model.PredictionReasonCode;
import com.goosage.service.study.predict.rule.PredictionRule;

@Service
public class PredictionServiceImpl implements PredictionService {

    private final List<PredictionRule> rules;

    public PredictionServiceImpl(List<PredictionRule> rules) {
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(PredictionRule::priority))
                .toList();
    }

    @Override
    public Prediction predict(PredictionInput input) {
    	return rules.stream()
    			  .filter(r -> r.matches(input))   			
    			  .map(r -> r.predict(input))
    			  .findFirst()
    			  .orElseGet(() -> defaultPrediction(input));

    }

    private Prediction defaultPrediction(PredictionInput input) {
        return Prediction.of(
        		PredictionLevel.SAFE,
            PredictionReasonCode.DEFAULT_FALLBACK,
            PredictionEvidence.from(input)
        );
    }

}
