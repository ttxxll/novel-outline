# Multi-AI-Provider 设计文档

**日期**: 2026-05-01
**目标**: 将 AI 调用从硬编码 DeepSeek 抽象为可配置的多提供商架构，支持 DeepSeek（OpenAI 格式）与 MiMo（Anthropic 格式），后续可扩展其他模型。

## 架构

```
novel-analyzer/src/main/java/.../client/
├── AiClient.java              -- 统一接口
├── DeepSeekClient.java        -- OpenAI 协议实现（改造）
├── MiMoClient.java            -- Anthropic 协议实现（新增）
├── AiProperties.java          -- 多提供商配置（新增，替代 DeepSeekProperties）
└── AiClientConfig.java        -- @Configuration，按 provider 激活实现（新增）
```

调用方（ChapterAnalyzer、VolumeAggregator、OutlineBuilder）依赖 `AiClient` 接口，不再依赖具体实现。

## 接口

```java
public interface AiClient {
    String chat(String systemPrompt, String userMessage);
}
```

## YAML 配置

```yaml
ai:
  provider: deepseek  # deepseek | mimo

  deepseek:
    api-key: ${DEEPSEEK_API_KEY:}
    base-url: https://api.deepseek.com
    chat-path: /v1/chat/completions
    model: deepseek-v4-pro
    max-tokens: 8192
    temperature: 0.3

  mimo:
    api-key: ${MIMO_API_KEY:}
    base-url: https://api.xiaomimimo.com
    chat-path: /anthropic/v1/messages
    model: mimo-v2.5-pro
    max-tokens: 8192
    temperature: 0.3
```

## 实施方案

### 新建

- **`AiClient`** — 接口，`chat(String, String): String`
- **`MiMoClient`** — 实现 `AiClient`，构造 Anthropic Messages API 请求体，解析 `content[0].text`
- **`AiProperties`** — `@ConfigurationProperties(prefix = "ai")`，内嵌 `DeepSeek` 和 `MiMo` 静态内部类，额外 `provider` 字段
- **`AiClientConfig`** — `@Configuration`，两个 `@Bean` + `@ConditionalOnProperty`，按 `ai.provider` 值激活对应实现

### 修改

- **`DeepSeekClient`** — 改为 `implements AiClient`，其他不变
- **`ChapterAnalyzer`** — `DeepSeekClient` → `AiClient`
- **`VolumeAggregator`** — `DeepSeekClient` → `AiClient`
- **`OutlineBuilder`** — `DeepSeekClient` → `AiClient`
- **`DeepSeekProperties`** — 删除，由 `AiProperties` 替代
- **`application-dev.yml`** — `deepseek:` 块改为 `ai:` 块，新增 `mimo` 配置
- **`application-prod.yml`** — 同上

### 删除

- **`DeepSeekProperties`** — 不再需要独立配置类

## 调用关系

```
ChapterAnalyzer ─┐
VolumeAggregator ─┼──> AiClient ──┬── DeepSeekClient (provider=deepseek)
OutlineBuilder ──┘                 └── MiMoClient     (provider=mimo)
```

## 设计决策

- 不内置重试：重试逻辑在上层（ChapterAnalysisHandler），客户端保持单一职责
- `@ConditionalOnProperty` 激活：Spring 原生机制，启动时根据一个 key 决定，不需要 profile 切换
- `chatPath` 保留为可配：尽管目前 OpenAI 路径和 Anthropic 路径不同，保留灵活性
- `DeepSeekProperties` 直接删除，不做向后兼容：项目处于 MVP 阶段，配置项极少，直接迁移成本最低
