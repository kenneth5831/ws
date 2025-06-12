# 訂單管理系統

本專案是一個基於 Spring Boot 的 RESTful API 系統 ，
整合 MyBatis-Plus、Redis、MySQL、Swagger UI 等組件，
提供客戶、商品與訂單等資料管理能力，並支援樂觀鎖、軟刪除、自動時間戳管理等功能。

## 功能
- 客戶、產品、訂單 CRUD API
- MyBatis-Plus 樂觀鎖（@Version `MyBatisPlusConfig`）
- 軟刪除（@TableLogic）
- 自動建立/更新時間戳（@TableField(fill = ...)`MyMetaObjectHandler`）
- 全域錯誤處理（如 409：樂觀鎖衝突）
- Swagger UI API 文件（http://localhost:8080/swagger-ui.html）
- Redis 快取與分布式鎖機制（預留整合）

## 技術與框架
| 分類                           |模組|描述|
|------------------------------|-----|-----|
| **後端框架**| Spring Boot |核心框架，用於快速建立 Web API 應用程式 |
| **ORM** | MyBatis-Plus | 資料庫操作 ORM，支援分頁查詢、樂觀鎖、自動填充等功能 |
| **資料庫相關** | MySQL |MySQL 資料庫|
| **資料庫相關**| MySQL Connector|連接 MySQL 資料庫|
| **資料庫相關**| HikariCP|資料庫連線池|
| **緩存**| Redis|快取與分布式鎖應用|
| **API文檔**| Swagger UI / OpenAPI|API 文件|
| **構建工具**| Maven||

### 前置條件
- JDK 17
- Maven 3.9.10
- Docker
- MySQL

### 運行應用
- API接口: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- 資料庫:
    - schema: src/main/resources/SQL/schema/db_init.sql
    - 用戶名: root
    - 密碼: qwer1234
    - mockData: src/main/resources/SQL/mockData/mockData.sql
- 步驟:
1. 確保本機已安裝 JDK 17+、Maven、MySQL、Redis
2. 建立資料庫與資料表
3. 建立redis 
   - 安裝docker desktop
   - `bash` 
   `docker run -d -p 8182:6379 --name redis redis`
4. 修改 `application.properties` 中的資料庫設定
5. 執行啟動指令：
`bash`
`mvn spring-boot:run`

## 開發指南

### Thread Safe
目前框架實際應用包含拓展共三種 
`MyBatis-Plus 樂觀鎖` 
`資料庫悲觀鎖`
`Redis 分布式鎖` 
可區分情境使用

| 情境         | Redis 分布式鎖 | MyBatis-Plus 樂觀鎖 | DB 悲觀鎖         | 例子       |
|------------|------------|------------------|----------------|----------|
| **頻繁變動**   | V          | V                | 實作時應該考慮死鎖及阻塞問題 | product  |
| **不常改動**   | X          | V (必要時)          | X              | customer |
| **新增多，更新少** | X          | V (更新時)          | X              | order    |


1. Redis SETNX 加鎖（product:{id}）
2. 查庫存（SELECT）
3. 判斷庫存是否足夠
4. 減庫存並 updateById（MyBatis-Plus 樂觀鎖）
5. Redis DEL 解鎖

### 樂觀鎖與軟刪除實作細節
- 樂觀鎖：所有 Entity 繼承 BaseEntity，並使用 @Version 欄位控制
- 軟刪除：啟用 @TableLogic 並於資料表中新增 deleted 欄位（預設 0）


### 錯誤處理機制
已實作的處理方式：

|錯誤類型|HTTP 狀態碼|描述與處理說明|
|---|---|---|
|樂觀鎖衝突（版本號不一致）|409 Conflict|當資料已被其他使用者更新，將回傳提示需重新讀取資料。處理類型包括 OptimisticLockingFailureException 及 MybatisPlusException|
|未預期的系統錯誤|500 Internal Server Error|捕捉所有其他非預期錯誤，避免堆疊資訊外洩，提供簡要訊息給使用者|