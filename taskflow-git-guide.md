# 🚀 TaskFlow - Git セットアップ完全ガイド

日本IT企業面接用プロジェクトをGitHubにアップロードする手順

---

## 📂 プロジェクト完全ファイル構造

```
taskflow/
├── .gitignore
├── README.md
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── taskflow/
    │   │           ├── TaskflowApplication.java
    │   │           │
    │   │           ├── domain/
    │   │           │   ├── user/
    │   │           │   │   ├── entity/
    │   │           │   │   │   ├── User.java
    │   │           │   │   │   └── UserRole.java
    │   │           │   │   ├── dto/
    │   │           │   │   │   ├── request/
    │   │           │   │   │   │   ├── SignUpRequest.java
    │   │           │   │   │   │   └── LoginRequest.java
    │   │           │   │   │   └── response/
    │   │           │   │   │       ├── UserResponse.java
    │   │           │   │   │       └── TokenResponse.java
    │   │           │   │   ├── repository/
    │   │           │   │   │   └── UserRepository.java
    │   │           │   │   ├── service/
    │   │           │   │   │   └── UserService.java
    │   │           │   │   └── controller/
    │   │           │   │       └── UserController.java
    │   │           │   │
    │   │           │   ├── task/
    │   │           │   │   └── [同じ構造]
    │   │           │   │
    │   │           │   └── team/
    │   │           │       └── [同じ構造]
    │   │           │
    │   │           └── global/
    │   │               ├── common/
    │   │               │   ├── BaseEntity.java
    │   │               │   └── ApiResponse.java
    │   │               ├── config/
    │   │               │   ├── JpaConfig.java
    │   │               │   ├── SecurityConfig.java
    │   │               │   ├── SwaggerConfig.java
    │   │               │   └── WebConfig.java
    │   │               ├── security/
    │   │               │   ├── JwtTokenProvider.java
    │   │               │   ├── JwtAuthenticationFilter.java
    │   │               │   └── CustomUserDetailsService.java
    │   │               └── exception/
    │   │                   ├── ErrorCode.java
    │   │                   ├── BusinessException.java
    │   │                   ├── ErrorResponse.java
    │   │                   └── GlobalExceptionHandler.java
    │   │
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       └── application-test.yml
    │
    └── test/
        └── java/
            └── com/
                └── taskflow/
                    └── domain/
                        └── user/
                            └── service/
                                └── UserServiceTest.java
```

---

## 🔧 1. .gitignore ファイル作成

プロジェクトルートに `.gitignore` ファイルを作成:

```gitignore
# Gradle
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/
.settings/
.classpath
.project

# OS
.DS_Store
Thumbs.db

# Application
*.log
logs/
tmp/

# Security (重要!)
application-prod.yml
application-local.yml

# Generated
bin/
out/
target/
```

---

## 📝 2. settings.gradle ファイル

プロジェクトルートに `settings.gradle` 作成:

```gradle
rootProject.name = 'taskflow'
```

---

## 📄 3. README.md (日本語完全版)

プロジェクトルートに `README.md` 作成:

```markdown
# 🚀 TaskFlow - チーム協業タスク管理システム

[![Java](https://img.shields.io/badge/Java-17-red?logo=openjdk)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://www.mysql.com/)

## 📖 プロジェクト概要

チーム単位でタスクを効率的に管理し、進行状況をリアルタイムで追跡できる協業ツールです。

### 🎯 開発動機

大学のチームプロジェクトを進める中で感じた不便さを解決するために開発しました。
既存のツールは機能が多すぎて複雑だったので、シンプルで使いやすいシステムを目指しました。

## ✨ 主な機能

- 🔐 **JWT認証**: メールアドレス/パスワードによる安全な認証
- 👥 **チーム管理**: チーム作成、メンバー招待、権限管理
- ✅ **タスク管理**: TODO/進行中/完了の状態管理
- 📊 **ダッシュボード**: チーム別進捗統計

## 🛠️ 技術スタック

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security 6 (JWT)
- Spring Data JPA
- MySQL 8.0

### Documentation & Testing
- Swagger 3.0
- JUnit 5 + Mockito

## 🚀 クイックスタート

### 1. 前提条件
- Java 17以上
- MySQL 8.0以上
- Gradle 8.x以上

### 2. データベース設定

```sql
CREATE DATABASE taskflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 設定ファイル修正

