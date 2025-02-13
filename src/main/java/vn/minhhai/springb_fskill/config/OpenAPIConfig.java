package vn.minhhai.springb_fskill.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("api-service")
                .packagesToScan("vn.minhhai.springb_fskill.controller")
                .build();
    }

    @Bean
    public OpenAPI openAPI(
            @Value("${open.api.title}") String title,
            @Value("${open.api.version}") String version,
            @Value("${open.api.description}") String description,
            @Value("${open.api.serverUrl}") String serverUrl,
            @Value("${open.api.serverName}") String serverName) {
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl).description(serverName)))
                .info(new Info().title(title)
                        .description(description)
                        .version(version)
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .components(new Components().addSecuritySchemes(serverName, null));
        // .components(
        // new Components()
        // .addSecuritySchemes(
        // "bearerAuth",
        // new SecurityScheme()
        // .type(SecurityScheme.Type.HTTP)
        // .scheme("bearer")
        // .bearerFormat("JWT")))
        // .security(List.of(new SecurityRequirement().addList("bearerAuth")));
    }
}