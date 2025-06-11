package com.example.ws.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 所有 Entity 的基底，提供樂觀鎖版本欄位
 */
@Getter
@Setter
public abstract class BaseEntity {

    @Version
    @Schema(description = "版本（樂觀鎖）", example = "1")
    private Integer version;

    @TableLogic
    @Schema(description = "是否已刪除（0：未刪除，1：已刪除）", example = "0")
    private Integer deleted;
}