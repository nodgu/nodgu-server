package io.github.nodgu.core_server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI openAPI() {
        // API 기본 정보 설정
        Info info = new Info()
                .title("NODGU API Documentation")
                .version("1.0.0")
                .description("NODGU 프로젝트의 API 문서입니다.")
                .contact(new Contact()
                        .name("NODGU Team")
                        .email("contact@nodgu.shop"));

        // 서버 설정 (환경별로 다르게 설정)
        List<Server> servers = getServers();

        // JWT 인증 설정
        String jwtScheme = "bearerAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);
        Components components = new Components()
                .addSecuritySchemes(jwtScheme, new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(info)
                .servers(servers)
                .components(components)
                .addSecurityItem(securityRequirement);
    }
    
    private List<Server> getServers() {
        return List.of(
            // 로컬 개발 서버
            new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server"),
            
            // 프로덕션 서버
            new Server()
                .url("https://api.nodgu.shop")
                .description("Production Server")
        );
    }
}
