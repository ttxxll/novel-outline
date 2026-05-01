# Novel Admin 后台管理页面实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建 Vue 2.7 + Element UI 独立前端项目 `novel-admin`，实现小说列表、上传、分析、详情查看功能。

**Architecture:** 独立 Vue 2.7 项目（`D:\Project\Front\novel-admin`），通过 dev proxy 连接后端 `localhost:8088`。2 个路由页面：小说列表 + 小说详情。不引入 Vuex，组件间用 props/events 通信。

**Tech Stack:** Vue 2.7.16, Vue Router 3.6.5, Element UI 2.15.14, Axios 1.7.x, Vue CLI 5.0.8

---

## 文件结构

```
novel-admin/
├── public/
│   └── index.html
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── router/
│   │   └── index.js
│   ├── api/
│   │   ├── index.js
│   │   ├── novel.js
│   │   └── parseRule.js
│   ├── views/
│   │   ├── NovelList.vue
│   │   └── NovelDetail.vue
│   └── components/
│       ├── UploadDialog.vue
│       ├── AnalyzeButton.vue
│       ├── NovelHeader.vue
│       ├── VolumeCard.vue
│       ├── ChapterItem.vue
│       └── OutlineSection.vue
├── vue.config.js
├── babel.config.js
└── package.json
```

---

### Task 1: 项目脚手架

**Files:**
- Create: `D:\Project\Front\novel-admin\package.json`
- Create: `D:\Project\Front\novel-admin\vue.config.js`
- Create: `D:\Project\Front\novel-admin\babel.config.js`
- Create: `D:\Project\Front\novel-admin\public\index.html`
- Create: `D:\Project\Front\novel-admin\src\main.js`
- Create: `D:\Project\Front\novel-admin\src\App.vue`
- Create: `D:\Project\Front\novel-admin\src\router\index.js`

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "novel-admin",
  "version": "0.1.0",
  "private": true,
  "scripts": {
    "serve": "vue-cli-service serve",
    "build": "vue-cli-service build"
  },
  "dependencies": {
    "axios": "^1.7.9",
    "element-ui": "^2.15.14",
    "vue": "^2.7.16",
    "vue-router": "^3.6.5"
  },
  "devDependencies": {
    "@vue/cli-plugin-babel": "^5.0.8",
    "@vue/cli-service": "^5.0.8",
    "vue-template-compiler": "^2.7.16"
  }
}
```

- [ ] **Step 2: 创建 vue.config.js**

```javascript
const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 babel.config.js**

```javascript
module.exports = {
  presets: [
    '@vue/cli-plugin-babel/preset'
  ]
}
```

- [ ] **Step 4: 创建 public/index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width,initial-scale=1.0">
  <title>小说后台管理</title>
</head>
<body>
  <div id="app"></div>
</body>
</html>
```

- [ ] **Step 5: 创建 src/main.js**

```javascript
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)
Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
```

- [ ] **Step 6: 创建 src/router/index.js**

```javascript
import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'NovelList',
    component: () => import('@/views/NovelList.vue')
  },
  {
    path: '/novels/:id',
    name: 'NovelDetail',
    component: () => import('@/views/NovelDetail.vue')
  }
]

export default new VueRouter({
  mode: 'hash',
  routes
})
```

- [ ] **Step 7: 创建 src/App.vue**

```vue
<template>
  <el-container id="app-container">
    <el-header>
      <h1>小说后台管理</h1>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
