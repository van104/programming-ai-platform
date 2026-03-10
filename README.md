# 融合大语言模型的高校编程教学智能支持平台

这是一个专为高校编程教学设计的智能支持平台，旨在通过集成大语言模型（LLM）能力，为学生提供智能代码分析、练习评价，并为教师提供教学数据洞察。

## 🚀 项目概述

本项目采用前后端分离架构，核心能力围绕“教学支持”与“智能反馈”展开。通过对学生编程过程数据的采集与 AI 分析，实现个性化的学习辅导。

## 🏗️ 核心架构

- **前端 (ai-platform-frontend)**: 基于 **Vue 3** + **Vite** + **Element Plus** 的现代化响应式界面。
- **后端 (ai-platform-backend)**: 基于 **Spring Boot 3** + **MyBatis-Plus** 的企业级工程化设计。
- **数据库 (MySQL)**: 存储用户信息、编程练习记录及 AI 分析结果。
- **AI 集成**: 深度集成 **Google Gemini API**，实现代码自动化评价与改进建议。

## ✨ 主要功能 (1.0 版本)

1.  **项目初始化与工程化**: 标准化前后端项目结构，实现统一接口协议（Result 封装）与跨域处理。
2.  **持久化数据存储**: 完整的用户管理系统与编程练习记录管理。
3.  **用户中心**: 实现基于角色（教师/学生）的登录与权限管理。
4.  **编程练习管理**: 支持代码提交、记录查看及练习过程数据采集。
5.  **AI 智能评价 (核心)**: 调用大语言模型对代码进行多维度分析（逻辑错误、代码风格、优化建议），提供即时反馈。
6.  **教学数据看板**: 学生侧展示练习统计，教师侧分析班级整体编程水平与常见错误。

## 🛠️ 技术栈

### 后端 (Backend)
- Java 17
- Spring Boot 3.5.x
- MyBatis-Plus (持久化)
- MySQL (数据库)
- Gemini API (AI 能力)

### 前端 (Frontend)
- Vue 3 (Composition API)
- Vite (构建工具)
- Element Plus (UI 组件库)
- Axios (网络请求)
- Pinia (状态管理)
- Vue Router (路由)

## 📁 项目目录结构

```text
programming-ai-platform
├── ai-platform-backend   # Spring Boot 后端项目
│   ├── src/main/java     # 业务代码 (Controller, Service, Mapper, Common, Entity)
│   └── src/main/resources  # 配置文件
├── ai-platform-frontend  # Vue 3 前端项目
│   ├── src/views         # 页面组件
│   ├── src/components    # 通用组件
│   └── src/stores        # 状态管理
└── README.md             # 项目说明文档
```

## 🚥 快速开始

### 1. 克隆项目
```bash
git clone [repository-url]
cd programming-ai-platform
```

### 2. 后端启动
- 确保已安装 **JDK 17** 和 **MySQL**。
- 修改 `application.yml` 中的数据库配置及 API Key。
- 运行 `AiPlatformApplication.java`。

### 3. 前端启动
- 进入 `ai-platform-frontend` 目录。
- 安装依赖并启动：
```bash
npm install
npm run dev
```

---

*本平台由 [czjtu] 课程教学团队研发，旨在探索 AI 在计算机编程教育中的深度应用。*
