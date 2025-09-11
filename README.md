# 壹句建站 开源项目
壹句建站 **地址**：http://cabbagehp.online
<img width="1890" height="880" alt="image" src="https://github.com/user-attachments/assets/0966cdf8-69b6-476c-8390-a5bce867dd98" />
<img width="1895" height="875" alt="image" src="https://github.com/user-attachments/assets/e6511f46-c373-4080-8be5-6ecb3616416e" />


## 项目简介

`壹句建站` 是一个基于 Java 的开源项目，旨在通过 AI 技术实现代码的自动化生成。该项目结合了 AI 模型与代码解析、保存、构建等模块，能够根据用户输入的自然语言描述生成 HTML、CSS、JavaScript 等多种类型的代码文件，并支持构建 Vue 项目。

项目主要面向开发者和 AI 工程师，提供了一套完整的代码生成、保存、部署和管理功能，适用于快速原型开发、低代码平台构建等场景。

---
## 主要功能

- **AI 代码生成**：基于 AI 模型，根据用户输入的自然语言生成 HTML、CSS、JS 或 Vue 项目代码。
- **代码解析与保存**：支持将生成的代码解析为结构化对象，并保存为文件。
- **Vue 项目构建**：提供异步构建 Vue 项目的功能。
- **流式处理与响应**：支持流式响应，实时返回生成的代码片段。
- **工具集成**：集成文件读写、删除、修改等工具，支持与 AI 生成过程的交互。
- **用户与权限管理**：支持用户注册、登录、权限控制等功能。
- **聊天历史记录**：记录用户与 AI 的交互历史。
- **限流机制**：防止请求过载，支持基于 Redis 的限流策略。
- **安全prompt**:Prompt 安全审查,有效拒绝敏感词和防范注入攻击。
---

## 技术栈

- **后端**：Java + Spring Boot
- **AI 模型**：LangChain4j + OpenAI Streaming Chat Model
- **数据库**：MySQL（通过 MyBatis flex 操作）
- **缓存**：Redis（用于聊天记忆和限流）
- **文件存储**：MinIO（用于截图和文件存储）
- **前端**：Vue.js（可选，用于生成前端项目）
- **构建工具**：Maven

---

## 项目结构概览

- **AI 生成模块**：负责与 AI 模型交互，生成代码。
- **代码解析模块**：解析 AI 生成的代码，提取 HTML、CSS、JS 等内容。
- **代码保存模块**：将解析后的代码保存为文件。
- **Vue 构建模块**：支持构建 Vue 项目。
- **工具模块**：提供文件读写、删除、修改等工具。
- **用户系统**：支持用户注册、登录、权限控制。
- **限流模块**：防止请求过载。
- **聊天历史模块**：记录用户与 AI 的交互。
- **部署模块**：支持项目部署与静态资源访问。

---

## 快速开始

### 环境要求

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- MinIO（可选）
- Node.js（用于构建 Vue 项目）

### 安装步骤

1. **克隆项目**

   ```bash
   git clone https://gitee.com/cabbage12/codeless.git
   cd codeless
   ```

2. **配置数据库**

   修改 `application.yml` 中的数据库连接信息：

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/yuaicodemother?useSSL=false&serverTimezone=UTC
       username: root
       password: your_password
   ```

3. **初始化数据库**

   执行 SQL 脚本创建表结构（脚本位于 `src/main/resources/sql`）。

4. **配置 AI 模型**

   在 `application.yml` 中配置 OpenAI 的 API Key 和模型参数：

   ```yaml
   langchain4j:
     open-ai:
       streaming-chat-model:
         api-key: your_openai_api_key
         model-name: gpt-3.5-turbo
   ```

5. **启动项目**

   ```bash
   mvn spring-boot:run
   ```

6. **访问接口**

   启动后，访问 `http://localhost:8080/swagger-ui.html` 查看 API 文档。

---

## 使用示例

### 生成 HTML 代码

```bash
POST /app/chat/gen/code
Content-Type: application/json

{
  "appId": 1,
  "message": "生成一个简单的计算器界面"
}
```

### 部署项目

```bash
POST /app/deploy
Content-Type: application/json

{
  "appId": 1
}
```

### 获取生成的代码

```bash
GET /static/{deployKey}/**
```

---

## 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 项目
2. 创建新分支 (`git checkout -b feature/your-feature`)
3. 提交更改 (`git commit -am 'Add some feature'`)
4. 推送分支 (`git push origin feature/your-feature`)
5. 创建 Pull Request

---

## 许可证

本项目采用 [MIT License](https://opensource.org/licenses/MIT) 开源协议。

---

## 联系方式

- 作者：Cabbage12
- 邮箱：cabbage12@example.com
- Gitee：[https://gitee.com/cabbage12/codeless](https://gitee.com/cabbage12/codeless)

---

## 致谢

感谢以下开源项目的支持：

- [LangChain4j](https://github.com/langchain4j/langchain4j)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis Plus](https://mp.baomidou.com/)
- [Redisson](https://github.com/redisson/redisson)
- [MinIO Java SDK](https://github.com/minio/minio-java)


---

