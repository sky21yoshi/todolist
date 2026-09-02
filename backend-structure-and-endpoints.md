# TodoList REST API - バックエンド構造とエンドポイント

## プロジェクト概要
- **言語**: Java 17
- **フレームワーク**: Spring Boot 3.1.5
- **ビルドツール**: Maven
- **データベース**: PostgreSQL
- **ORM**: Spring Data JPA

## プロジェクト構成

```
backend/
├── pom.xml                                          # Maven設定
├── README.md                                        # セットアップガイド
├── src/
│   ├── main/
│   │   ├── java/com/example/todolist/
│   │   │   ├── TodolistApplication.java            # アプリケーションエントリポイント
│   │   │   │
│   │   │   ├── controller/                         # REST API コントローラー
│   │   │   │   ├── TaskController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   └── TagController.java
│   │   │   │
│   │   │   ├── service/                            # ビジネスロジック層
│   │   │   │   ├── TaskService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   └── TagService.java
│   │   │   │
│   │   │   ├── repository/                         # データアクセス層
│   │   │   │   ├── TaskRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   └── TagRepository.java
│   │   │   │
│   │   │   ├── entity/                             # エンティティ（データベースモデル）
│   │   │   │   ├── Task.java
│   │   │   │   ├── TaskEx.java
│   │   │   │   ├── Category.java
│   │   │   │   └── Tag.java
│   │   │   │
│   │   │   ├── dto/                                # データ転送オブジェクト
│   │   │   │   ├── TaskRequest.java
│   │   │   │   ├── TaskResponse.java
│   │   │   │   ├── CategoryRequest.java
│   │   │   │   ├── CategoryResponse.java
│   │   │   │   ├── TagRequest.java
│   │   │   │   └── TagResponse.java
│   │   │   │
│   │   │   └── config/                             # 設定ファイル
│   │   │       └── DatabaseConfig.java (将来用)
│   │   │
│   │   └── resources/
│   │       ├── application.properties              # アプリケーション設定
│   │       └── schema.sql (将来用)
│   │
│   └── test/                                        # テストケース（将来用）
│
└── ddl/                                             # データベーススキーマ定義
    ├── task.sql
    ├── task_ex.sql
    └── category.sql
```

## エンティティ（Entity）設計

### Task（タスク基本情報）
| カラム | 型 | 説明 |
|--------|------|------|
| id | SERIAL (PK) | タスク一意識別子 |
| title | CHAR(255) NOT NULL | タスク名 |
| description | TEXT | タスク説明 |
| order | INTEGER | 表示順序 |
| priority | INTEGER | 優先度（0-3） |
| completed | BOOLEAN | 完了状態 |
| created_at | TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | 更新日時 |

### TaskEx（タスク拡張情報）
| カラム | 型 | 説明 |
|--------|------|------|
| task_id | INTEGER (FK, PK) | タスクID（Task テーブルへの外部キー） |
| due_date | TIMESTAMP | 期限日時 |
| category_id | INTEGER (FK) | カテゴリID（Category テーブルへの外部キー） |
| tag_id | INTEGER (FK) | タグID（Tag テーブルへの外部キー） |
| created_at | TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | 更新日時 |

### Category（カテゴリ）
| カラム | 型 | 説明 |
|--------|------|------|
| id | SERIAL (PK) | カテゴリ一意識別子 |
| name | CHAR(100) NOT NULL | カテゴリ名 |
| description | TEXT | カテゴリ説明 |
| created_at | TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | 更新日時 |

### Tag（タグ）
| カラム | 型 | 説明 |
|--------|------|------|
| id | SERIAL (PK) | タグ一意識別子 |
| name | CHAR(100) NOT NULL | タグ名 |
| description | TEXT | タグ説明 |
| created_at | TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | 更新日時 |

## REST API エンドポイント

### Task API

#### 1. 全タスク取得
```
GET /api/v1/tasks
```
**説明**: 全てのタスクをリスト表示
**レスポンス**: 200 OK
```json
[
  {
    "id": 1,
    "title": "タスク1",
    "description": "説明",
    "order": 0,
    "priority": 1,
    "completed": false,
    "dueDate": "2024-12-31T23:59:59",
    "categoryId": 1,
    "categoryName": "カテゴリ名",
    "tagId": 1,
    "tagName": "タグ名",
    "createdAt": "2024-09-02T10:00:00",
    "updatedAt": "2024-09-02T10:00:00"
  }
]
```

#### 2. 特定タスク取得
```
GET /api/v1/tasks/{id}
```
**説明**: 指定されたIDのタスクを取得
**パラメータ**: `id` - タスクID（パス変数）
**レスポンス**: 200 OK（上記の単一オブジェクト）

#### 3. 完了状態でフィルター
```
GET /api/v1/tasks/filter/completed?completed=true|false
```
**説明**: 完了/未完了のタスクをフィルター
**パラメータ**: `completed` - true|false（クエリパラメータ）
**レスポンス**: 200 OK（配列）

#### 4. タスク作成
```
POST /api/v1/tasks
Content-Type: application/json
```
**説明**: 新しいタスクを作成
**リクエストボディ**:
```json
{
  "title": "新規タスク",
  "description": "説明（省略可）",
  "order": 0,
  "priority": 0,
  "completed": false,
  "dueDate": "2024-12-31T23:59:59",
  "categoryId": 1,
  "tagId": 1
}
```
**レスポンス**: 201 Created

#### 5. タスク更新
```
PUT /api/v1/tasks/{id}
Content-Type: application/json
```
**説明**: 指定されたタスクを更新
**パラメータ**: `id` - タスクID（パス変数）
**リクエストボディ**: TaskRequest（上記参照）
**レスポンス**: 200 OK