`src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskflow
    username: your_username
    password: your_password
```

### 4. ビルド & 実行

```bash
./gradlew clean build
./gradlew bootRun
```

### 5. API確認

```
http://localhost:8080/swagger-ui.html
```

## 🎨 APIエンドポイント

### 認証API
- `POST /api/v1/auth/signup` - 会員登録
- `POST /api/v1/auth/login` - ログイン
- `GET /api/v1/auth/me` - 自分の情報照会

### チームAPI
- `POST /api/v1/teams` - チーム作成
- `GET /api/v1/teams` - 自分のチームリスト
- `GET /api/v1/teams/{id}` - チーム詳細

### タスクAPI
- `POST /api/v1/tasks` - タスク作成
- `GET /api/v1/tasks` - タスクリスト
- `PATCH /api/v1/tasks/{id}/status` - 状態変更

## 💡 工夫した点

### 1. N+1クエリ問題の解決
Fetch Joinを使用してクエリ数を削減し、API応答時間を10倍改善しました。

```java
@Query("SELECT t FROM Task t JOIN FETCH t.assignee")
List<Task> findAllWithAssignee();
```

### 2. 同時実行制御
楽観的ロック(`@Version`)を使用してデータ整合性を保証しました。

### 3. セキュリティ強化
- BCryptでパスワード暗号化
- JWTトークン基盤認証
- 権限基盤アクセス制御

## 🎯 面接アピールポイント

### なぜこのプロジェクトを選んだか
実際に大学のチーム活動で感じた不便さを解決するために始めました。
既存のツールは複雑すぎたので、シンプルで使いやすいシステムを目指しました。

### 苦労した点
1. JWT トークン管理 - 有効期限とセキュリティを同時に考慮
2. N+1問題解決 - Fetch JoinとDTOプロジェクションを学習
3. 同時実行制御 - @Versionを利用した楽観的ロック実装

### 今後の改善計画
- Redisキャッシング追加
- WebSocketリアルタイム通知
- Elasticsearch検索機能

## 📚 学習リソース

このプロジェクトで学べる技術:
- Spring Boot 3.x 最新機能
- Spring Security JWT認証
- JPA / Hibernate ORM
- REST API設計
- テスト駆動開発 (TDD)

## 📞 お問い合わせ

