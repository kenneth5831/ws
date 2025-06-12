package com.example.ws.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class PageRequestParams<T> extends Page<T> {
    public PageRequestParams(int page, long size) {
        super(page, size);
    }
}