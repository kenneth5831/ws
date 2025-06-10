package com.example.ws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ws.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}