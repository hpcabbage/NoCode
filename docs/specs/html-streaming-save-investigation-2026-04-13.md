# HTML 流式生成不落盘问题排查说明（2026-04-13）

## 1. 背景

项目：壹句建站（NoCode）

相关目录：
- 后端：`/home/cabbage/.openclaw/workspace/NoCode`
- 前端：`/home/cabbage/.openclaw/workspace/NocodeFront/yu-ai-code-mother-frontend`
- Java：`/home/cabbage/.local/java`
- Maven：`/home/cabbage/.local/maven`

当前主问题：
- 前端在应用对话页发起 HTML 模式代码生成时，能够收到流式文本
- 但生成结果没有成功保存到 `tmp/code_output/html_{appId}`
- 进一步导致：
  - 预览 404
  - 下载代码失败
  - 部署提示“应用代码不存在，请先生成代码”

---

## 2. 已确认可用的链路

以下链路已经实际验证通过：

### 2.1 运行环境
- Java 可用
- Maven / `mvnw` 可用
- Spring Boot 能成功启动
- MySQL 连通
- Redis 连通

### 2.2 用户链路
- 注册：通过
- 登录：通过
- 登录态查询：通过

### 2.3 应用基础链路
- 创建应用：通过
- 获取应用详情：通过
- 我的应用列表：通过
- 精选应用列表：通过
- 初始聊天历史查询：通过

### 2.4 AI 生成入口链路
- `/app/chat/gen/code` 可调用
- 后端会向 DeepSeek 发起请求
- 返回 `text/event-stream`
- 前端可接收到流式 chunk

结论：
**问题不在“请求能不能发起”，而在“流式生成后的收尾/保存阶段”。**

---

## 3. 当前故障表现

### 3.1 现象
对于 HTML 模式：
- SSE 有内容
- 聊天历史里有用户消息
- 但代码目录未落地

例如：
- `tmp/code_output/html_2` 不存在
- `tmp/code_output/html_3` 不存在
- `tmp/code_output/html_4` 不存在

### 3.2 连锁结果
因此后续功能全部被阻塞：
- `/api/static/html_{appId}/` → 404
- `/app/download/{appId}` → 返回 JSON，不是 zip
- `/app/deploy` → `应用代码不存在，请先生成代码`

---

## 4. 代码层定位结论

### 4.1 当前保存逻辑依赖流结束
`AiCodeGeneratorFacade.processCodeStream(...)` 当前的核心逻辑是：

```java
return codeStream
    .doOnNext(chunk -> codeBuilder.append(chunk))
    .doOnComplete(() -> {
        Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
        File savedDir = CodeFileSaveExecutor.executeSave(parsedResult, codeGenType, appId);
    });
```

也就是说：

**只有流 complete 后，才会 parse + save。**

### 4.2 直接推论
只要 streaming 没有正常 complete：
- 不会解析代码
- 不会保存代码
- 不会生成 `index.html`
- 预览 / 下载 / 部署全部失效

---

## 5. 已做过的技术探索

## 5.1 探索 A：怀疑是测试时过早断开 SSE

最初怀疑：
- 是不是因为测试脚本没有完整消费 SSE
- 导致 `doOnComplete()` 没有执行

后续做了完整收口测试后，问题依旧存在，说明：

**并不是简单的“测试中途掐断连接”导致。**

---

## 5.2 探索 B：怀疑是 AI Services 的 `Flux<String>` 包装有问题

原实现：
- `AiCodeGeneratorService.generateHtmlCodeStream(String)`
- 由 LangChain4j AI Services 直接返回 `Flux<String>`

为验证这一层，我做过一轮最小改造：
- 不再依赖 AI Services 自动返回 `Flux<String>`
- 对 HTML / MULTI_FILE 改成直接使用底层 `StreamingChatModel`
- 手动实现 `StreamingChatResponseHandler`
  - `onPartialResponse()`：实时向前端推 chunk
  - `onCompleteResponse()`：执行 parse + save

该改造已经完成过一次，并且：
- Java 编译通过
- 后端可正常启动

但运行后症状仍然表现为：
- 有流式内容
- 但未稳定进入 complete 收尾

这说明：

**问题不只在 AI Services 的高层 `Flux<String>` 包装层，可能更深。**

---

## 5.3 探索 C：怀疑是 LangChain4j 版本混搭

当前 `pom.xml` 中 LangChain4j 相关版本为：
- `langchain4j` → `1.1.0`
- `langchain4j-open-ai-spring-boot-starter` → `1.1.0-beta7`
- `langchain4j-reactor` → `1.1.0-beta7`
- `langchain4j-community-redis-spring-boot-starter` → `1.1.0-beta7`

### 尝试过的动作
#### 尝试 1：全部升到 `1.1.0`
结果：失败
- Maven Central 中不存在这些 starter/reactor/community 模块的 `1.1.0`

#### 尝试 2：把核心 `langchain4j` 降到 `1.1.0-beta7`
结果：失败
- Maven Central 中不存在 `dev.langchain4j:langchain4j:1.1.0-beta7`

