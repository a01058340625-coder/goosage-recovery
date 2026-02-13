package com.goosage.app;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.goosage.domain.qa.QaPort;
import com.goosage.dto.qa.QaRequest;
import com.goosage.dto.qa.QaResponse;
import com.goosage.entity.QaEntity;
import com.goosage.support.web.NotFoundException;

@Service
public class QaService {

    private final QaPort qaPort;

    public QaService(QaPort qaPort) {
        this.qaPort = qaPort;
    }

    /** 1) 질문 저장(답은 없어도 됨) */
    public QaResponse create(QaRequest req) {
        QaEntity e = new QaEntity();
        e.setQuestion(req.getQuestion());
        e.setAnswer(req.getAnswer()); // null 가능
        e.setTags(req.getTags());

        QaEntity saved = qaPort.save(e);
        return QaResponse.from(saved);
    }

    /** 2) 목록 */
    public List<QaResponse> findAll() {
        return qaPort.findAll()
                .stream()
                .map(QaResponse::from)
                .collect(Collectors.toList());
    }

    /** 3) 답변 채우기(부분 업데이트) */
    public QaResponse answer(long id, QaRequest req) {
        QaEntity e = qaPort.findById(id)
                .orElseThrow(() -> new NotFoundException("qa not found: id=" + id));

        e.setAnswer(req.getAnswer());
        e.setTags(req.getTags());

        QaEntity saved = qaPort.save(e);
        return QaResponse.from(saved);
    }
}
