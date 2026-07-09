package com.bookkeeping.controller;

import com.bookkeeping.common.PageResult;
import com.bookkeeping.common.Result;
import com.bookkeeping.dto.ExpenseCreateDTO;
import com.bookkeeping.dto.ExpenseQueryDTO;
import com.bookkeeping.dto.ExpenseVO;
import com.bookkeeping.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 花销管理 Controller
 */
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /** 分页查询 */
    @GetMapping
    public Result<PageResult<ExpenseVO>> list(@Valid ExpenseQueryDTO query) {
        return Result.success(expenseService.list(query));
    }

    /** 查单条 */
    @GetMapping("/{id}")
    public Result<ExpenseVO> getById(@PathVariable Integer id) {
        return Result.success(expenseService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<ExpenseVO> create(@Valid @RequestBody ExpenseCreateDTO dto) {
        return Result.success(expenseService.create(dto));
    }

    /** 更新 */
    @PutMapping("/{id}")
    public Result<ExpenseVO> update(@PathVariable Integer id, @Valid @RequestBody ExpenseCreateDTO dto) {
        return Result.success(expenseService.update(id, dto));
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        expenseService.delete(id);
        return Result.success();
    }
}
