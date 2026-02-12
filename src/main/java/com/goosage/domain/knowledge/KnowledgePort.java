package com.goosage.domain.knowledge;

import java.util.Optional;

public interface KnowledgePort {
    Optional<KnowledgeView> findById(long id);
}