body { margin: 0; background: #f5f7fa; }
#app-container { min-height: 100vh; }
.el-header {
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
}
.el-header h1 { font-size: 20px; margin: 0; }
</style>
```

- [ ] **Step 8: 安装依赖并验证启动**

```bash
cd D:\Project\Front\novel-admin && npm install
```

- [ ] **Step 9: 启动开发服务器验证**

```bash
npm run serve
```
预期：`App running at: Local: http://localhost:3000/`，页面显示空白（路由组件尚未创建）

- [ ] **Step 10: 提交**

```bash
cd D:\Project\Front\novel-admin && git init && git add -A && git commit -m "chore: init novel-admin project scaffold"
```

---

### Task 2: API 封装

**Files:**
- Create: `D:\Project\Front\novel-admin\src\api\index.js`
- Create: `D:\Project\Front\novel-admin\src\api\novel.js`
- Create: `D:\Project\Front\novel-admin\src\api\parseRule.js`

- [ ] **Step 1: 创建 axios 实例 src/api/index.js**

```javascript
import axios from 'axios'
import { Message } from 'element-ui'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000
})

api.interceptors.response.use(
  response => response,
  error => {
    const msg = error.response?.data?.message || error.message || '网络错误'
    Message.error(msg)
    return Promise.reject(error)
  }
)

export default api
```

- [ ] **Step 2: 创建 src/api/novel.js**

```javascript
import api from './index'

export function listNovels() {
  return api.get('/novels')
}

export function getNovel(id) {
  return api.get(`/novels/${id}`)
}

export function uploadNovel(formData) {
  return api.post('/novels', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deleteNovel(id) {
  return api.delete(`/novels/${id}`)
}

export function startAnalysis(id) {
  return api.post(`/novels/${id}/analyze`)
}

export function getProgress(id) {
  return api.get(`/novels/${id}/progress`)
}

export function getOutline(id) {
  return api.get(`/novels/${id}/outline`)
}

export function getVolumesByNovel(novelId) {
  return api.get(`/volumes/by-novel/${novelId}`)
}

export function getChaptersByNovel(novelId) {
  return api.get(`/chapters/by-novel/${novelId}`)
}
```

- [ ] **Step 3: 创建 src/api/parseRule.js**

```javascript
import api from './index'

export function listParseRules() {
  return api.get('/parse-rules')
}
```

- [ ] **Step 4: 提交**

```bash
cd D:\Project\Front\novel-admin && git add -A && git commit -m "feat: add API layer (axios + novel/parseRule modules)"
```

---

### Task 3: 小说列表页

**Files:**
- Create: `D:\Project\Front\novel-admin\src\views\NovelList.vue`
- Create: `D:\Project\Front\novel-admin\src\components\UploadDialog.vue`
- Create: `D:\Project\Front\novel-admin\src\components\AnalyzeButton.vue`

- [ ] **Step 1: 创建 UploadDialog.vue**

```vue
<template>
  <el-dialog title="上传小说" :visible.sync="dialogVisible" width="500px" @close="resetForm">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="小说文件" prop="file">
        <el-upload
          ref="upload"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="fileList"
          accept=".txt"
          action=""
        >
          <el-button slot="trigger" size="small">选择文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="解析规则" prop="parseRuleId">
        <el-select v-model="form.parseRuleId" placeholder="请选择解析规则">
          <el-option
            v-for="rule in rulesList"
            :key="rule.id"
            :label="rule.name"
            :value="rule.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="uploading" @click="submit">上传</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { listParseRules } from '@/api/parseRule'
import { uploadNovel } from '@/api/novel'

export default {
  name: 'UploadDialog',
  props: {
    visible: Boolean
  },
  data() {
    return {
      rulesList: [],
      fileList: [],
      selectedFile: null,
      form: { parseRuleId: null },
      rules: {
        parseRuleId: [{ required: true, message: '请选择解析规则', trigger: 'change' }]
      },
      uploading: false
    }
  },
  computed: {
    dialogVisible: {
      get() { return this.visible },
      set(v) { this.$emit('update:visible', v) }
    }
  },
  watch: {
    visible(val) {
      if (val) {
        listParseRules().then(res => { this.rulesList = res.data })
      }
    }
  },
  methods: {
    handleFileChange(file) {
      this.selectedFile = file.raw
    },
    handleFileRemove() {
      this.selectedFile = null
    },
    resetForm() {
      this.fileList = []
      this.selectedFile = null
      this.form.parseRuleId = null
      this.$refs.form && this.$refs.form.resetFields()
    },
    async submit() {
      if (!this.selectedFile) {
        this.$message.warning('请选择文件')
        return
      }
      const valid = await this.$refs.form.validate().catch(() => false)
      if (!valid) return
      this.uploading = true
      try {
        const fd = new FormData()
        fd.append('file', this.selectedFile)
        fd.append('parseRuleId', this.form.parseRuleId)
        await uploadNovel(fd)
        this.$message.success('上传成功')
        this.dialogVisible = false
        this.$emit('success')
      } finally {
        this.uploading = false
      }
    }
  }
}
</script>
```

- [ ] **Step 2: 创建 AnalyzeButton.vue**

```vue
<template>
  <el-button
    type="primary"
    size="small"
    :loading="analyzing"
    :disabled="analyzing"
    @click.stop="start"
  >
    {{ analyzing ? '分析中...' : '分析' }}
  </el-button>
</template>

<script>
import { startAnalysis, getProgress } from '@/api/novel'

export default {
  name: 'AnalyzeButton',
  props: {
    novel: Object
  },
  data() {
    return {
      analyzing: false,
      timer: null
    }
  },
  methods: {
    async start() {
      this.analyzing = true
      try {
        await startAnalysis(this.novel.id)
        this.poll()
      } catch {
        this.analyzing = false
      }
    },
    async poll() {
      try {
        const res = await getProgress(this.novel.id)
        const p = res.data
        if (p.status === 'COMPLETED') {
          this.$message.success('分析完成')
          this.analyzing = false
          this.$emit('updated')
          return
        }
        if (p.status === 'FAILED') {
          this.$message.error('分析失败')
          this.analyzing = false
          this.$emit('updated')
          return
        }
        this.timer = setTimeout(() => this.poll(), 3000)
      } catch {
        this.analyzing = false
      }
    }
  },
  beforeDestroy() {
    clearTimeout(this.timer)
  }
}
</script>
```

- [ ] **Step 3: 创建 NovelList.vue**

```vue
<template>
  <div>
    <el-row class="toolbar">
      <el-button type="primary" icon="el-icon-upload" @click="uploadVisible = true">上传小说</el-button>
    </el-row>

    <el-table :data="novels" stripe @row-click="goDetail" v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="{row}">
          <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalChapters" label="总章节" width="80" align="center" />
      <el-table-column prop="analyzedChapters" label="已分析" width="80" align="center" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="160">
        <template slot-scope="{row}">
          <AnalyzeButton :novel="row" @updated="fetchList" />
          <el-button type="danger" size="small" @click.stop="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <UploadDialog :visible.sync="uploadVisible" @success="fetchList" />
  </div>
</template>

<script>
import { listNovels, deleteNovel } from '@/api/novel'
import UploadDialog from '@/components/UploadDialog'
import AnalyzeButton from '@/components/AnalyzeButton'

const STATUS_MAP = {
  'NOT_STARTED': '未开始',
  'ANALYZING': '分析中',
  'COMPLETED': '已完成',
  'FAILED': '失败'
}

const STATUS_TAG_MAP = {
  'NOT_STARTED': 'info',
  'ANALYZING': '',
  'COMPLETED': 'success',
  'FAILED': 'danger'
}

export default {
  name: 'NovelList',
  components: { UploadDialog, AnalyzeButton },
  data() {
    return {
      novels: [],
      loading: false,
      uploadVisible: false
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listNovels()
        this.novels = res.data
      } finally {
        this.loading = false
      }
    },
    statusText(s) { return STATUS_MAP[s] || s },
    statusTag(s) { return STATUS_TAG_MAP[s] || 'info' },
    goDetail(row) {
      this.$router.push(`/novels/${row.id}`)
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该小说？删除后数据不可恢复。', '确认删除', {
          type: 'warning'
        })
        await deleteNovel(row.id)
        this.$message.success('已删除')
        this.fetchList()
      } catch {}
    }
  }
}
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
```

- [ ] **Step 4: 提交**

```bash
cd D:\Project\Front\novel-admin && git add -A && git commit -m "feat: add NovelList page with upload and analyze"
```

---

### Task 4: 小说详情页

**Files:**
- Create: `D:\Project\Front\novel-admin\src\views\NovelDetail.vue`
- Create: `D:\Project\Front\novel-admin\src\components\NovelHeader.vue`
- Create: `D:\Project\Front\novel-admin\src\components\VolumeCard.vue`
- Create: `D:\Project\Front\novel-admin\src\components\ChapterItem.vue`
- Create: `D:\Project\Front\novel-admin\src\components\OutlineSection.vue`

- [ ] **Step 1: 创建 NovelHeader.vue**

```vue
<template>
  <div class="novel-header">
    <el-row>
      <el-col :span="18">
        <h2>{{ novel.title }}</h2>
        <el-tag :type="statusTag(novel.status)" style="margin-left: 12px">{{ statusText(novel.status) }}</el-tag>
      </el-col>
      <el-col :span="6">
        <el-button size="small" icon="el-icon-back" @click="$router.push('/')">返回列表</el-button>
      </el-col>
    </el-row>
    <el-row class="progress-row">
      <span class="progress-label">分析进度：</span>
      <el-progress
        :percentage="percent"
        :status="novel.status === 'COMPLETED' ? 'success' : null"
        :text-inside="true"
        :stroke-width="20"
        style="width: 300px"
      />
      <span class="progress-text">{{ progress.chaptersDone || 0 }} / {{ progress.totalChapters || 0 }}</span>
    </el-row>
  </div>
</template>

<script>
const STATUS_MAP = {
  'NOT_STARTED': '未开始',
  'ANALYZING': '分析中',
  'COMPLETED': '已完成',
  'FAILED': '失败'
}

export default {
  name: 'NovelHeader',
  props: {
    novel: Object,
    progress: Object
  },
  computed: {
    percent() {
      if (!this.progress.totalChapters) return 0
      return Math.round((this.progress.chaptersDone || 0) / this.progress.totalChapters * 100)
    }
  },
  methods: {
    statusText(s) { return STATUS_MAP[s] || s },
    statusTag(s) {
      const m = { NOT_STARTED: 'info', ANALYZING: '', COMPLETED: 'success', FAILED: 'danger' }
      return m[s] || 'info'
    }
  }
}
</script>

<style scoped>
.novel-header { padding: 16px; background: #fff; border-radius: 4px; margin-bottom: 16px; }
.novel-header h2 { display: inline; margin: 0; }
.progress-row { margin-top: 12px; display: flex; align-items: center; }
.progress-label { margin-right: 8px; color: #606266; }
.progress-text { margin-left: 8px; color: #909399; font-size: 13px; }
</style>
```

- [ ] **Step 2: 创建 ChapterItem.vue**

```vue
<template>
  <div class="chapter-item">
    <div class="chapter-title" @click="expanded = !expanded">
      <i :class="expanded ? 'el-icon-caret-bottom' : 'el-icon-caret-right'" />
      <span>{{ chapter.idx != null ? '第' + (chapter.idx + 1) + '章 ' : '' }}{{ chapter.title }}</span>
      <el-tag v-if="chapter.status" :type="chapter.status === 'COMPLETED' ? 'success' : chapter.status === 'FAILED' ? 'danger' : 'info'" size="mini" style="margin-left: 8px">
        {{ chapter.status }}
      </el-tag>
    </div>
    <div v-show="expanded" class="chapter-content">
      <div v-if="chapter.analysisResult" class="analysis-result">
        <pre>{{ chapter.analysisResult }}</pre>
      </div>
      <div v-else class="no-analysis">暂未分析</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChapterItem',
  props: {
    chapter: Object
  },
  data() {
    return {
      expanded: false
    }
  }
}
</script>

<style scoped>
.chapter-item { border-bottom: 1px solid #ebeef5; }
.chapter-title {
  padding: 8px 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
}
.chapter-title:hover { background: #f5f7fa; }
.chapter-title i { margin-right: 6px; color: #909399; }
.chapter-content { padding: 8px 12px 12px 28px; }
.analysis-result pre {
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 13px;
  line-height: 1.7;
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  margin: 0;
}
.no-analysis { color: #c0c4cc; font-size: 13px; }
</style>
```

- [ ] **Step 3: 创建 VolumeCard.vue**

```vue
<template>
  <div class="volume-card">
    <div class="volume-header" @click="collapsed = !collapsed">
      <i :class="collapsed ? 'el-icon-caret-right' : 'el-icon-caret-bottom'" />
      <strong>第{{ volume.idx + 1 }}卷 {{ volume.title }}</strong>
      <span class="chapter-count">（{{ chapters.length }}章）</span>
    </div>
    <div v-show="!collapsed">
      <div v-if="volume.summary" class="volume-summary">{{ volume.summary }}</div>
      <ChapterItem
        v-for="chapter in chapters"
        :key="chapter.id"
        :chapter="chapter"
      />
      <div v-if="chapters.length === 0" class="no-chapters">暂无章节</div>
    </div>
  </div>
</template>

<script>
import ChapterItem from './ChapterItem'

export default {
  name: 'VolumeCard',
  components: { ChapterItem },
  props: {
    volume: Object,
    chapters: Array
  },
  data() {
    return {
      collapsed: false
    }
  }
}
</script>

<style scoped>
.volume-card {
  background: #fff;
  border-radius: 4px;
  margin-bottom: 8px;
  border: 1px solid #ebeef5;
}
.volume-header {
  padding: 12px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 15px;
  border-bottom: 1px solid #ebeef5;
}
.volume-header:hover { background: #f5f7fa; }
.volume-header i { margin-right: 8px; color: #909399; }
.chapter-count { color: #909399; font-size: 13px; margin-left: 6px; }
.volume-summary {
  padding: 12px 16px 12px 32px;
  color: #606266;
  font-size: 13px;
  line-height: 1.7;
  border-bottom: 1px solid #ebeef5;
  background: #fafbfc;
}
.no-chapters { padding: 12px 16px 12px 32px; color: #c0c4cc; font-size: 13px; }
</style>
```

- [ ] **Step 4: 创建 OutlineSection.vue**

```vue
<template>
  <div class="outline-section">
    <div class="outline-header" @click="collapsed = !collapsed">
      <i :class="collapsed ? 'el-icon-caret-right' : 'el-icon-caret-bottom'" />
      <strong>生成纲要</strong>
    </div>
    <div v-show="!collapsed">
      <div v-if="error" class="outline-error">{{ error }}</div>
      <div v-else-if="!data" class="outline-empty">纲要尚未生成</div>
      <pre v-else class="outline-json">{{ JSON.stringify(data, null, 2) }}</pre>
    </div>
  </div>
</template>

<script>
import { getOutline } from '@/api/novel'

export default {
  name: 'OutlineSection',
  props: {
    novelId: [Number, String]
  },
  data() {
    return {
      collapsed: false,
      data: null,
      error: null
    }
  },
  watch: {
    novelId: {
      immediate: true,
      handler() { this.fetch() }
    }
  },
  methods: {
    async fetch() {
      this.error = null
      this.data = null
      try {
        const res = await getOutline(this.novelId)
        if (res.data.message || res.data.error) {
          this.error = res.data.message || res.data.error
        } else {
          this.data = res.data
        }
      } catch {
        this.error = '加载纲要失败'
      }
    }
  }
}
</script>

<style scoped>
.outline-section {
  background: #fff;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  margin-top: 16px;
}
.outline-header {
  padding: 12px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 15px;
  border-bottom: 1px solid #ebeef5;
}
.outline-header:hover { background: #f5f7fa; }
.outline-header i { margin-right: 8px; color: #909399; }
.outline-json {
  padding: 16px;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 13px;
  line-height: 1.6;
  max-height: 500px;
  overflow-y: auto;
}
.outline-empty, .outline-error {
  padding: 16px;
  color: #c0c4cc;
  font-size: 13px;
}
.outline-error { color: #f56c6c; }
</style>
```

- [ ] **Step 5: 创建 NovelDetail.vue**

```vue
<template>
  <div v-loading="loading">
    <NovelHeader v-if="novel" :novel="novel" :progress="progress" />

    <el-card v-if="volumes.length === 0 && !loading" class="empty-card">
      <p>暂无卷和章节数据</p>
    </el-card>

    <VolumeCard
      v-for="volume in volumes"
      :key="volume.id"
      :volume="volume"
      :chapters="chaptersByVolume(volume.id)"
    />

    <OutlineSection v-if="novel" :novelId="novel.id" />
  </div>
</template>

<script>
import { getNovel, getProgress, getVolumesByNovel, getChaptersByNovel } from '@/api/novel'
import NovelHeader from '@/components/NovelHeader'
import VolumeCard from '@/components/VolumeCard'
import OutlineSection from '@/components/OutlineSection'

export default {
  name: 'NovelDetail',
  components: { NovelHeader, VolumeCard, OutlineSection },
  data() {
    return {
      novel: null,
      progress: {},
      volumes: [],
      chapters: [],
      loading: false
    }
  },
  created() {
    this.fetchAll()
  },
  methods: {
    async fetchAll() {
      this.loading = true
      const id = this.$route.params.id
      try {
        const [novelRes, volRes, chRes, progRes] = await Promise.all([
          getNovel(id),
          getVolumesByNovel(id),
          getChaptersByNovel(id),
          getProgress(id)
        ])
        this.novel = novelRes.data
        this.volumes = volRes.data
        this.chapters = chRes.data
        this.progress = progRes.data
      } finally {
        this.loading = false
      }
    },
    chaptersByVolume(volumeId) {
      return this.chapters.filter(c => c.volumeId === volumeId)
    }
  }
}
</script>

<style scoped>
.empty-card { text-align: center; color: #909399; }
</style>
```

- [ ] **Step 6: 提交**

```bash
cd D:\Project\Front\novel-admin && git add -A && git commit -m "feat: add NovelDetail page with volumes, chapters, and outline"
```

---

### Task 5: 端到端验证

- [ ] **Step 1: 确认后端运行**

```bash
curl http://localhost:8088/api/novels
```
预期：返回 JSON 数组（可能为空）

- [ ] **Step 2: 启动前端并验证页面**

```bash
cd D:\Project\Front\novel-admin && npm run serve
```

打开 `http://localhost:3000`：

- [ ] **小说列表页**：表格展示已有小说（如果有），状态标签颜色正确
- [ ] **上传**：点击上传按钮 → 弹窗选择文件 + 规则 → 上传成功 → 列表刷新
- [ ] **分析**：点击分析按钮 → 按钮进入 loading → 完成后状态更新
- [ ] **删除**：点击删除 → 确认弹窗 → 删除成功
- [ ] **详情页**：点击小说行 → 跳转详情页
- [ ] **卷展示**：卷卡片可折叠，显示摘要和章节
- [ ] **章节展示**：点击章节展开 AI 分析结果
- [ ] **纲要展示**：底部显示生成的纲要 JSON

- [ ] **Step 3: 修复发现的问题并提交**

---

## Spec 自检

| 检查项 | 结果 |
|--------|------|
| 无 TBD/TODO | ✓ |
| 步骤有实际代码 | ✓ |
| Spec 需求全覆盖 | ✓ |
| 文件路径精确 | ✓ |
