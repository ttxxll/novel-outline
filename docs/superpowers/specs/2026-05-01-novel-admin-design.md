# Novel Admin 后台管理页面设计

## 概述

为 novel-outline 项目搭建独立的 Vue 2.7 管理后台，提供小说列表、上传、分析、详情查看功能。后端 API 不做改动。

## 技术选型

| 项 | 选择 | 理由 |
|---|------|------|
| 框架 | Vue 2.7.16 | 最后一个 Vue 2 稳定版，选项式 API |
| UI 组件库 | Element UI 2.15 | Vue 2 最成熟组件库，表格/表单/上传开箱即用 |
| HTTP 客户端 | Axios | 行业标准 |
| 路由 | Vue Router 3 | Vue 2 配套版本 |
| 构建工具 | Vue CLI 5 | Vue 2 官方工具链 |
| 项目位置 | `D:\Project\Front\novel-admin` | 独立于后端项目 |

## 路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | 小说列表 | 表格展示所有小说，上传入口，分析/删除按钮 |
| `/novels/:id` | 小说详情 | 卷列表 + 章节列表，展示 AI 总结和分析结果 |

## 组件树

```
App
├── NovelList                    — 小说列表页
│   ├── UploadDialog             — 上传弹窗（文件选择 + 解析规则下拉）
│   └── AnalyzeButton            — 分析按钮（带 loading + 轮询进度）
│
└── NovelDetail                  — 小说详情页
    ├── NovelHeader              — 标题、状态标签、进度条
    ├── VolumeCard               — 卷卡片（可折叠），显示 AI 摘要
    │   └── ChapterItem          — 章节行（可展开），显示 AI 分析结果
    └── OutlineSection           — 纲要展示（JSON 格式化）
```

## 数据流

### NovelList
- **加载列表**：`GET /api/novels` → 表格 rows
- **上传**：`POST /api/novels` (multipart: file + parseRuleId) → 刷新列表
- **开始分析**：`POST /api/novels/:id/analyze` → 轮询 `GET /api/novels/:id/progress` 更新行状态
- **删除**：`DELETE /api/novels/:id` → ElMessageBox 确认 → 刷新列表

### NovelDetail
- **小说信息**：`GET /api/novels/:id` → 标题、状态、原始文件名
- **卷列表**：`GET /api/volumes/by-novel/:id` → 卷列表（含 summary 字段）
- **章节列表**：`GET /api/chapters/by-novel/:id` → 章节列表（含 analysisResult 字段）
- **分析进度**：`GET /api/novels/:id/progress` → 进度条（totalChapters / chaptersDone）
- **纲要**：`GET /api/novels/:id/outline` → JSON 展示

### 上传流程
1. 点击上传按钮 → UploadDialog 弹出
2. `GET /api/parse-rules` 获取解析规则下拉选项
3. 用户选择 .txt 文件 + 解析规则
4. `POST /api/novels` (FormData: file + parseRuleId)
5. 成功后关闭弹窗，刷新列表

### 分析流程
1. 点击分析按钮 → 调用 `POST /api/novels/:id/analyze`
2. 按钮进入 loading 状态
3. 前端每 3 秒轮询 `GET /api/novels/:id/progress`
4. 直到 status 变为 `COMPLETED` 或 `FAILED`，停止轮询
5. 更新界面状态

## API 封装

```
src/api/
├── index.js          — axios 实例，baseURL "/api"，dev proxy → localhost:8088
├── novel.js          — list, detail, upload, delete, analyze, progress, outline
└── parseRule.js      — list
```

## 状态管理

不引入 Vuex，组件内 `data` + `props` + `$emit` 传递。页面少数据简单。

## 要素 UI 展示规则

- **NovelStatus**：NOT_STARTED → 灰色 default 标签，ANALYZING → 蓝色 loading，COMPLETED → 绿色 success，FAILED → 红色 danger
- **ChapterStatus**：PENDING → 灰色，COMPLETED → 绿色，FAILED → 红色
- **纲要 JSON**：用 `<pre>` + `<code>` 展示格式化的 JSON（`JSON.stringify(data, null, 2)`）
- **AI 分析/摘要**：支持长文本，用 Element Collapse 折叠，默认展开第一条，其余折叠

## 错误处理

- Axios 响应拦截器统一处理 HTTP 错误，`ElMessage.error()` 提示
- 上传失败：提示错误信息
- 分析失败：状态标签变红，tooltip 显示失败
- 网络异常：`ElMessage.error('网络错误')`

## 开发配置

- `vue.config.js` 配置 devServer proxy：`/api` → `http://localhost:8088`
- 生产部署：`npm run build` → `dist/` 可直接用 nginx 或 copy 到 Spring Boot static 目录
