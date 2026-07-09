<template>
  <el-container class="app-layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="app-sidebar">
      <div class="logo">
        <el-icon :size="24"><Money /></el-icon>
        <span>记账APP</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/">
          <el-icon><DataBoard /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/expenses">
          <el-icon><List /></el-icon>
          <span>账单列表</span>
        </el-menu-item>
        <el-menu-item index="/expenses/add">
          <el-icon><CirclePlus /></el-icon>
          <span>记一笔</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><TrendCharts /></el-icon>
          <span>统计分析</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容 -->
    <el-container>
      <el-header class="app-header">
        <h2>{{ pageTitle }}</h2>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => {
  if (route.path.startsWith('/expenses')) return '/expenses'
  return route.path
})

const pageTitle = computed(() => route.meta?.title || '记账APP')
</script>

<style scoped>
.app-layout {
  height: 100vh;
}
.app-sidebar {
  background-color: #304156;
  overflow-y: auto;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.app-header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 24px;
}
.app-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
.app-main {
  background: #f0f2f5;
  min-height: 0;
  overflow-y: auto;
}
</style>
