# 融合大语言模型的高校编程教学智能支持平台
(AI-Powered Programming Teaching Platform)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-blue.svg)](https://vuejs.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> 💡 **项目愿景**：这是一个专为高校编程教学设计的智能支持平台，旨在通过集成大语言模型（LLM）能力，为学生提供智能代码分析、练习评价，并为教师提供教学数据洞察。

---

## 📑 目录 (Table of Contents)

- [🚀 项目概述](#-项目概述)
- [🏗️ 核心架构](#️-核心架构)
- [✨ 主要功能 (1.0 版本)](#-主要功能-10-版本)
- [🛠️ 技术栈](#️-技术栈)
- [📁 目录结构](#-目录结构)
- [🚥 快速开始](#-快速开始)
  - [环境依赖](#1-环境依赖)
  - [后端部署](#2-后端部署)
  - [前端部署](#3-前端部署)
- [🤝 参与贡献](#-参与贡献)
- [📄 许可证](#-许可证)

---

## 🚀 项目概述

本项目采用主流的开源前后端分离架构，核心能力围绕 **“教学支持”** 与 **“智能反馈”** 展开。平台通过采集学生编程过程中的真实数据，利用 AI 大语言模型进行深度分析，不仅能够纠正语法错误，还能指导代码规范并提供优化思路，实现千人千面的个性化编程学习辅导。

---

## 🏗️ 核心架构

| 模块 | 说明 | 核心技术 |
| :--- | :--- | :--- |
| **🎨 前端 (Frontend)** | 现代化响应式前端界面，提供流畅的编码体验 | Vue 3, Vite, Element Plus, Pinia |
| **⚙️ 后端 (Backend)** | 企业级工程化设计的后端服务，处理高并发请求 | Spring Boot 3, Java 17, MyBatis-Plus |
| **💾 持久层 (Storage)** | 高效的结构化数据存储引擎 | MySQL 8.x |
| **🧠 智能核心 (AI Layer)** | 深度集成主流大模型 API 获取智能能力 | Google Gemini API / 兼容标准协议接口 |

---

## ✨ 主要功能 (1.0 版本)

- 📌 **工程基础架构**: 标准化前后端项目结构，实现统一接口协议（Result 封装）、全局异常拦截与跨域处理。
- 👥 **用户权限中心**: 基于角色的访问控制（RBAC），支持教师与学生双重角色登录与鉴权。
- 💻 **在线编程环境**: 包含实时代码编辑、代码提交历史追踪与学习行为数据采集。
- 🤖 **AI 智能评价看板 (核心)**: 
  - **逻辑诊断**：找出潜在 bug 和运行时异常
  - **规范审查**：基于业界标准提供代码风格修改建议
  - **重构引导**：指导学生使用更高效的数据结构与算法
- 📈 **全景数据洞察**: 
  - **学生侧**：能力雷达图、练习时长与提交次数统计
  - **教师侧**：班级整体学习进度概览、高频错误聚类分析

---

## 🛠️ 技术栈

### 💻 后端生态
- **语  言**: Java 17
- **框  架**: Spring Boot 3.5.x
- **持久层**: MyBatis-Plus
- **数据库**: MySQL

### 📱 前端生态
- **核心框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5
- **UI 组件**: Element Plus
- **状态管理**: Pinia
- **路  由**: Vue Router 4
- **网络请求**: Axios

---

## 📁 目录结构

```text
programming-ai-platform/
├── ai-platform-backend/        # ⚙️ Spring Boot 后端项目代码
│   ├── src/main/java/          # Java 源码目录 (Controller, Service, Entity...)
│   └── src/main/resources/     # Spring Boot 环境配置文件 (application.yml 等)
├── ai-platform-frontend/       # 🎨 Vue 3 前端项目代码
│   ├── src/assets/             # 静态资源文件
│   ├── src/components/         # 页面公共组件
│   ├── src/stores/             # Pinia 状态管理库
│   └── src/views/              # 路由视图层
├── package.json                # 全局依赖管理 (含 concurrently 一键启动脚本)
└── README.md                   # 项目整体说明文档
```

---

## 🚥 快速开始

### 1. 环境依赖

在运行项目前，请确保本地已在运行以下环境：
- **Node.js** >= 18.x
- **JDK** >= 17
- **MySQL** >= 8.0
- **Maven** >= 3.8+

### 2. 克隆项目

```bash
git clone https://github.com/your-username/programming-ai-platform.git
cd programming-ai-platform
```

### 3. 后端部署

1. 在 MySQL 中创建名为 `ai_platform` 的数据库（字符集推荐 `utf8mb4`）。
2. 定位到后端配置文件 `ai-platform-backend/src/main/resources/application.yml`。
3. 修改数据库连接凭证及 AI Provider API Key 配置：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/ai_platform?useUnicode=true&characterEncoding=utf-8
       username: root
       password: your_password
   ```
4. 使用 IDE (推荐 IntelliJ IDEA) 打开 `ai-platform-backend` 目录，找到 `AiPlatformApplication.java` 并执行 `Run` 开始运行，或通过 Maven 启动：
   ```bash
   cd ai-platform-backend
   mvn spring-boot:run
   ```
*(后端默认在 `http://localhost:8080` 启动)*

### 4. 前端部署

1. 打开终端进入前端工作目录：
   ```bash
   cd ai-platform-frontend
   ```
2. 安装依赖包：
   ```bash
   npm install
   ```
3. 启动开发服务器：
   ```bash
   npm run dev
   ```
*(前端默认在 `http://localhost:5173` 启动)*

> ⚡ **Tip (一键启动)**：在项目根目录运行 `npm install concurrently -D` 后，可直接在根目录执行 `npm run dev` 同时拉起前后端服务！

---

## 🤝 参与贡献

我们非常欢迎任何形式的贡献和反馈！你可以通过以下方式参与：

1. **提交 BUG**：如果发现问题，请通过提出 Issue 告知我们。
2. **贡献代码**：欢迎 Fork 本仓库并提交 Pull Request，我们会在代码 Review 后将你的修改合并。
3. **功能建议**：你有好的创意？欢迎在 Issue 中发起 Feature 请求讨论。

---

## 📄 许可证

本项目开源，受 [MIT License](LICENSE) 许可协议保护。

---

<p align="center">
  <i>本平台由 <b>[czjtu]</b> 课程教学团队倾力研发 <br/>旨在探索且验证 AI 大语言模型在计算机编程教育体系中的深度应用与无限潜力。</i>
</p>
