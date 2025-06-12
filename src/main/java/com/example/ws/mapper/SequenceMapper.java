package com.example.ws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ws.entity.Sequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SequenceMapper extends BaseMapper<Sequence> {

    @Select("SELECT * FROM sequence WHERE name = #{name} FOR UPDATE")
    Sequence selectForUpdate(String name);

    @Update("UPDATE sequence SET current_value = #{currentValue} WHERE name = #{name}")
    int updateValue(Sequence sequence);
}