package com.shree031.jsondatasetapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JSON Dataset API")
                        .version("1.0")
                        .description("API for inserting and querying JSON dataset records with group-by and sort-by options."));
    }
}
