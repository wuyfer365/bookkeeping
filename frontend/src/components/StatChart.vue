<template>
  <div class="stat-chart" v-loading="loading">
    <div v-if="!loading && (!data || data.length === 0)" class="empty-hint">
      暂无数据
    </div>
    <v-chart
      v-else-if="!loading && data && data.length > 0"
      :option="chartOption"
      :style="{ width: '100%', height: height + 'px' }"
      autoresize
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { PieChart, BarChart, LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册所需 ECharts 模块
use([PieChart, BarChart, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const props = defineProps({
  type: { type: String, default: 'pie' },  // pie | bar | line
  data: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  height: { type: Number, default: 350 },
  nameKey: { type: String, default: 'name' },     // 数据中用作名称的字段
  valueKey: { type: String, default: 'value' },   // 数据中用作值的字段
})

const chartOption = computed(() => {
  const names = props.data.map(d => d[props.nameKey])
  const values = props.data.map(d => d[props.valueKey])

  switch (props.type) {
    case 'pie':
      return {
        tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
        legend: { type: 'scroll', orient: 'vertical', right: 10, top: 20, bottom: 20 },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['40%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
          data: names.map((n, i) => ({ name: n, value: values[i] }))
        }]
      }
    case 'bar':
      return {
        tooltip: { trigger: 'axis', formatter: '{b}: ¥{c}' },
        grid: { left: 60, right: 20, top: 20, bottom: 60 },
        xAxis: { type: 'category', data: names, axisLabel: { rotate: 45 } },
        yAxis: { type: 'value' },
        series: [{ type: 'bar', data: values, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
      }
    case 'line':
      return {
        tooltip: { trigger: 'axis', formatter: '{b}: ¥{c}' },
        grid: { left: 60, right: 20, top: 20, bottom: 30 },
        xAxis: { type: 'category', data: names, boundaryGap: false },
        yAxis: { type: 'value' },
        series: [{
          type: 'line',
          data: values,
          smooth: true,
          areaStyle: { opacity: 0.15 },
          lineStyle: { width: 3 }
        }]
      }
    default:
      return {}
  }
})
</script>

<style scoped>
.stat-chart {
  min-height: 200px;
}
.empty-hint {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #909399;
  font-size: 14px;
}
</style>
