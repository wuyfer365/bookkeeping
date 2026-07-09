<template>
  <div class="dashboard">
    <!-- 汇总卡片 -->
    <el-row :gutter="20" class="summary-row">
      <el-col :span="8">
        <el-card shadow="hover" class="summary-card">
          <div class="card-label">本月支出</div>
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

    <!-- 图表 + 最近记录 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 分类饼图 -->
      <el-col :span="12">
        <el-card>
          <template #header>本月分类占比</template>
          <StatChart
            type="pie"
            :data="categoryStats"
            name-key="categoryName"
            value-key="totalAmount"
            :loading="loading"
            :height="320"
          />
        </el-card>
      </el-col>

      <!-- 近6月趋势 -->
      <el-col :span="12">
        <el-card>
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
      </el-col>
    </el-row>

    <!-- 最近记录 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>最近记录</span>
        <el-button style="float: right;" size="small" type="primary" @click="$router.push('/expenses')">
          查看全部
        </el-button>
      </template>
      <ExpenseTable
        :data="recentExpenses"
        :loading="loading"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import StatChart from '../components/StatChart.vue'
import ExpenseTable from '../components/ExpenseTable.vue'
import { getSummary, getByCategory, getTrend } from '../api/statistics'
import { getExpenses, deleteExpense } from '../api/expense'
import { formatRMB } from '../utils/format'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)

// 默认统计本月
const now = dayjs()
const startDate = now.startOf('month').format('YYYY-MM-DD')
const endDate = now.format('YYYY-MM-DD')

const summary = ref({})
const categoryStats = ref([])
const trendData = ref([])
const recentExpenses = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const [s, cats, trend, expenses] = await Promise.all([
      getSummary({ startDate, endDate }),
      getByCategory({ startDate, endDate }),
      getTrend({ months: 6 }),
      getExpenses({ page: 1, size: 10 })
    ])
    summary.value = s || {}
    categoryStats.value = cats || []
    trendData.value = trend || []
    recentExpenses.value = expenses?.records || []
  } finally {
    loading.value = false
  }
})

const handleEdit = (row) => {
  router.push(`/expenses/${row.id}/edit`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteExpense(row.id)
    ElMessage.success('删除成功')
    recentExpenses.value = recentExpenses.value.filter(e => e.id !== row.id)
  } catch {
    // 取消删除
  }
}
</script>

<style scoped>
.summary-row {
  margin-bottom: 0;
}
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
