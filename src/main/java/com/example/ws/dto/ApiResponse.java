package com.example.ws.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "統一 API 回應格式")
public record ApiResponse<T>(
        @Schema(description = "回應成功失敗", example = "true") boolean success,
        @Schema(description = "錯誤訊息，成功時為空", example = "") String message,
        @Schema(description = "錯誤代碼，成功時為0000", example = "0000") String code,
        @Schema(description = "資料內容") T data,
        @Schema(description = "時間戳記") LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "", "0000", data, LocalDateTime.now());
    }
    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, "", "0000", null, LocalDateTime.now());
    }
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, message, code,null, LocalDateTime.now());
    }

    public static ApiResponse<String> error(String message) {
        return new ApiResponse<>(false, message, "99998",null, LocalDateTime.now());
    }
}