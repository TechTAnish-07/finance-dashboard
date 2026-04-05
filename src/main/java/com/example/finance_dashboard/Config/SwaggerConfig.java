package com.example.finance_dashboard.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
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
                .addServersItem(new Server()
                        .url("https://finance-dashboard-production-b11b.up.railway.app")
                        .description("Production")
                )
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local")
                )
                .info(new Info()
                        .title("Finance Dashboard API")
                        .version("1.0.0")
                        .description("""
                    ## 🔐 Demo Credentials
                    
                    Use these credentials to test the API:
                    
                    | Field    | Value                        |
                    |----------|------------------------------|
                    | Email    | `patidar29tanish@gmail.com`  |
                    | Password | `123456789`                  |
                    
                    **Steps:**
                    1. Call `POST /auth/login` with the credentials above
                    2. Copy the JWT token from the response
                    3. Click **Authorize** 🔒 and paste: `Bearer <your_token>`
                    """)
                        .contact(new Contact()
                                .name("Tanish Patidar")
                                .email("patidartanish31@gmail.com")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
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