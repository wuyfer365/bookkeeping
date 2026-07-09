package com.bookkeeping.service.impl;

import com.bookkeeping.common.PageResult;
import com.bookkeeping.dto.*;
import com.bookkeeping.entity.Category;
import com.bookkeeping.entity.Expense;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.mapper.CategoryMapper;
import com.bookkeeping.mapper.ExpenseMapper;
import com.bookkeeping.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseMapper expenseMapper;
    private final CategoryMapper categoryMapper;

    public ExpenseServiceImpl(ExpenseMapper expenseMapper, CategoryMapper categoryMapper) {
        this.expenseMapper = expenseMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PageResult<ExpenseVO> list(ExpenseQueryDTO query) {
        List<ExpenseVO> records = expenseMapper.findWithCategory(query);
        long total = expenseMapper.count(query);
        return new PageResult<>(records, total, query.getPage(), query.getSize());
    }

    @Override
    public ExpenseVO getById(Integer id) {
        ExpenseVO vo = expenseMapper.findById(id);
        if (vo == null) {
            throw new BusinessException(404, "花销记录不存在");
        }
        return vo;
    }

    @Override
    public ExpenseVO create(ExpenseCreateDTO dto) {
        // 校验分类存在且是二级分类
        Category cat = categoryMapper.findById(dto.getCategoryId());
        if (cat == null) {
            throw new BusinessException("分类不存在");
        }
        if (cat.getLevel() != 2) {
            throw new BusinessException("请选择二级分类（小类）");
        }

        Expense expense = new Expense();
        expense.setAmount(dto.getAmount());
        expense.setCategoryId(dto.getCategoryId());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setDescription(dto.getDescription());
        expenseMapper.insert(expense);

        return expenseMapper.findById(expense.getId());
    }

    @Override
    public ExpenseVO update(Integer id, ExpenseCreateDTO dto) {
        // 确保记录存在
        ExpenseVO existing = getById(id);

        // 校验分类
        Category cat = categoryMapper.findById(dto.getCategoryId());
        if (cat == null) {
            throw new BusinessException("分类不存在");
        }
        if (cat.getLevel() != 2) {
            throw new BusinessException("请选择二级分类（小类）");
        }

        Expense expense = new Expense();
        expense.setId(id);
        expense.setAmount(dto.getAmount());
        expense.setCategoryId(dto.getCategoryId());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setDescription(dto.getDescription());
        expenseMapper.update(expense);

        return expenseMapper.findById(id);
    }

    @Override
    public void delete(Integer id) {
        getById(id); // 确保存在
        expenseMapper.deleteById(id);
    }
}
