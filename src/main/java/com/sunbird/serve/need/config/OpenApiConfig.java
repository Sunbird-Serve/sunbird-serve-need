package com.sunbird.serve.need.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Swagger UI to authenticate via Keycloak OAuth2.
 * 
 * Developers click "Authorize" in Swagger → redirected to Keycloak login →
 * token injected automatically into all API requests.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public OpenAPI customOpenAPI() {
        String authUrl = issuerUri + "/protocol/openid-connect/auth";
        String tokenUrl = issuerUri + "/protocol/openid-connect/token";

        return new OpenAPI()
            .info(new Info()
                .title("Sunbird SERVE - Need Service API")
                .version("1.0")
                .description("Need management APIs secured with Keycloak JWT"))
            .addSecurityItem(new SecurityRequirement().addList("keycloak"))
            .components(new Components()
                .addSecuritySchemes("keycloak", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                            .authorizationUrl(authUrl)
                            .tokenUrl(tokenUrl)
                        )
                    )
                )
            );
    }
}
