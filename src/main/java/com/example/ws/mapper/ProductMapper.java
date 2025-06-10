package com.example.ws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ws.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Select("SELECT COUNT(1) FROM product WHERE id = #{productId}")
    boolean existsById(@Param("productId") Long productId);
}