import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { title: '首页概览' }
  },
  {
    path: '/expenses',
    name: 'expenseList',
    component: () => import('../views/ExpenseList.vue'),
    meta: { title: '账单列表' }
  },
  {
    path: '/expenses/add',
    name: 'expenseAdd',
    component: () => import('../views/ExpenseForm.vue'),
    meta: { title: '记一笔' }
  },
  {
    path: '/expenses/:id/edit',
    name: 'expenseEdit',
    component: () => import('../views/ExpenseForm.vue'),
    meta: { title: '编辑账单' }
  },
  {
    path: '/statistics',
    name: 'statistics',
    component: () => import('../views/Statistics.vue'),
    meta: { title: '统计分析' }
  },
  {
    path: '/snake-game',
    name: 'snakeGame',
    component: () => import('../views/SnakeGame.vue'),
    meta: { title: '贪吃蛇 🐍' }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
