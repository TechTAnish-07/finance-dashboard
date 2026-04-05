package com.example.finance_dashboard.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomizer;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url("https://finance-dashboard-production-b11b.up.railway.app")
                        .description("Production")
                )
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url("http://localhost:8080")
                        .description("Local")
                )
                .info(new Info()
                        .title("Finance Dashboard API")
                        .version("1.0.0")
                        .description("""
                                ## 🔐 Demo Credentials
                                
                                | Field    | Value                         |
                                |----------|-------------------------------|
                                | Email    | `patidar29tanish@gmail.com`   |
                                | Password | `123456789`                   |
                                
                                **Steps:**
                                1. Call `POST /auth/login` with credentials above
                                2. Copy the JWT token from response
                                3. Click **Authorize 🔒** → paste: `Bearer <token>`
                                """)
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
    @Bean
    public OpenApiCustomizer serverBaseUrlCustomizer() {
        return openApi -> {
            openApi.getServers().clear();
            openApi.addServersItem(new Server()
                    .url("https://finance-dashboard-production-b11b.up.railway.app")
                    .description("Production")
            );
            openApi.addServersItem(new Server()
                    .url("http://localhost:8080")
                    .description("Local")
            );
        };
    }
}