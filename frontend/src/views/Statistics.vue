<template>
  <div class="statistics">
    <!-- 日期筛选 -->
    <el-card style="margin-bottom: 16px;">
      <el-form :inline="true">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 汇总卡片 -->
    <el-row :gutter="20" class="summary-row">
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="card-label">总支出</div>
          <div class="card-value">{{ formatRMB(summary.totalAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="card-label">记账笔数</div>
          <div class="card-value">{{ summary.recordCount || 0 }}<span class="card-unit">笔</span></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="card-label">日均支出</div>
          <div class="card-value">{{ formatRMB(summary.avgPerDay) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>分类支出占比</template>
          <StatChart
            type="pie"
            :data="categoryStats"
            name-key="categoryName"
            value-key="totalAmount"
            :loading="loading"
            :height="350"
          />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>分类排行</template>
          <StatChart
            type="bar"
            :data="categoryStats"
            name-key="categoryName"
            value-key="totalAmount"
            :loading="loading"
            :height="350"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势 -->
    <el-card style="margin-top: 20px;">
      <template #header>近6月趋势</template>
      <StatChart
        type="line"
        :data="trendData"
        name-key="month"
        value-key="totalAmount"
        :loading="loading"
        :height="320"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import StatChart from '../components/StatChart.vue'
import { getSummary, getByCategory, getTrend } from '../api/statistics'
import { formatRMB } from '../utils/format'
import dayjs from 'dayjs'

const loading = ref(false)

const now = dayjs()
const dateRange = ref([
  now.startOf('month').format('YYYY-MM-DD'),
  now.format('YYYY-MM-DD')
])

const summary = ref({})
const categoryStats = ref([])
const trendData = ref([])

const fetchData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return

  loading.value = true
  try {
    const startDate = dateRange.value[0]
    const endDate = dateRange.value[1]
    const [s, cats, trend] = await Promise.all([
      getSummary({ startDate, endDate }),
      getByCategory({ startDate, endDate }),
      getTrend({ months: 6 })
    ])
    summary.value = s || {}
    categoryStats.value = cats || []
    trendData.value = trend || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.summary-card {
  text-align: center;
}
.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.card-unit {
  font-size: 14px;
  font-weight: normal;
  color: #909399;
  margin-left: 4px;
}
</style>
