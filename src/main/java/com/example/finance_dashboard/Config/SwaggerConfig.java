package com.example.finance_dashboard.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Dashboard API")
                        .version("1.0.0")
                        .description("Role based finance management system")
                        .contact(new Contact()
                                .name("Tanish Patidar")
                                .email("patidartanish31@gmail.com")
                        )
                )
                // ✅ Adds lock icon on every endpoint
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                // ✅ Adds Authorize button at top of Swagger UI
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT token here")
                        )
                );
    }
}