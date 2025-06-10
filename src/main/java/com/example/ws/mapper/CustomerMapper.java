package com.example.ws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ws.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    @Select("SELECT COUNT(1) FROM customer WHERE id = #{id}")
    boolean existsById(@Param("id") Long customerId);
}