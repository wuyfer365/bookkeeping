import request from './request'

/** 分页查询花销 */
export const getExpenses = (params) => request.get('/expenses', { params })

/** 查询单条花销 */
export const getExpenseById = (id) => request.get(`/expenses/${id}`)

/** 新增花销 */
export const createExpense = (data) => request.post('/expenses', data)

/** 更新花销 */
export const updateExpense = (id, data) => request.put(`/expenses/${id}`, data)

/** 删除花销 */
export const deleteExpense = (id) => request.delete(`/expenses/${id}`)
