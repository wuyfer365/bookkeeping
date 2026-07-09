import request from './request'

/** 获取全部分类树 */
export const getCategoryTree = () => request.get('/categories')

/** 获取单个分类 */
export const getCategoryById = (id) => request.get(`/categories/${id}`)

/** 新建分类 */
export const createCategory = (data) => request.post('/categories', data)

/** 更新分类 */
export const updateCategory = (id, data) => request.put(`/categories/${id}`, data)

/** 删除分类 */
export const deleteCategory = (id) => request.delete(`/categories/${id}`)
