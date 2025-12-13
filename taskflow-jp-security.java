// ==========================================
// 🔐 SecurityConfig.java - Spring Security設定
// パス: src/main/java/com/taskflow/global/config/SecurityConfig.java
// ==========================================

package com.taskflow.global.config;

import com.taskflow.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security設定
 * JWT基盤認証実装
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Securityフィルターチェーン設定
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF無効化（JWT使用時不要）
            .csrf(AbstractHttpConfigurer::disable)
            
            // セッション使用しない（Stateless）
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // リクエスト権限設定
            .authorizeHttpRequests(auth -> auth
                // 認証なしでアクセス可能なエンドポイント
                .requestMatchers(
                    "/api/v1/auth/signup",
                    "/api/v1/auth/login",
                    "/swagger-ui/**",
                    "/api-docs/**",
                    "/v3/api-docs/**"
                ).permitAll()
                
                // 残りは全て認証必要
                .anyRequest().authenticated()
            )
            
            // JWTフィルター追加
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * パスワード暗号化
     * BCryptアルゴリズム使用（一方向ハッシュ）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


// ==========================================
// 🔑 JwtTokenProvider.java - JWT生成・検証
// パス: src/main/java/com/taskflow/global/security/JwtTokenProvider.java
// ==========================================

package com.taskflow.global.security;

import com.taskflow.domain.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWTトークン生成と検証を担当するクラス
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidity;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidity) {
        
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenValidity = accessTokenValidity;
    }

    /**
     * Access Token生成
     */
    public String createAccessToken(Long userId, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.getKey())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWTトークンからユーザーID抽出
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * JWTトークン有効性検証
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("無効なJWT署名です");
        } catch (ExpiredJwtException e) {
            log.error("期限切れのJWTトークンです");
        } catch (UnsupportedJwtException e) {
            log.error("サポートされていないJWTトークンです");
        } catch (IllegalArgumentException e) {
            log.error("JWTトークンが間違っています");
        }
        return false;
    }

    /**
     * JWTトークンパース
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Access Token有効期間を返却（秒単位）
     */
    public Long getAccessTokenValidity() {
        return accessTokenValidity / 1000;
    }
}


// ==========================================
// 🛡️ JwtAuthenticationFilter.java - JWT認証フィルター
// パス: src/main/java/com/taskflow/global/security/JwtAuthenticationFilter.java
// ==========================================

package com.taskflow.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWTトークンを検証し、認証情報をSecurityContextに保存するフィルター
 * 全てのHTTPリクエスト毎に実行
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. リクエストからJWTトークン抽出
            String jwt = extractToken(request);

            // 2. トークン有効性検証
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                
                // 3. トークンからユーザーID抽出
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

                // 4. 認証オブジェクト生成
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        null
                    );

                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. SecurityContextに認証情報保存
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("JWT認証成功: userId={}", userId);
            }
        } catch (Exception e) {
            log.error("JWT認証失敗: {}", e.getMessage());
        }

        // 6. 次のフィルターへ進行
        filterChain.doFilter(request, response);
    }

    /**
     * HTTPリクエストヘッダーからJWTトークン抽出
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
}


// ==========================================
// 👤 CustomUserDetailsService.java - ユーザー認証サービス
// パス: src/main/java/com/taskflow/global/security/CustomUserDetailsService.java
// ==========================================

package com.taskflow.global.security;

import com.taskflow.domain.user.entity.User;
import com.taskflow.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Securityでユーザー認証に使用するサービス
 * 
 * 注意: 現在のプロジェクトはJWTを使用するため、実際にはほとんど使用されません
 * UserDetailsServiceは主にフォームログインで使用されます
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey()))
        );
    }
}
