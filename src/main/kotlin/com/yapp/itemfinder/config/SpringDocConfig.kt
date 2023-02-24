package com.yapp.itemfinder.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringDocConfig {
    companion object {
        private const val JWT_SCHEME_NAME = "JWT"
        private const val JWT_PREFIX = "bearer"
    }

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Item finder API Docs")
                    .description("Item finder Application")
                    .version("v0.0.1")
                    .license(License().name("Server Github link").url("https://github.com/YAPP-Github/21st-Android-Team-2-BE"))
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Android Github link")
                    .url("https://github.com/YAPP-Github/21st-Android-Team-2-Android")
            )
            .components(
                Components()
                    .addSecuritySchemes(JWT_SCHEME_NAME, createSecurityScheme())
            )
            .addSecurityItem(
                SecurityRequirement().addList(JWT_SCHEME_NAME)
            )
    }

    private fun createSecurityScheme(): SecurityScheme {
        return SecurityScheme()
            .name(JWT_SCHEME_NAME)
            .type(SecurityScheme.Type.HTTP)
            .scheme(JWT_PREFIX)
    }
}
