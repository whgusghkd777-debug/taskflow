# 🚀 TaskFlow - 完全版プロジェクトファイル

日本のIT企業面接用 Spring Boot プロジェクト

---

## 📂 プロジェクト構造

```
taskflow/
├── src/
│   ├── main/
│   │   ├── java/com/taskflow/
│   │   │   ├── domain/
│   │   │   │   ├── user/
│   │   │   │   ├── task/
│   │   │   │   └── team/
│   │   │   ├── global/
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── exception/
│   │   │   │   └── common/
│   │   │   └── TaskflowApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── build.gradle
└── README.md
```

---

## 📄 ファイル一覧

### 1. ビルド設定
- `build.gradle` - Gradle依存関係設定
- `application.yml` - アプリケーション設定

### 2. メインクラス
- `TaskflowApplication.java` - Spring Boot起動クラス

### 3. 共通クラス (global/common/)
- `BaseEntity.java` - 共通エンティティ
- `ApiResponse.java` - 統一されたAPI応答フォーマット

### 4. 例外処理 (global/exception/)
- `ErrorCode.java` - エラーコード定義
- `BusinessException.java` - ビジネス例外
- `ErrorResponse.java` - エラー応答DTO
- `GlobalExceptionHandler.java` - グローバル例外ハンドラー

### 5. セキュリティ (global/security/)
- `SecurityConfig.java` - Spring Security設定
- `JwtTokenProvider.java` - JWT生成・検証
- `JwtAuthenticationFilter.java` - JWT認証フィルター
- `CustomUserDetailsService.java` - ユーザー認証サービス

### 6. 設定 (global/config/)
- `JpaConfig.java` - JPA設定
- `SwaggerConfig.java` - Swagger設定
- `WebConfig.java` - CORS設定

### 7. ユーザードメイン (domain/user/)
**Entity:**
- `User.java` - ユーザーエンティティ
- `UserRole.java` - ユーザー権限Enum

**DTO:**
- `SignUpRequest.java` - 会員登録リクエスト
- `LoginRequest.java` - ログインリクエスト
- `UserResponse.java` - ユーザー応答
- `TokenResponse.java` - トークン応答

**Repository:**
- `UserRepository.java` - ユーザーリポジトリ

**Service:**
- `UserService.java` - ユーザーサービス

**Controller:**
- `UserController.java` - ユーザーAPI

### 8. チームドメイン (domain/team/)
**Entity:**
- `Team.java` - チームエンティティ
- `TeamMember.java` - チームメンバー
- `TeamRole.java` - チーム権限Enum

**DTO:**
- `TeamRequest.java` - チームリクエスト
- `TeamResponse.java` - チーム応答

**Repository:**
- `TeamRepository.java` - チームリポジトリ
- `TeamMemberRepository.java` - チームメンバーリポジトリ

**Service:**
- `TeamService.java` - チームサービス

**Controller:**
- `TeamController.java` - チームAPI

### 9. タスクドメイン (domain/task/)
**Entity:**
- `Task.java` - タスクエンティティ
- `TaskStatus.java` - タスク状態Enum
- `Priority.java` - 優先度Enum

**DTO:**
- `TaskRequest.java` - タスクリクエスト
- `TaskResponse.java` - タスク応答

**Repository:**
- `TaskRepository.java` - タスクリポジトリ

**Service:**
- `TaskService.java` - タスクサービス

**Controller:**
- `TaskController.java` - タスクAPI

### 10. テスト
- `UserServiceTest.java` - ユーザーサービステスト
- `TaskControllerTest.java` - タスクAPI統合テスト

### 11. ドキュメント
- `README.md` - プロジェクト説明（日本語）

---

## 🎯 セットアップ手順

### 1. プロジェクト作成
1. https://start.spring.io/ にアクセス
2. 以下の設定を選択:
   - Project: Gradle - Groovy
   - Language: Java
   - Spring Boot: 3.2.0
   - Java: 17
   - Dependencies: Web, JPA, Security, MySQL Driver, Lombok, Validation

### 2. データベース作成
```sql
CREATE DATABASE taskflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 設定ファイル修正
`application.yml`の以下の部分を修正:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskflow
    username: あなたのユーザー名
    password: あなたのパスワード
```

### 4. ビルド & 実行
```bash
./gradlew clean build
./gradlew bootRun
```

### 5. API確認
ブラウザで以下にアクセス:
```
http://localhost:8080/swagger-ui.html
```

---

## 📋 次にすること

1. ✅ 上記のフォルダ構造を作成
2. ✅ 各ファイルに以下のコードをコピー&ペースト
3. ✅ MySQLデータベースを作成
4. ✅ アプリケーションを実行
5. ✅ Swagger UIでAPIをテスト

---

## 💡 日本企業面接のポイント

### 技術的な工夫 (工夫した点)
1. **N+1問題の解決** - Fetch Joinを使用
2. **同時実行制御** - @Versionで楽観的ロック
3. **セキュリティ強化** - JWT + BCrypt

### 苦労した点
1. JWT トークン管理
2. Spring Security 6の新しいAPI習得
3. N+1クエリ問題の発見と解決

### 今後の改善計画
1. Redisキャッシング追加
2. WebSocketリアルタイム通知
3. Docker Compose デプロイ自動化

---

## 📞 重要な注意事項

### JWT Secret Key
本番環境では必ず環境変数で管理してください:
```yaml
jwt:
  secret: ${JWT_SECRET}
```

### データベースパスワード
application.ymlにパスワードを直接書かないでください。
環境変数を使用:
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

---

## 🎓 学習ポイント

このプロジェクトで学べる技術:
- ✅ Spring Boot 3.x 最新機能
- ✅ Spring Security JWT認証
- ✅ JPA / Hibernate ORM
- ✅ REST API設計
- ✅ 例外処理戦略
- ✅ テスト駆動開発 (TDD)
- ✅ Clean Architecture

---

## 📚 参考資料

- Spring Boot公式ドキュメント
- Spring Security リファレンス
- JWT.io - JWTについて
- Swagger OpenAPI 仕様

---

次のページから、実際のコードファイルが始まります！
全てのコメントとメッセージは日本語で書かれています。

面接頑張ってください！ 🇯🇵💼
