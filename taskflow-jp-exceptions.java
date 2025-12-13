// ==========================================
// 🚨 ErrorCode.java - エラーコード定義
// パス: src/main/java/com/taskflow/global/exception/ErrorCode.java
// ==========================================

package com.taskflow.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * アプリケーション全体で使用するエラーコード
 * HTTPステータスコードとエラーメッセージを一緒に管理
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "不正な入力値です"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "不正なタイプです"),
    
    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "認証が必要です"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "メールアドレスまたはパスワードが正しくありません"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "期限切れのトークンです"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "無効なトークンです"),
    
    // 403 Forbidden
    NO_PERMISSION(HttpStatus.FORBIDDEN, "権限がありません"),
    NOT_TEAM_LEADER(HttpStatus.FORBIDDEN, "チームリーダーのみ実行できます"),
    
    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "チームが見つかりません"),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "タスクが見つかりません"),
    
    // 409 Conflict
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "既に使用されているメールアドレスです"),
    ALREADY_TEAM_MEMBER(HttpStatus.CONFLICT, "既にチームメンバーです"),
    NOT_TEAM_MEMBER(HttpStatus.CONFLICT, "チームメンバーではありません"),
    
    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "サーバー内部エラーが発生しました");

    private final HttpStatus status;
    private final String message;
}


// ==========================================
// ❌ BusinessException.java - ビジネス例外
// パス: src/main/java/com/taskflow/global/exception/BusinessException.java
// ==========================================

package com.taskflow.global.exception;

import lombok.Getter;

/**
 * ビジネスロジックで発生するカスタム例外
 * ErrorCodeを含んで一貫した例外処理を実現
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}


// ==========================================
// 📄 ErrorResponse.java - エラー応答DTO
// パス: src/main/java/com/taskflow/global/exception/ErrorResponse.java
// ==========================================

package com.taskflow.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * エラー応答のためのDTO
 * クライアントに統一された形式のエラー情報を提供
 */
@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private boolean success;
    private int status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    
    @Builder.Default
    private List<FieldErrorDetail> errors = new ArrayList<>();

    /**
     * ErrorCodeからErrorResponseを生成
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .success(false)
                .status(errorCode.getStatus().value())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * カスタムメッセージと一緒にErrorResponseを生成
     */
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .success(false)
                .status(errorCode.getStatus().value())
                .code(errorCode.name())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * バリデーションエラー含むErrorResponseを生成
     */
    public static ErrorResponse of(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return ErrorResponse.builder()
                .success(false)
                .status(errorCode.getStatus().value())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * フィールドエラー詳細情報
     */
    @Getter
    @AllArgsConstructor
    public static class FieldErrorDetail {
        private String field;
        private String value;
        private String reason;

        public static FieldErrorDetail of(FieldError fieldError) {
            return new FieldErrorDetail(
                    fieldError.getField(),
                    fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue().toString(),
                    fieldError.getDefaultMessage()
            );
        }
    }
}


// ==========================================
// 🛡️ GlobalExceptionHandler.java - グローバル例外ハンドラー
// パス: src/main/java/com/taskflow/global/exception/GlobalExceptionHandler.java
// ==========================================

package com.taskflow.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * アプリケーショングローバル例外処理器
 * 全てのコントローラーで発生する例外を一箇所で処理
 * 
 * @RestControllerAdvice: @ControllerAdvice + @ResponseBody
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ビジネスロジック例外処理
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("ビジネス例外発生: {}", e.getMessage());
        
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode);
        
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    /**
     * @Valid検証失敗例外処理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("入力値検証エラー: {}", e.getMessage());
        
        List<ErrorResponse.FieldErrorDetail> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.FieldErrorDetail::of)
                .collect(Collectors.toList());
        
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, errors);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * @ModelAttribute検証失敗例外処理
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("バインディングエラー: {}", e.getMessage());
        
        List<ErrorResponse.FieldErrorDetail> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.FieldErrorDetail::of)
                .collect(Collectors.toList());
        
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, errors);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * タイプ不一致例外処理
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("タイプ不一致エラー: {}", e.getMessage());
        
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Spring Security認証例外処理
     */
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.error("認証エラー: {}", e.getMessage());
        
        ErrorResponse response = ErrorResponse.of(ErrorCode.UNAUTHORIZED);
        
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Spring Security権限例外処理
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("アクセス拒否: {}", e.getMessage());
        
        ErrorResponse response = ErrorResponse.of(ErrorCode.NO_PERMISSION);
        
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    /**
     * 処理されていない全ての例外処理
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("予期しない例外発生: {}", e.getMessage(), e);
        
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
