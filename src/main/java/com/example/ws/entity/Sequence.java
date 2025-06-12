package com.example.ws.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sequence")
public class Sequence {
    @TableId
    private String name;
    private Long currentValue;
}