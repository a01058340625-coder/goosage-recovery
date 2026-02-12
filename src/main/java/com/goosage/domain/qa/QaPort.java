package com.goosage.domain.qa;

import java.util.Optional;

public interface QaPort {
    Optional<QaView> findById(long id);
    // 필요한 메서드가 더 있으면 여기 추가(일단 findById부터 시작)
}
