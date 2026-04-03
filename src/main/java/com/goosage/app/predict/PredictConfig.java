package com.goosage.app.predict;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.predict.vector.VectorConverter;
import com.goosage.domain.predict.vector.VectorMatcher;

@Configuration
public class PredictConfig {
	
	@Bean
	public PredictionEngine predictionEngine(
	        List<PredictionRule> rules,
	        VectorConverter vectorConverter,
	        VectorMatcher vectorMatcher
	) {
	    return new PredictionEngine(rules, vectorConverter, vectorMatcher);
	}
}