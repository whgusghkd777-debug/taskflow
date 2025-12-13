// ==========================================
// 👤 User.java - ユーザーエンティティ
// パス: src/main/java/com/taskflow/domain/user/entity/User.java
// ==========================================

package com.taskflow.domain.user.entity;

import com.taskflow.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * ユーザー情報を保存するエンティティ
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * パスワード変更（セキュリティのためSetterの代わりに明示的メソッド）
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 名前変更
     */
    public void changeName(String newName) {
        this.name = newName;
    }
}


// ==========================================
// 🔐 UserRole.java - ユーザー権限Enum
// パス: src/main/java/com/taskflow/domain/user/entity/UserRole.java
// ==========================================

package com.taskflow.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ユーザー権限
 * ADMIN: 管理者（全権限）
 * USER: 一般ユーザー
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN", "管理者"),
    USER("ROLE_USER", "一般ユーザー");

    private final String key;
    private final String description;
}


// ==========================================
// 📝 SignUpRequest.java - 会員登録リクエスト
// パス: src/main/java/com/taskflow/domain/user/dto/request/SignUpRequest.java
// ==========================================

package com.taskflow.domain.user.dto.request;

import com.taskflow.domain.user.entity.User;
import com.taskflow.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 会員登録リクエストDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "正しいメールアドレス形式ではありません")
    private String email;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, message = "パスワードは最低8文字以上である必要があります")
    private String password;

    @NotBlank(message = "名前は必須です")
    @Size(min = 2, max = 50, message = "名前は2文字以上50文字以下である必要があります")
    private String name;

    /**
     * DTO → Entity変換
     * @param encodedPassword 暗号化されたパスワード
     */
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(UserRole.USER)
                .build();
    }
}


// ==========================================
// 🔐 LoginRequest.java - ログインリクエスト
// パス: src/main/java/com/taskflow/domain/user/dto/request/LoginRequest.java
// ==========================================

package com.taskflow.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ログインリクエストDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "正しいメールアドレス形式ではありません")
    private String email;

    @NotBlank(message = "パスワードは必須です")
    private String password;
}


// ==========================================
// 📤 UserResponse.java - ユーザー応答
// パス: src/main/java/com/taskflow/domain/user/dto/response/UserResponse.java
// ==========================================

package com.taskflow.domain.user.dto.response;

import com.taskflow.domain.user.entity.User;
import com.taskflow.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ユーザー情報応答DTO
 * Entityを直接公開せず、必要な情報のみ返却
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private UserRole role;
    private LocalDateTime createdAt;

    /**
     * Entity → DTO変換
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}


// ==========================================
// 🔑 TokenResponse.java - トークン応答
// パス: src/main/java/com/taskflow/domain/user/dto/response/TokenResponse.java
// ==========================================

package com.taskflow.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * ログイン成功時に返却されるJWTトークンDTO
 */
@Getter
@AllArgsConstructor
@Builder
public class TokenResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn; // 有効期限（秒）

    public static TokenResponse of(String accessToken, Long expiresIn) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}


// ==========================================
// 📦 UserRepository.java - ユーザーリポジトリ
// パス: src/main/java/com/taskflow/domain/user/repository/UserRepository.java
// ==========================================

package com.taskflow.domain.user.repository;

import com.taskflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ユーザーデータアクセスインターフェース
 * JpaRepositoryが基本CRUDメソッドを提供
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * メールアドレスでユーザーを検索
     */
    Optional<User> findByEmail(String email);

    /**
     * メールアドレス重複確認
     */
    boolean existsByEmail(String email);
}


// ==========================================
// 📦 UserService.java - ユーザーサービス
// パス: src/main/java/com/taskflow/domain/user/service/UserService.java
// ==========================================

package com.taskflow.domain.user.service;

import com.taskflow.domain.user.dto.request.LoginRequest;
import com.taskflow.domain.user.dto.request.SignUpRequest;
import com.taskflow.domain.user.dto.response.TokenResponse;
import com.taskflow.domain.user.dto.response.UserResponse;
import com.taskflow.domain.user.entity.User;
import com.taskflow.domain.user.repository.UserRepository;
import com.taskflow.global.exception.BusinessException;
import com.taskflow.global.exception.ErrorCode;
import com.taskflow.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザー関連ビジネスロジック
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 会員登録
     */
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        log.info("会員登録試行: {}", request.getEmail());

        // メールアドレス重複確認
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // パスワード暗号化
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // ユーザー作成と保存
        User user = request.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        log.info("会員登録成功: userId={}", savedUser.getId());
        return UserResponse.from(savedUser);
    }

    /**
     * ログイン
     */
    public TokenResponse login(LoginRequest request) {
        log.info("ログイン試行: {}", request.getEmail());

        // ユーザー照会
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        // パスワード検証
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // JWTトークン生成
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        Long expiresIn = jwtTokenProvider.getAccessTokenValidity();

        log.info("ログイン成功: userId={}", user.getId());
        return TokenResponse.of(accessToken, expiresIn);
    }

    /**
     * ユーザー照会（IDで）
     */
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        return UserResponse.from(user);
    }

    /**
     * 自分の情報照会
     */
    public UserResponse getMyInfo(Long userId) {
        return getUserById(userId);
    }

    /**
     * IDでUser Entity照会（内部使用）
     * 他のサービスでUserを参照する時使用
     */
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}


// ==========================================
// 📦 UserController.java - ユーザーAPI
// パス: src/main/java/com/taskflow/domain/user/controller/UserController.java
// ==========================================

package com.taskflow.domain.user.controller;

import com.taskflow.domain.user.dto.request.LoginRequest;
import com.taskflow.domain.user.dto.request.SignUpRequest;
import com.taskflow.domain.user.dto.response.TokenResponse;
import com.taskflow.domain.user.dto.response.UserResponse;
import com.taskflow.domain.user.service.UserService;
import com.taskflow.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * ユーザー関連APIコントローラー
 */
@Tag(name = "User", description = "ユーザーAPI")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 会員登録
     */
    @Operation(summary = "会員登録", description = "新しいユーザーを登録します")
    @PostMapping("/signup")
    public ApiResponse<UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        UserResponse response = userService.signUp(request);
        return ApiResponse.success("会員登録が完了しました", response);
    }

    /**
     * ログイン
     */
    @Operation(summary = "ログイン", description = "メールアドレスとパスワードでログインし、JWTトークンを発行します")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = userService.login(request);
        return ApiResponse.success("ログイン成功", response);
    }

    /**
     * 自分の情報照会
     */
    @Operation(summary = "自分の情報照会", description = "現在ログイン中のユーザー情報を照会します")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(@AuthenticationPrincipal Long userId) {
        UserResponse response = userService.getMyInfo(userId);
        return ApiResponse.success(response);
    }

    /**
     * 特定ユーザー照会
     */
    @Operation(summary = "ユーザー照会", description = "IDでユーザー情報を照会します")
    @GetMapping("/users/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ApiResponse.success(response);
    }
}
