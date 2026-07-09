package com.bookkeeping.service;

import com.bookkeeping.dto.*;
import com.bookkeeping.common.PageResult;

/**
 * 花销服务接口
 */
public interface ExpenseService {

    /** 分页查询 */
    PageResult<ExpenseVO> list(ExpenseQueryDTO query);

    /** 查单条 */
    ExpenseVO getById(Integer id);

    /** 新增 */
    ExpenseVO create(ExpenseCreateDTO dto);

    /** 更新 */
    ExpenseVO update(Integer id, ExpenseCreateDTO dto);

    /** 删除 */
    void delete(Integer id);
}
