<template>
  <el-table :data="data" stripe border style="width: 100%" v-loading="loading">
    <el-table-column prop="expenseDate" label="日期" width="120" sortable />
    <el-table-column label="分类" width="160">
      <template #default="{ row }">
        <el-tag type="primary" size="small">{{ row.parentCategoryName }}</el-tag>
        <span style="margin-left: 4px; color: #909399; font-size: 13px">{{ row.categoryName }}</span>
      </template>
    </el-table-column>
    <el-table-column label="金额" width="140" align="right">
      <template #default="{ row }">
        <span class="amount-text">{{ formatRMB(row.amount) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="description" label="备注" min-width="180" show-overflow-tooltip />
    <el-table-column label="操作" width="160" fixed="right">
      <template #default="{ row }">
        <el-button size="small" @click="$emit('edit', row)">编辑</el-button>
        <el-button size="small" type="danger" @click="$emit('delete', row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { formatRMB } from '../utils/format'

defineProps({
  data: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false }
})

defineEmits(['edit', 'delete'])
</script>

<style scoped>
.amount-text {
  font-weight: bold;
  color: #f56c6c;
  font-family: 'Courier New', monospace;
  font-size: 15px;
}
</style>
