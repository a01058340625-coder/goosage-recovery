package com.goosage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.goosage.common.NotFoundException;
import com.goosage.dto.qa.QaRequest;
import com.goosage.dto.qa.QaResponse;
import com.goosage.entity.QaEntity;
import com.goosage.repository.QaRepository;

@Service
public class QaService {

    private final QaRepository qaRepository;

    public QaService(QaRepository qaRepository) {
        this.qaRepository = qaRepository;
    }

    /** 1) 질문 저장(답은 없어도 됨) */
    public QaResponse create(QaRequest req) {
        QaEntity e = new QaEntity();
        e.setQuestion(req.getQuestion());
        e.setAnswer(req.getAnswer()); // null 가능
        e.setTags(req.getTags());

        QaEntity saved = qaRepository.save(e);
        return QaResponse.from(saved);
    }

    /** 2) 목록 */
    public List<QaResponse> findAll() {
        return qaRepository.findAll()
                .stream()
                .map(QaResponse::from)
                .collect(Collectors.toList());
    }

    /** 3) 답변 채우기(부분 업데이트) */
    public QaResponse answer(long id, QaRequest req) {
        QaEntity e = qaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("qa not found: id=" + id));

        // 질문 수정은 일단 막고, "답변"만 채우는 MVP로 고정
        e.setAnswer(req.getAnswer());
        e.setTags(req.getTags()); // tags도 같이 갱신 허용(선택)

        QaEntity saved = qaRepository.save(e);
        return QaResponse.from(saved);
    }
}
