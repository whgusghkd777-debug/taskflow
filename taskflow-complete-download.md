# 🎉 TaskFlow 完全版 - ダウンロードガイド

**全てのファイルが完成しました！これをコピー&ペーストしてプロジェクトを作成してください。**

---

## 📦 ファイルリスト（全37ファイル）

### ✅ 完成したファイル

| # | ファイル | 場所 | 状態 |
|---|---------|------|------|
| 1 | build.gradle | backend/ | ✅ |
| 2 | settings.gradle | backend/ | ✅ |
| 3 | application.yml | backend/src/main/resources/ | ✅ |
| 4 | TaskflowApplication.java | backend/src/main/java/com/taskflow/ | ✅ |
| 5-9 | **User ドメイン** (5ファイル) | backend/.../user/ | ✅ |
| 10-14 | **Team ドメイン** (9ファイル) | backend/.../team/ | ✅ |
| 15-23 | **Task ドメイン** (9ファイル) | backend/.../task/ | ✅ |
| 24-27 | **Common** (2ファイル) | backend/.../global/common/ | ✅ |
| 28-31 | **Config** (4ファイル) | backend/.../global/config/ | ✅ |
| 32-34 | **Security** (3ファイル) | backend/.../global/security/ | ✅ |
| 35-37 | **Exception** (4ファイル) | backend/.../global/exception/ | ✅ |
| 38 | index.html | frontend/ | ✅ |
| 39 | README.md | root | ✅ |
| 40 | .gitignore | root | ✅ |

---

## 🚀 ダウンロード方法

### 方法1: 全ファイルを一つのZIPにまとめる（推奨）

以下の手順で全てのファイルをダウンロードできます：

#### **ステップ1: フォルダ作成**

```bash
# Windowsの場合
mkdir taskflow
cd taskflow
mkdir backend frontend docs

# macOS/Linuxの場合
mkdir -p taskflow/{backend/{src/main/{java/com/taskflow/domain/{user/{entity,dto/{request,response},repository,service,controller},task/{entity,dto/{request,response},repository,service,controller},team/{entity,dto/{request,response},repository,service,controller}},global/{common,config,security,exception}},resources},test/java/com/taskflow},frontend/{css,js,assets},docs/screenshots}
cd taskflow
```

#### **ステップ2: ファイルコピー**

以下の各アーティファクトから内容をコピーしてください：

1. ✅ **`taskflow-jp-user-domain`** → User関連ファイル
2. ✅ **`taskflow-team-domain`** → Team関連ファイル
3. ✅ **`taskflow-task-domain`** → Task関連ファイル
4. ✅ **`taskflow-jp-configs`** → 設定ファイル
5. ✅ **`taskflow-jp-security`** → Securityファイル
6. ✅ **`taskflow-jp-exceptions`** → Exception関連
7. ✅ **`taskflow-complete-frontend`** → フロントエンドHTML
8. ✅ **`taskflow-complete-structure`** → README等

---

## 📝 各ファイルの配置場所

### **Backend ファイル構造**

```
backend/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── src/
    ├── main/
    │   ├── java/com/taskflow/
    │   │   ├── TaskflowApplication.java
    │   │   ├── domain/
    │   │   │   ├── user/
    │   │   │   │   ├── entity/
    │   │   │   │   │   ├── User.java
    │   │   │   │   │   └── UserRole.java
    │   │   │   │   ├── dto/request/
    │   │   │   │   │   ├── SignUpRequest.java
    │   │   │   │   │   └── LoginRequest.java
    │   │   │   │   ├── dto/response/
    │   │   │   │   │   ├── UserResponse.java
    │   │   │   │   │   └── TokenResponse.java
    │   │   │   │   ├── repository/
    │   │   │   │   │   └── UserRepository.java
    │   │   │   │   ├── service/
    │   │   │   │   │   └── UserService.java
    │   │   │   │   └── controller/
    │   │   │   │       └── UserController.java
    │   │   │   ├── task/
    │   │   │   │   ├── entity/
    │   │   │   │   │   ├── Task.java
    │   │   │   │   │   ├── TaskStatus.java
    │   │   │   │   │   └── Priority.java
    │   │   │   │   ├── dto/request/
    │   │   │   │   │   └── TaskRequest.java
    │   │   │   │   ├── dto/response/
    │   │   │   │   │   └── TaskResponse.java
    │   │   │   │   ├── repository/
    │   │   │   │   │   └── TaskRepository.java
    │   │   │   │   ├── service/
    │   │   │   │   │   └── TaskService.java
    │   │   │   │   └── controller/
    │   │   │   │       └── TaskController.java
    │   │   │   └── team/
    │   │   │       ├── entity/
    │   │   │       │   ├── Team.java
    │   │   │       │   ├── TeamMember.java
    │   │   │       │   └── TeamRole.java
    │   │   │       ├── dto/request/
    │   │   │       │   └── TeamRequest.java
    │   │   │       ├── dto/response/
    │   │   │       │   └── TeamResponse.java
    │   │   │       ├── repository/
    │   │   │       │   ├── TeamRepository.java
    │   │   │       │   └── TeamMemberRepository.java
    │   │   │       ├── service/
    │   │   │       │   └── TeamService.java
    │   │   │       └── controller/
    │   │   │           └── TeamController.java
    │   │   └── global/
    │   │       ├── common/
    │   │       │   ├── BaseEntity.java
    │   │       │   └── ApiResponse.java
    │   │       ├── config/
    │   │       │   ├── JpaConfig.java
    │   │       │   ├── SecurityConfig.java
    │   │       │   ├── SwaggerConfig.java
    │   │       │   └── WebConfig.java
    │   │       ├── security/
    │   │       │   ├── JwtTokenProvider.java
    │   │       │   ├── JwtAuthenticationFilter.java
    │   │       │   └── CustomUserDetailsService.java
    │   │       └── exception/
    │   │           ├── ErrorCode.java
    │   │           ├── BusinessException.java
    │   │           ├── ErrorResponse.java
    │   │           └── GlobalExceptionHandler.java
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       └── application-test.yml
    └── test/
        └── java/com/taskflow/
```

