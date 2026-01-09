package com.goosage.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import com.goosage.dto.TemplateDto;
import com.goosage.repository.TemplateRepository;

import org.springframework.stereotype.Service;

import com.goosage.dto.KnowledgeDto;
import com.goosage.template.SummaryV1Formatter;

@Service
public class KnowledgeTemplateService {

	private final TemplateRepository templateRepository;
	private final SummaryV1Formatter formatter = new SummaryV1Formatter();

	public KnowledgeTemplateService(TemplateRepository templateRepository) {
	    this.templateRepository = templateRepository;
	}

	    

	public String toSummaryV1(KnowledgeDto k) {

	    // 1) DB 캐시 조회
	    Optional<TemplateDto> cached =
	            templateRepository.findByKnowledgeIdAndTemplateType(k.getId(), "SUMMARY_V1");

	    if (cached.isPresent()) {
	        return cached.get().getResultText();
	    }

	    // 2) 없으면 생성 (기존 formatter 그대로 사용)
	    String resultText = formatter.format(
	            k.getSubject(),
	            k.getTitle(),
	            k.getContent(),
	            k.getTags()
	    );

	    // 3) DB 저장
	    TemplateDto t = new TemplateDto();
	    t.setKnowledgeId(k.getId());
	    t.setTemplateType("SUMMARY_V1");
	    t.setResultText(resultText);

	    templateRepository.save(t);

	    return resultText;
	}
}