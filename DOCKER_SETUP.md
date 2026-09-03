# Docker Compose セットアップガイド

## 前提条件

- Docker Desktop がインストール済み
- Docker Compose がインストール済み（Docker Desktop に含まれている）

## ファイル構成

```
todolist/
├── backend/
│   ├── Dockerfile                  # Spring Boot アプリケーション用
│   ├── .dockerignore
│   ├── pom.xml
│   └── src/
├── docker-compose.yml              # サービス定義
├── initdb.sql                      # データベース初期化スクリプト
└── README.md
```

## クイックスタート

### 1. アプリケーション起動

```bash
# リポジトリのルートディレクトリで実行
docker-compose up -d
```

**初回起動時:**
- Maven が依存関係をダウンロード
- Docker イメージをビルド
- PostgreSQL が初期化
- Spring Boot アプリケーションが起動

所要時間: 約 2-5 分

### 2. アプリケーションへのアクセス

**API ベースURL**: `http://localhost:8081`

**ヘルスチェック:**
```bash
curl http://localhost:8081/api/v1/tasks
```

### 3. ログ確認

```bash
# 全ログを表示
docker-compose logs -f

# 特定サービスのみ
docker-compose logs -f app
docker-compose logs -f postgres
```

### 4. アプリケーション停止

```bash
docker-compose down
```

データベース削除:
```bash
docker-compose down -v
```

## サービス詳細

### PostgreSQL Service

| 項目 | 値 |
|------|-----|
| イメージ | `postgres:15-alpine` |
| コンテナ名 | `todolist-db` |
| ホスト | localhost |
| ポート | 5433（ホスト） / 5432（コンテナ） |
| データベース | todolist |
| ユーザー | postgres |
| パスワード | postgres |
| ボリューム | `postgres_data` (永続化) |

**接続例:**
```bash
psql -h localhost -U postgres -d todolist
```

### Spring Boot Service

| 項目 | 値 |
|------|-----|
| イメージ | カスタムビルド |
| コンテナ名 | `todolist-app` |
| ポート | 8081（ホスト） / 8080（コンテナ） |
| Java バージョン | 25 |
| ビルド方式 | マルチステージビルド |

## 環境変数

### PostgreSQL

| 変数 | 値 |
|------|-----|
| `POSTGRES_DB` | todolist |
| `POSTGRES_USER` | postgres |
| `POSTGRES_PASSWORD` | postgres |

### Spring Boot

| 変数 | 値 | 説明 |
|------|-----|------|
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://postgres:5432/todolist | DB接続先 |
| `SPRING_DATASOURCE_USERNAME` | postgres | DB ユーザー |
| `SPRING_DATASOURCE_PASSWORD` | postgres | DB パスワード |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | validate | DDL 自動生成（検証のみ） |
| `SPRING_JPA_SHOW_SQL` | false | SQL ログ出力 OFF |
| `LOGGING_LEVEL_ROOT` | INFO | ルートログレベル |
| `LOGGING_LEVEL_COM_EXAMPLE_TODOLIST` | DEBUG | アプリケーションログレベル |

## ネットワーク構成

- **ネットワーク名**: `todolist-network` (bridge)
- **コンテナ間通信**: 
  - `app` → `postgres` (ホスト名: `postgres`)
  - 外部アクセス: `localhost:8081`, `localhost:5433`

## ヘルスチェック

PostgreSQL に対してヘルスチェック実装:
- 実行コマンド: `pg_isready -U postgres`
- 間隔: 10秒
- タイムアウト: 5秒
- リトライ回数: 5回

Spring Boot はヘルスチェック成功後に起動します。

## 開発時のカスタマイズ

### ログレベルの変更

`docker-compose.yml` の環境変数を変更:

```yaml
LOGGING_LEVEL_ROOT: DEBUG  # より詳細なログを出力
```

### ポート番号の変更

```yaml
# PostgreSQL ポート変更例
ports:
  - "5433:5432"  # ローカル 5433 でアクセス

# Spring Boot ポート変更例
ports:
  - "8081:8080"  # ローカル 8081 でアクセス
```

### 永続化の無効化

データベースをコンテナ削除時にリセット:
```bash
docker-compose down  # -v オプションなし
```

## トラブルシューティング

### ポートが既に使用されている

```bash
# 既存のコンテナを停止
docker-compose down

# または別のポートを使用
docker-compose -f docker-compose.yml up -d
# docker-compose.yml のポート設定を変更
```

### ビルドキャッシュをクリア

```bash
docker-compose build --no-cache
```

### データベース接続失敗

```bash
# PostgreSQL のステータス確認
docker-compose logs postgres

# ヘルスチェック実行
docker exec todolist-db pg_isready -U postgres
```

### Spring Boot アプリケーションが起動しない

```bash
# ログ確認
docker-compose logs app

# 詳細ログ出力
docker-compose logs -f --tail=50 app
```

## プロダクション環境への適用

以下の変更が推奨されます:

1. **パスワード管理**
   - 環境変数ファイルを使用 (`.env` ファイル)
   - シークレット管理ツール (AWS Secrets Manager など)

2. **イメージセキュリティ**
   - 非root ユーザーで実行 ✓ (既に実装)
   - イメージスキャン (Trivy など)

3. **リソース制限**
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1'
         memory: 1G
       reservations:
         cpus: '0.5'
         memory: 512M
   ```

4. **ログ集約**
   - ELK Stack、Splunk など

5. **バックアップ戦略**
   - 定期的なデータベースバックアップ

## よくある質問

**Q: データベースの永続化について**  
A: `postgres_data` ボリュームにより永続化されます。`docker-compose down -v` で削除されます。

**Q: ソースコード変更後は？**  
A: `docker-compose up --build` で再ビルド

**Q: 複数の環境を実行したい**  
A: `docker-compose -f docker-compose.yml -p todolist-dev up` などでプロジェクト名指定
