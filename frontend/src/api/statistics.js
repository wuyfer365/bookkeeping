import request from './request'

/** 汇总统计 */
export const getSummary = (params) => request.get('/statistics/summary', { params })

/** 按分类统计 */
export const getByCategory = (params) => request.get('/statistics/by-category', { params })

/** 趋势统计 */
export const getTrend = (params) => request.get('/statistics/trend', { params })
