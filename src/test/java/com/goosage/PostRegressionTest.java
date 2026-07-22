/**
 * 🚨 배포 회귀 테스트
 * - 배포 후 이 테스트 4개 통과 = 서비스 정상
 * - 실패 시 즉시 롤백/장애 대응
 */


package com.goosage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRegressionTest {

    @Autowired
    private TestRestTemplate rest;

    private static Long createdId; // 테스트 간 공유 (간단 회귀용)

    private String sessionCookie;

    @BeforeAll
    void loginForRegressionTests() {
        String email = "post-regression-" + UUID.randomUUID() + "@test.local";
        String password = "regression-password";

        Map<String, String> credentials = Map.of(
                "email", email,
                "password", password
        );

        ResponseEntity<Map> signup =
                rest.postForEntity("/auth/signup", credentials, Map.class);
        assertThat(signup.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map> login =
                rest.postForEntity("/auth/login", credentials, Map.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);

        String setCookie = login.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).isNotBlank();

        sessionCookie = setCookie.split(";", 2)[0];
    }

    private HttpHeaders authenticatedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, sessionCookie);
        return headers;
    }

    private <T> HttpEntity<T> authenticatedEntity(T body) {
        return new HttpEntity<>(body, authenticatedHeaders());
    }


    /**
     * 1) GET /posts : 목록 조회가 200이고 ApiResponse 형태인지
     */
    @Test
    @Order(1)
    void reg_01_findAll() {
        ResponseEntity<Map> res = rest.exchange(
                "/posts",
                HttpMethod.GET,
                authenticatedEntity(null),
                Map.class
        );

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

        HttpHeaders headers = authenticatedHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(req, headers);

        ResponseEntity<Map> res = rest.exchange(
                "/posts",
                HttpMethod.POST,
                entity,
                Map.class
        );

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

        ResponseEntity<Map> res = rest.exchange(
                "/posts/" + createdId,
                HttpMethod.GET,
                authenticatedEntity(null),
                Map.class
        );
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
                authenticatedEntity(null),
                Map.class
        );

        assertThat(delRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 삭제 검증: 다시 조회하면 404(NotFound) 또는 4xx여야 정상
        ResponseEntity<String> getRes = rest.exchange(
                "/posts/" + createdId,
                HttpMethod.GET,
                authenticatedEntity(null),
                String.class
        );
        assertThat(getRes.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @Order(5)
    void reg_05_paging_and_search() {
        ResponseEntity<Map> res = rest.exchange(
                "/posts/page?page=0&size=5&keyword=t",
                HttpMethod.GET,
                authenticatedEntity(null),
                Map.class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map body = res.getBody();
        assertThat(body).containsKey("success");
        assertThat(body).containsKey("data");
    }
}