#### 6. タスク削除
```
DELETE /api/v1/tasks/{id}
```
**説明**: 指定されたタスクを削除
**パラメータ**: `id` - タスクID（パス変数）
**レスポンス**: 204 No Content

---

### Category API

#### 1. 全カテゴリ取得
```
GET /api/v1/categories
```
**説明**: 全てのカテゴリをリスト表示
**レスポンス**: 200 OK
```json
[
  {
    "id": 1,
    "name": "カテゴリ名",
    "description": "説明",
    "createdAt": "2024-09-02T10:00:00",
    "updatedAt": "2024-09-02T10:00:00"
  }
]
```

#### 2. 特定カテゴリ取得
```
GET /api/v1/categories/{id}
```
**説明**: 指定されたIDのカテゴリを取得
**パラメータ**: `id` - カテゴリID（パス変数）
**レスポンス**: 200 OK

#### 3. カテゴリ作成
```
POST /api/v1/categories
Content-Type: application/json
```
**説明**: 新しいカテゴリを作成
**リクエストボディ**:
```json
{
  "name": "新規カテゴリ",
  "description": "説明（省略可）"
}
```
**レスポンス**: 201 Created

#### 4. カテゴリ更新
```
PUT /api/v1/categories/{id}
Content-Type: application/json
```
**説明**: 指定されたカテゴリを更新
**パラメータ**: `id` - カテゴリID（パス変数）
**リクエストボディ**: CategoryRequest
**レスポンス**: 200 OK

#### 5. カテゴリ削除
```
DELETE /api/v1/categories/{id}
```
**説明**: 指定されたカテゴリを削除
**パラメータ**: `id` - カテゴリID（パス変数）
**レスポンス**: 204 No Content

---

### Tag API

#### 1. 全タグ取得
```
GET /api/v1/tags
```
**説明**: 全てのタグをリスト表示
**レスポンス**: 200 OK
```json
[
  {
    "id": 1,
    "name": "タグ名",
    "description": "説明",
    "createdAt": "2024-09-02T10:00:00",
    "updatedAt": "2024-09-02T10:00:00"
  }
]
```

#### 2. 特定タグ取得
```
GET /api/v1/tags/{id}
```
**説明**: 指定されたIDのタグを取得
**パラメータ**: `id` - タグID（パス変数）
**レスポンス**: 200 OK

#### 3. タグ作成
```
POST /api/v1/tags
Content-Type: application/json
```
**説明**: 新しいタグを作成
**リクエストボディ**:
```json
{
  "name": "新規タグ",
  "description": "説明（省略可）"
}
```
**レスポンス**: 201 Created

#### 4. タグ更新
```
PUT /api/v1/tags/{id}
Content-Type: application/json
```
**説明**: 指定されたタグを更新
**パラメータ**: `id` - タグID（パス変数）
**リクエストボディ**: TagRequest
**レスポンス**: 200 OK

#### 5. タグ削除
```
DELETE /api/v1/tags/{id}
```
**説明**: 指定されたタグを削除
**パラメータ**: `id` - タグID（パス変数）
**レスポンス**: 204 No Content

---

## アーキテクチャレイヤー

### 1. Controller レイヤー
- HTTP リクエストの受け取り
- リクエスト/レスポンスの処理
- CORS 対応

### 2. Service レイヤー
- ビジネスロジックの実装
- トランザクション管理（`@Transactional`）
- エンティティとDTO間の変換

### 3. Repository レイヤー
- Spring Data JPA を使用したデータベースアクセス
- CRUD 操作の抽象化

### 4. Entity レイヤー
- JPA アノテーションを使用したデータベースマッピング
- ライフサイクルコールバック（`@PrePersist`, `@PreUpdate`）

### 5. DTO レイヤー
- リクエスト/レスポンスのデータ構造定義
- バリデーション（`@NotBlank` など）

## 設定

### application.properties
```properties
# Server
spring.application.name=todolist-api
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/todolist
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.root=INFO
logging.level.com.example.todolist=DEBUG
```

## Maven 依存関係

- `spring-boot-starter-web` - Web API 開発
- `spring-boot-starter-data-jpa` - データベースアクセス
- `spring-boot-starter-validation` - バリデーション
- `postgresql` - PostgreSQL ドライバー
- `lombok` - ボイラープレートコード削減
- `spring-boot-starter-test` - テスト

## セットアップおよび実行手順

### 1. データベース準備
```bash
# PostgreSQL に接続
psql -U postgres

# データベース作成
CREATE DATABASE todolist;

# スキーマ実行
\c todolist
\i path/to/ddl/task.sql
\i path/to/ddl/task_ex.sql
\i path/to/ddl/category.sql
```

### 2. ビルド
```bash
cd backend
mvn clean install
```

### 3. アプリケーション実行
```bash
mvn spring-boot:run
```

アプリケーションは `http://localhost:8080` で起動します。

## トランザクション管理

- `@Transactional(readOnly = true)` - 読み取り専用トランザクション
- `@Transactional` - 書き込みトランザクション
- 自動的にロールバック機能が有効

## エラーハンドリング

現在の実装では `IllegalArgumentException` をスロー。
将来は Global Exception Handler を実装予定。

## 将来の拡張機能

- [ ] Global Exception Handler の実装
- [ ] ログイン・認証機能
- [ ] ページネーション
- [ ] 高度なフィルター（複数条件検索）
- [ ] バッチ処理
- [ ] 監査ログ
- [ ] ユニットテスト・統合テスト
- [ ] API ドキュメント（Swagger/OpenAPI）
- [ ] キャッシング機構
- [ ] イベント駆動設計
