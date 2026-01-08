/**
 * 🚨 배포 회귀 테스트
 * - 배포 후 이 테스트 4개 통과 = 서비스 정상
 * - 실패 시 즉시 롤백/장애 대응
 */


package com.goosage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class PostRegressionTest {

    @Autowired
    private TestRestTemplate rest;

    private static Long createdId; // 테스트 간 공유 (간단 회귀용)

    /**
     * 1) GET /posts : 목록 조회가 200이고 ApiResponse 형태인지
     */
    @Test
    @Order(1)
    void reg_01_findAll() {
        ResponseEntity<Map> res = rest.getForEntity("/posts", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map body = res.getBody();
        assertThat(body).isNotNull();
        assertThat(body).containsKey("success"); // ApiResponse 공통 필드 추정
        assertThat(body).containsKey("data");    // posts 리스트
    }

    /**
     * 2) POST /posts : 생성이 201이고, 응답에 data가 있으며 id를 뽑을 수 있는지
     */
    @Test
    @Order(2)
    void reg_02_create() {
        Map<String, Object> req = Map.of(
                "title", "reg-title",
                "content", "reg-content"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(req, headers);

        ResponseEntity<Map> res = rest.postForEntity("/posts", entity, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Map body = res.getBody();
        assertThat(body).isNotNull();
        assertThat(body).containsKey("data");

        // data 안에 id가 들어있다고 가정 (PostResponse에 id가 있을 확률 높음)
        Object dataObj = body.get("data");
        assertThat(dataObj).isInstanceOf(Map.class);

        Map data = (Map) dataObj;

        // id 필드명이 다를 수도 있어서( id / postId ) 둘 다 시도
        Object idObj = data.get("id");
        if (idObj == null) idObj = data.get("postId");

        assertThat(idObj).isNotNull();

        createdId = ((Number) idObj).longValue();
        assertThat(createdId).isGreaterThan(0);
    }

    /**
     * 3) GET /posts/{id} : 방금 만든 글이 조회되는지
     */
    @Test
    @Order(3)
    void reg_03_findOne() {
        assertThat(createdId).isNotNull();

        ResponseEntity<Map> res = rest.getForEntity("/posts/" + createdId, Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map body = res.getBody();
        assertThat(body).isNotNull();
        Map data = (Map) body.get("data");

        // title/content가 맞게 들어왔는지까지 확인
        assertThat(data.get("title")).isEqualTo("reg-title");
        assertThat(data.get("content")).isEqualTo("reg-content");
    }

    /**
     * 4) DELETE /posts/{id} : 삭제 후, 다시 조회하면 NotFound(또는 4xx)인지
     */
    @Test
    @Order(4)
    void reg_04_delete_and_verify() {
        assertThat(createdId).isNotNull();

        ResponseEntity<Map> delRes = rest.exchange(
                "/posts/" + createdId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Map.class
        );

        assertThat(delRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 삭제 검증: 다시 조회하면 404(NotFound) 또는 4xx여야 정상
        ResponseEntity<String> getRes = rest.getForEntity("/posts/" + createdId, String.class);
        assertThat(getRes.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @Order(5)
    void reg_05_paging_and_search() {
        ResponseEntity<Map> res =
                rest.getForEntity("/posts/page?page=0&size=5&keyword=t", Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map body = res.getBody();
        assertThat(body).containsKey("success");
        assertThat(body).containsKey("data");
    }
}