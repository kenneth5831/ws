package com.example.ws.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 含建立/更新時間的抽象 Entity，可被各 Entity 繼承使用
 */
@Getter
@Setter
public abstract class AuditableEntity extends BaseEntity {

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "建立時間", example = "2025-06-11T12:00:00")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新時間", example = "2025-06-11T12:00:00")
    private LocalDateTime updatedAt;
}