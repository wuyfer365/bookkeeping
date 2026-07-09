<template>
  <div class="expense-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑账单' : '记一笔' }}</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 600px"
      >
        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0.01"
            :max="99999999.99"
            :precision="2"
            :step="10"
            controls-position="right"
            style="width: 100%"
            placeholder="请输入金额"
          />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <CategoryCascader v-model="form.categoryId" />
        </el-form-item>

        <el-form-item label="消费日期" prop="expenseDate">
          <el-date-picker
            v-model="form.expenseDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="选填，记录消费详情"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ isEdit ? '保存修改' : '确认添加' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import CategoryCascader from '../components/CategoryCascader.vue'
import { createExpense, updateExpense, getExpenseById } from '../api/expense'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  amount: null,
  categoryId: null,
  expenseDate: dayjs().format('YYYY-MM-DD'),
  description: ''
})

const rules = {
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  expenseDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const data = await getExpenseById(route.params.id)
      form.amount = data.amount
      form.categoryId = data.categoryId
      form.expenseDate = data.expenseDate
      form.description = data.description || ''
    } catch {
      router.push('/expenses')
    }
  }
})

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateExpense(route.params.id, { ...form })
      ElMessage.success('修改成功')
    } else {
      await createExpense({ ...form })
      ElMessage.success('添加成功')
    }
    router.push('/expenses')
  } finally {
    submitting.value = false
  }
}
</script>
