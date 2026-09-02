# Docker Compose セットアップ

## 作成ファイル

### 1. docker-compose.yml
**位置**: `c:\Users\sky21\GitHub\todolist\docker-compose.yml`

**サービス定義:**
- **postgres** - PostgreSQL 15 (Alpine)
  - イメージ: `postgres:15-alpine`
  - ポート: 5432
  - DB: todolist
  - ユーザー: postgres
  - パスワード: postgres
  - ボリューム: postgres_data（永続化）
  - ヘルスチェック: pg_isready（10秒間隔）

- **app** - Spring Boot アプリケーション
  - イメージ: マルチステージビルド（Maven + Temurin JRE 21）
  - ポート: 8080
  - 依存関係: postgres（ヘルスチェック完了後に起動）
  - ネットワーク: `todolist-network` (bridge)

### 2. Dockerfile
**位置**: `c:\Users\sky21\GitHub\todolist\backend\Dockerfile`

**特徴:**
- マルチステージビルド（最終イメージサイズ削減）
  - ステージ1（builder）: Maven 3.9.4 + Eclipse Temurin 21
  - ステージ2（runtime）: Eclipse Temurin 21 JRE (Alpine)
- 非root ユーザー実行（セキュリティ）
- JAR を直接実行（app.jar）

### 3. .dockerignore
**位置**: `c:\Users\sky21\GitHub\todolist\backend\.dockerignore`

**除外ファイル:**
- target/（ビルド成果物）
- .git/, .gitignore
- .mvn/, mvnw, mvnw.cmd
- .vscode/, .idea/
- *.log, *.class, *.jar

### 4. initdb.sql
**位置**: `c:\Users\sky21\GitHub\todolist\initdb.sql`

**内容:**
- TASK テーブル
- CATEGORY テーブル
- TAG テーブル
- TASK_EX テーブル
- インデックス定義
- サンプルデータ挿入

## クイックスタート

### 1. アプリケーション起動

```bash
# リポジトリのルートで実行
docker-compose up -d

# または再ビルドを強制
docker-compose up -d --build
```

### 2. アプリケーションへのアクセス

**ヘルスチェック:**
```bash
curl http://localhost:8080/api/v1/tasks
```

**タスク作成:**
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "テスト", "priority": 1}'
```

### 3. ログ確認

```bash
docker-compose logs -f
docker-compose logs -f app
docker-compose logs -f postgres
```

### 4. 停止・削除

```bash
# 停止のみ
docker-compose stop

# 停止・削除
docker-compose down

# ボリューム含めて削除（DB リセット）
docker-compose down -v
```

## ネットワーク構成

```
┌─────────────────────────────────────┐
│       Docker Network                 │
│     (todolist-network)               │
├─────────────────────────────────────┤
│                                      │
│  ┌──────────────┐  ┌──────────────┐ │
│  │   app:8080   │──│ postgres:5432│ │
│  │ (Spring Boot)│  │(PostgreSQL)  │ │
│  └──────────────┘  └──────────────┘ │
│       ↑                ↑              │
│       │                │              │
│    localhost:8080  localhost:5432    │
└─────────────────────────────────────┘
```

## 環境変数

### PostgreSQL

```yaml
POSTGRES_DB: todolist
POSTGRES_USER: postgres
POSTGRES_PASSWORD: postgres
```

### Spring Boot

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/todolist
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: postgres
SPRING_JPA_HIBERNATE_DDL_AUTO: validate
SPRING_JPA_SHOW_SQL: false
LOGGING_LEVEL_ROOT: INFO
LOGGING_LEVEL_COM_EXAMPLE_TODOLIST: DEBUG
```

## 起動順序

1. Docker デーモン起動
2. postgresql コンテナ起動
3. PostgreSQL サービス起動
4. ヘルスチェック実行（10秒間隔、最大5回リトライ）
5. app コンテナ起動（ヘルスチェック成功後）
6. Spring Boot アプリケーション起動
7. ポート 8080 でリッスン開始

**所要時間:** 約 2-5 分（初回ビルド時）

## ボリューム管理

### postgres_data

- **ドライバー**: local
- **マウントポイント**: `/var/lib/postgresql/data`
- **用途**: PostgreSQL データベースの永続化
- **削除**: `docker-compose down -v`

## トラブルシューティング

### ポート競合

```bash
# ポート使用状況確認
netstat -ano | findstr :8080
netstat -ano | findstr :5432

# 別のポートを使用（docker-compose.yml 変更）
ports:
  - "8081:8080"
  - "5433:5432"
```

### ビルドキャッシュクリア

```bash
docker-compose build --no-cache
```

### DB 接続失敗

```bash
# PostgreSQL ステータス確認
docker exec todolist-db pg_isready -U postgres

# ログ確認
docker-compose logs postgres
```

### アプリケーション起動失敗

```bash
# アプリケーションログ確認
docker-compose logs -f app

# コンテナ内で bash 実行
docker exec -it todolist-app bash
```

## プロダクション設定の例

### .env ファイル

```
POSTGRES_DB=todolist
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your-secure-password
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/todolist
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your-secure-password
```

### docker-compose.yml での使用

```yaml
environment:
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
  SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
```

### 実行

```bash
docker-compose --env-file .env up -d
```

## パフォーマンス最適化

### リソース制限

```yaml
app:
  deploy:
    resources:
      limits:
        cpus: '1'
        memory: 1G
      reservations:
        cpus: '0.5'
        memory: 512M
```

### ログドライバー設定

```yaml
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"
```

## CI/CD 統合

### GitHub Actions 例

```yaml
- name: Start Docker Compose
  run: docker-compose up -d --build

- name: Wait for app to be healthy
  run: sleep 30

- name: Run tests
  run: docker-compose exec -T app mvn test
```
