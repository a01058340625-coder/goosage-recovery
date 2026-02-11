package com.goosage.api.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    // ✅ 이제 FilterRegistrationBean으로 AuthFilter 등록하지 않는다.
    // ✅ AuthSessionFilter는 @Component + OncePerRequestFilter라서 자동 등록됨.
}