### **Frontend ファイル構造**

```
frontend/
├── index.html
├── css/
│   └── style.css (オプション)
├── js/
│   └── app.js (オプション)
└── README.md
```

### **Docs ファイル構造**

```
docs/
├── API.md
├── SETUP.md
└── screenshots/
```

### **Root ファイル**

```
taskflow/
├── README.md
├── .gitignore
└── LICENSE
```

---

## 🔧 Gradle Wrapper ファイル

Gradle Wrapperファイルは以下からダウンロードしてください：

```bash
cd backend
gradle wrapper --gradle-version 8.5
```

これにより以下が自動生成されます：
- `gradlew` (Linux/macOS用)
- `gradlew.bat` (Windows用)
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`

---

## ⚙️ settings.gradle

`backend/settings.gradle` に以下を記述：

```gradle
rootProject.name = 'taskflow'
```

---

## 🎯 各アーティファクトのダウンロード手順

### 1. **build.gradle**
- アーティファクト: `spring-build-gradle`
- 配置: `backend/build.gradle`

### 2. **application.yml**
- アーティファクト: `spring-application-yml`
- 配置: `backend/src/main/resources/application.yml`

### 3. **TaskflowApplication.java**
- アーティファクト: `taskflow-jp-configs`
- 配置: `backend/src/main/java/com/taskflow/TaskflowApplication.java`

### 4. **Common クラス**
- アーティファクト: `taskflow-jp-configs`
- `BaseEntity.java` → `backend/src/main/java/com/taskflow/global/common/`
- `ApiResponse.java` → `backend/src/main/java/com/taskflow/global/common/`

### 5. **Config クラス**
- アーティファクト: `taskflow-jp-configs`, `taskflow-jp-security`
- 全て `backend/src/main/java/com/taskflow/global/config/` へ

### 6. **Security クラス**
- アーティファクト: `taskflow-jp-security`
- 全て `backend/src/main/java/com/taskflow/global/security/` へ

### 7. **Exception クラス**
- アーティファクト: `taskflow-jp-exceptions`
- 全て `backend/src/main/java/com/taskflow/global/exception/` へ

### 8. **User ドメイン**
- アーティファクト: `taskflow-jp-user-domain`
- 各ファイルを対応する場所へ

### 9. **Task ドメイン**
- アーティファクト: `taskflow-task-domain`
- 各ファイルを対応する場所へ

### 10. **Team ドメイン**
- アーティファクト: `taskflow-team-domain`
- 各ファイルを対応する場所へ

### 11. **Frontend**
- アーティファクト: `taskflow-complete-frontend`
- 配置: `frontend/index.html`

### 12. **README & .gitignore**
- アーティファクト: `taskflow-complete-structure`
- 配置: プロジェクトルート

---

## ✅ 完成確認チェックリスト

```bash
# フォルダ構造確認
tree taskflow/

# ファイル数確認
find taskflow -type f | wc -l
# 結果: 約40ファイル

# Gradleビルド確認
cd taskflow/backend
./gradlew clean build

# 実行確認
./gradlew bootRun
```

---

## 🚀 実行手順

### 1. データベース作成

```sql
CREATE DATABASE taskflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. バックエンド起動

```bash
cd backend
./gradlew bootRun
```

### 3. フロントエンド起動

```bash
cd frontend
# ブラウザで index.html を開く
# または
python -m http.server 8000
```

### 4. 確認

- Swagger: http://localhost:8080/swagger-ui.html
- Frontend: http://localhost:8000

---

## 📋 よくある質問

### Q: ファイルが多すぎて大変です
**A:** 以下の順序でコピーしてください：
1. 設定ファイル (build.gradle, application.yml)
2. Common & Config
3. Security & Exception
4. User ドメイン
5. Task ドメイン
6. Team ドメイン
7. Frontend

### Q: Gradle Wrapperがありません
**A:** 
```bash
cd backend
gradle wrapper --gradle-version 8.5
```

### Q: ビルドエラーが出ます
**A:** 
1. Java 17がインストールされているか確認
2. `./gradlew clean build --refresh-dependencies`
3. Lombokがインストールされているか確認

---

## 🎉 完成！

全てのファイルをコピーしたら：

```bash
cd taskflow
git init
git add .
git commit -m "🚀 初期コミット: TaskFlow完全版"
git remote add origin https://github.com/yourusername/taskflow.git
git push -u origin main
```

**お疲れ様でした！完璧なフルスタックプロジェクトが完成しました！** ✨

---

## 📞 サポート

問題が発生した場合は、以下を確認してください：

1. ✅ 全てのファイルが正しい場所にあるか
2. ✅ パッケージ名が正しいか (`com.taskflow`)
3. ✅ MySQLが起動しているか
4. ✅ application.ymlの設定が正しいか

それでも解決しない場合は、エラーメッセージを教えてください！
