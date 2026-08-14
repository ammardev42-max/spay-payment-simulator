package com.ammarbhatkar.SPay.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI spayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SPay API")
                        .description("Secure Swift Safe - UPI payment switch simulator API")
                        .version("v1"));
    }
}
