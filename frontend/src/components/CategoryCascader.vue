<template>
  <el-cascader
    :model-value="modelValue"
    :options="options"
    :props="cascaderProps"
    placeholder="请选择分类"
    clearable
    filterable
    style="width: 100%"
    @update:model-value="$emit('update:modelValue', $event)"
  />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategoryTree } from '../api/category'

const props = defineProps({
  modelValue: [Number, Array]
})

defineEmits(['update:modelValue'])

const options = ref([])
const cascaderProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  checkStrictly: false,
  emitPath: false  // 只返回选中的末级节点ID
}

onMounted(async () => {
  try {
    options.value = await getCategoryTree()
  } catch {
    // 错误由拦截器处理
  }
})
</script>
