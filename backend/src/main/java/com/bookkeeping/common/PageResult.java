package com.bookkeeping.common;

import java.util.List;

/**
 * 分页响应封装
 */
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int pageSize;

    public PageResult(List<T> records, long total, int page, int pageSize) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getRecords() { return records; }
    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
}
