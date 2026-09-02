# TodoList REST API

Spring Boot を使用した TodoList 用の REST API バックエンド

## 環境要件

- Java 17+
- Maven 3.8+
- PostgreSQL 12+

## セットアップ

### 1. データベース準備

PostgreSQL に `todolist` データベースを作成：

```sql
CREATE DATABASE todolist;
```

### 2. スキーマ作成

`ddl/` ディレクトリ内のSQLファイルを実行：

```bash
psql -U postgres -d todolist -f ../ddl/task.sql
psql -U postgres -d todolist -f ../ddl/task_ex.sql
psql -U postgres -d todolist -f ../ddl/category.sql
```

### 3. アプリケーション起動

```bash
# Maven でビルド
mvn clean install

# アプリケーション実行
mvn spring-boot:run
```

アプリケーションは `http://localhost:8080` で起動します。

## API エンドポイント

### タスク一覧取得
```
GET /api/v1/tasks
```

### 特定のタスク取得
```
GET /api/v1/tasks/{id}
```

### 完了状態でフィルター
```
GET /api/v1/tasks/filter/completed?completed=true|false
```

### タスク作成
```
POST /api/v1/tasks
Content-Type: application/json

{
  "title": "タスク名",
  "description": "説明（省略可）",
  "completed": false,
  "priority": 0,
  "dueDate": "2024-12-31T23:59:59",
  "category": "カテゴリ名"
}
```

### タスク更新
```
PUT /api/v1/tasks/{id}
Content-Type: application/json

{
  "title": "新しいタスク名",
  "description": "新しい説明",
  "completed": true
}
```

### タスク削除
```
DELETE /api/v1/tasks/{id}
```

## プロジェクト構成

```
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/example/todolist/
│   │   │   ├── TodolistApplication.java
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java
│   │   │   ├── service/
│   │   │   │   └── TaskService.java
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── Task.java
│   │   │   │   └── TaskEx.java
│   │   │   └── dto/
│   │   │       ├── TaskRequest.java
│   │   │       └── TaskResponse.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── README.md
```

## 技術スタック

- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Maven**

## 開発メモ

- エンティティは `@Entity` アノテーションで定義
- `TaskEx` は `Task` の 1:1 関連付けで拡張属性を保持
- トランザクション管理は `@Transactional` で実装
- DTOで API リクエスト/レスポンスを管理

## ライセンス

MIT
