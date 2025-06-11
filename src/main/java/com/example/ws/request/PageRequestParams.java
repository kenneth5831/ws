package com.example.ws.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class PageRequestParams<T> extends Page<T> {
    private int page;
    private long size;

    public PageRequestParams(int page, long size) {
        super(page, size);
        this.page = page;
        this.size = size;
    }

    @Override
    public Page<T> setSize(long size) {
        super.setSize(size);
        this.size = size;
        return this;
    }
}