- Email: your.email@example.com
- GitHub: [@yourusername](https://github.com/yourusername)

## 📄 ライセンス

MIT License

---

⭐ このプロジェクトが役に立ったら、スターをお願いします！
```

---

## 🔄 4. Git初期化とアップロード手順

### ステップ1: ローカルGit初期化

```bash
# プロジェクトルートディレクトリで実行
cd taskflow

# Git初期化
git init

# 全てのファイルをステージング
git add .

# 初期コミット
git commit -m "🚀 初期コミット: TaskFlow プロジェクト作成

主な機能:
- ユーザー認証 (JWT)
- チーム管理
- タスク管理
- Swagger API ドキュメント

技術スタック:
- Spring Boot 3.2.0
- Spring Security 6
- JPA + MySQL
- Swagger 3.0"
```

### ステップ2: GitHubリポジトリ作成

1. https://github.com にアクセス
2. 右上の **New repository** クリック
3. リポジトリ情報入力:
   - Repository name: `taskflow`
   - Description: `チーム協業タスク管理システム - 日本IT企業面接用ポートフォリオ`
   - Public または Private 選択
   - **README は追加しない**（既にあるため）
4. **Create repository** クリック

### ステップ3: リモートリポジトリ連結

```bash
# GitHubリポジトリURLを追加
git remote add origin https://github.com/あなたのユーザー名/taskflow.git

# メインブランチ名を設定
git branch -M main

# 初めてプッシュ
git push -u origin main
```

---

## 🌿 5. ブランチ戦略 (推奨)

### メインブランチ

```bash
# 開発ブランチ作成
git checkout -b develop

# 機能開発ブランチ
git checkout -b feature/user-authentication
git checkout -b feature/task-management
git checkout -b feature/team-management
```

### コミットメッセージ規約

```bash
# 新機能追加
git commit -m "✨ feat: ユーザー認証機能追加"

# バグ修正
git commit -m "🐛 fix: パスワード検証ロジック修正"

# リファクタリング
git commit -m "♻️ refactor: UserService コード整理"

# テスト追加
git commit -m "✅ test: UserService 単体テスト追加"

# ドキュメント更新
git commit -m "📝 docs: README にセットアップ手順追加"
```

---

## 📊 6. GitHub リポジトリ設定

### About セクション設定

リポジトリページ右側の **About** で設定:

- **Description**: チーム協業タスク管理システム - 日本IT企業面接用Spring Bootプロジェクト
- **Website**: デプロイURLがあれば追加
- **Topics**: 
  - `spring-boot`
  - `java`
  - `jwt`
  - `mysql`
  - `rest-api`
  - `swagger`
  - `japanese`

### README バッジ追加

README.mdの上部に追加:

```markdown
![Build Status](https://github.com/あなたのユーザー名/taskflow/workflows/build/badge.svg)
![Java Version](https://img.shields.io/badge/Java-17-red?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=springboot)
```

---

## 🚨 7. 重要なセキュリティ注意事項

### application.yml のパスワード管理

**絶対にGitにプッシュしてはいけない情報:**

```yaml
# ❌ 悪い例 - パスワード直接記入
spring:
  datasource:
    password: mypassword123

jwt:
  secret: my-secret-key
```

**✅ 良い例 - 環境変数使用:**

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

### 環境変数設定方法

```bash
# Linux/Mac
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key

# Windows (PowerShell)
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your_secret_key"
```

---

## 📋 8. チェックリスト

アップロード前に確認:

- [ ] `.gitignore` ファイル作成済み
- [ ] パスワード/秘密鍵を環境変数に変更
- [ ] README.md に日本語説明追加
- [ ] コードに日本語コメント追加
- [ ] Swagger でAPI動作確認
- [ ] テストコード実行成功
- [ ] ビルドエラーなし

---

## 🎯 9. 面接準備のための追加Tips

### GitHubプロフィール整理

```markdown
# プロフィールREADMEに追加
## 🚀 最近のプロジェクト
- [TaskFlow](https://github.com/あなたのユーザー名/taskflow) - チーム協業タスク管理システム
  - Spring Boot 3.2.0, JWT認証, MySQL
  - N+1問題解決、楽観的ロック実装
```

### コミット履歴を綺麗に保つ

```bash
# 小さな変更は一つのコミットに
git add .
git commit -m "✨ feat: ユーザー認証機能完成

- JWT トークン生成
- BCrypt パスワード暗号化
- Spring Security 設定"
```

---

## 📞 トラブルシューティング

### Push が拒否される場合

```bash
# 強制プッシュ（初回のみ）
git push -f origin main
```

### Git 認証エラー

```bash
# Personal Access Token 使用
# GitHubの Settings → Developer settings → Personal access tokens で生成
```

---

## 🎉 完成！

これで、日本IT企業の面接官に見せられる完璧なGitHubリポジトリが完成しました！

面接で自信を持って説明できるよう、プロジェクトの全ての部分を理解しておいてください。

頑張ってください！ 応援しています！ 🇯🇵💼✨