### 结论
不能简单通过“把所有版本改成同一数字”解决。
但该现象说明：

**当前 LangChain4j 的发布本身就存在正式版/测试版模块交错，版本组合的兼容风险是真实存在的。**

---

## 6. 外部资料调查结论

对 LangChain4j 官方文档、GitHub issue、DeepSeek 官方文档做了检索，得到的方向性结论如下：

### 6.1 LangChain4j 官方语义
官方文档明确说明：
- 当模型完成流式响应时，应调用 `onCompleteResponse(ChatResponse)`

这意味着当前故障不是业务逻辑理解错误，而是：

**实际运行时没有稳定进入这个框架预期的完成态。**

### 6.2 GitHub issue 方向
查到的公开问题方向包括：
- DeepSeek + LangChain4j 下 final response / final text 异常
- OpenAI-compatible provider 在 streaming 模式下存在兼容性问题
- AI Services + streaming 在某些 OpenAI-compatible provider 下空响应 / 行为异常
- Streaming 模式下 tool / final response / complete 行为异常

这些 issue 不一定与当前问题一模一样，但共同说明：

**DeepSeek 与 LangChain4j 的 OpenAI-compatible streaming 组合，存在真实兼容风险。**

### 6.3 DeepSeek 官方文档
DeepSeek 仍然宣称兼容 OpenAI API，并支持 streaming。
因此问题更像是：
- 协议“基本兼容”
- 但在 usage chunk / 最后一个 delta / 结束事件等细节上与官方 OpenAI 并不完全一致

---

## 7. 当前最可信的根因判断

基于代码、运行日志和外部资料，当前最可信的判断是：

### 主判断
**DeepSeek + LangChain4j OpenAI-compatible streaming 在 HTML 这条链路下没有稳定触发 complete，导致保存逻辑永远无法执行。**

### 更细一点的可能点
1. 底层 streaming 连接没有正常触发完成事件
2. DeepSeek 的最后 usage / finish / done 包与 LangChain4j 当前实现的完成判定不完全兼容
3. LangChain4j 当前版本组合对该 provider 的 streaming 收尾支持不够稳

---

## 8. 当前最值得继续验证的“单点”

下一步最值得优先验证的点：

### 去掉 `stream_options.includeUsage(true)`
原因：
- `OpenAiStreamingChatModel` 当前默认会发送：

```java
.stream(true)
.streamOptions(StreamOptions.builder()
    .includeUsage(true)
    .build())
```

- DeepSeek 这类 OpenAI-compatible provider 的最后 usage chunk、空 delta、done 包有可能和 LangChain4j 预期不完全一致
- 如果问题卡在最后 usage 包，去掉 `includeUsage(true)` 有可能直接恢复 complete

这是一条非常适合做“单点验证”的路径：
- 改动小
- 影响范围集中
- 验证成本低

---

## 9. 当前不建议的处理方式

### 不建议 1：直接改成非流式/伪流式
原因：
- 会破坏产品体验
- 前端会等很久后一次性吐大量内容
- 不符合当前产品对“实时生成反馈”的要求

### 不建议 2：直接断言是保存模板问题
原因：
- 现有证据更支持“complete 没发生”
- 保存链路未获得执行机会，不能先怪保存模板

### 不建议 3：简单粗暴统一 LangChain4j 版本号
原因：
- 已验证过，这条路在 Maven Central 上不成立

---

## 10. 后续建议动作

建议按下面顺序继续：

### 步骤 1（优先）
验证：**禁用 `includeUsage(true)` 后，HTML streaming 是否恢复完整收尾**

目标：
- 继续保持真流式
- 观察是否开始正常 complete
- 验证是否能触发保存、预览、下载、部署恢复

### 步骤 2
如果步骤 1 无效，则继续往更底层挖：
- `OpenAiClient.chatCompletion(...).onComplete(...)`
- DeepSeek streaming 的最后一个 SSE 包到底长什么样

### 步骤 3
如果最终确认是 LangChain4j 对 DeepSeek 的兼容问题，则考虑：
- 为 HTML / MULTI_FILE 场景自己实现更稳的 streaming client 收尾逻辑
- 而不是继续完全依赖 LangChain4j 当前默认完成判定

---

## 11. 附：今天已做过的关键事实记录

- 后端已可以成功启动并对外提供服务
- HTML 模式创建应用成功
- SSE 流式文本可见
- 代码未落盘
- 手工改造为底层 `StreamingChatModel + StreamingChatResponseHandler` 后，问题依旧未完全消失
- 版本统一方案已探索但未直接成立
- 当前最合理的单点探索目标为：`includeUsage(true)`

---

## 12. 文档用途

本文件用于：
- 记录今天针对 HTML 流式生成不落盘问题的排查过程
- 给后续迭代提供上下文
- 避免重复走过已证伪的路线
- 为后续修复提供单点验证顺序
