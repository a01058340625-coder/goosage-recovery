package com.goosage.infra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goosage.entity.Template;

public interface TemplateRepository extends JpaRepository<Template, Long> {

	Optional<Template> findByKnowledgeIdAndTemplateType(Long knowledgeId, String templateType);
}
