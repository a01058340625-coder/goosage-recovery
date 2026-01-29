package com.goosage.service.study.forge;

import org.springframework.stereotype.Service;

import com.goosage.dao.TemplateDao;
import com.goosage.dto.KnowledgeDto;
import com.goosage.repository.KnowledgeRepository;
import com.goosage.service.KnowledgeTemplateService;

@Service
public class ForgeTriggerService {

    private final TemplateDao templateDao;
    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgeTemplateService knowledgeTemplateService;

    public ForgeTriggerService(
            TemplateDao templateDao,
            KnowledgeRepository knowledgeRepository,
            KnowledgeTemplateService knowledgeTemplateService
    ) {
        this.templateDao = templateDao;
        this.knowledgeRepository = knowledgeRepository;
        this.knowledgeTemplateService = knowledgeTemplateService;
    }

    public ForgePrepareResult prepare(long knowledgeId, String templateType) {

        if (templateDao.existsByKnowledgeIdAndType(knowledgeId, templateType)) {
            return ForgePrepareResult.ok("REUSE");
        }

        try {
            KnowledgeDto k = knowledgeRepository.findById(knowledgeId).orElse(null);
            if (k == null) {
                return ForgePrepareResult.fail("FAILED", "knowledge not found: " + knowledgeId);
            }

            String resultText = switch (templateType) {
                case "summary-v1" -> knowledgeTemplateService.toSummaryV1(k);
                default -> knowledgeTemplateService.toSummaryV1(k);
            };

            templateDao.insertTemplate(knowledgeId, templateType, resultText);
            return ForgePrepareResult.ok("CREATED");

        } catch (Exception e) {
            return ForgePrepareResult.fail("FAILED", e.getMessage());
        }
    }

    public record ForgePrepareResult(boolean success, String mode, String error) {
        public static ForgePrepareResult ok(String mode) { return new ForgePrepareResult(true, mode, null); }
        public static ForgePrepareResult fail(String mode, String error) { return new ForgePrepareResult(false, mode, error); }
    }
}
