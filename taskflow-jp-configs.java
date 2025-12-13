// ==========================================
// 🚀 TaskflowApplication.java - メインクラス
// パス: src/main/java/com/taskflow/TaskflowApplication.java
// ==========================================

package com.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * TaskFlow アプリケーション メインクラス
 * 
 * @EnableJpaAuditing: BaseEntityの@CreatedDate, @LastModifiedDateを有効化
 * @author あなたの名前
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class TaskflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskflowApplication.class, args);
        
        System.out.println("=".repeat(60));
        System.out.println("🚀 TaskFlow アプリケーション起動成功！");
        System.out.println("📖 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("📋 API Docs: http://localhost:8080/api-docs");
        System.out.println("=".repeat(60));
    }
}


// ==========================================
// 📦 BaseEntity.java - 共通エンティティ
// パス: src/main/java/com/taskflow/global/common/BaseEntity.java
// ==========================================

package com.taskflow.global.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 全てのエンティティの基底クラス
 * 作成日時、更新日時を自動管理
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}


// ==========================================
// 📦 ApiResponse.java - 統一されたAPI応答フォーマット
// パス: src/main/java/com/taskflow/global/common/ApiResponse.java
// ==========================================

package com.taskflow.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 全てのAPI応答を統一されたフォーマットで返却
 * 
 * @param <T> 応答データタイプ
 */
@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;

    /**
     * 成功応答（データ含む）
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("リクエストが正常に処理されました")
                .data(data)
                .build();
    }

    /**
     * 成功応答（カスタムメッセージ）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 失敗応答
     */
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}


// ==========================================
// 🔧 JpaConfig.java - JPA設定
// パス: src/main/java/com/taskflow/global/config/JpaConfig.java
// ==========================================

package com.taskflow.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA設定クラス
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.taskflow.domain")
@EnableJpaAuditing
public class JpaConfig {
    // BaseEntityの@CreatedDate, @LastModifiedDateを自動管理
}


// ==========================================
// 🌐 WebConfig.java - CORS設定
// パス: src/main/java/com/taskflow/global/config/WebConfig.java
// ==========================================

package com.taskflow.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC設定
 * CORS(Cross-Origin Resource Sharing)許可
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}


// ==========================================
// 📚 SwaggerConfig.java - Swagger設定
// パス: src/main/java/com/taskflow/global/config/SwaggerConfig.java
// ==========================================

package com.taskflow.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger (OpenAPI 3.0) 設定
 * APIドキュメントを自動生成し、テスト可能なUIを提供
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement())
                .components(securitySchemes());
    }

    /**
     * API基本情報
     */
    private Info apiInfo() {
        return new Info()
                .title("TaskFlow API")
                .description("チーム協業タスク管理システム APIドキュメント")
                .version("1.0.0")
                .contact(new Contact()
                        .name("開発者名")
                        .email("your.email@example.com"));
    }

    /**
     * JWT認証スキーマ
     */
    private Components securitySchemes() {
        return new Components()
                .addSecuritySchemes("Bearer認証", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .description("JWT トークンを入力してください（例: Bearer eyJhbGci...)")
                );
    }

    /**
     * 全てのAPIにJWT認証適用
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("Bearer認証");
    }
}
