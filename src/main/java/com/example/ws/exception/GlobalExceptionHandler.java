package com.example.ws.exception;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 樂觀鎖衝突
    @ExceptionHandler({OptimisticLockingFailureException.class, MybatisPlusException.class})
    public ResponseEntity<String> handleOptimisticLockException(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("操作失敗：資料已被其他使用者修改，請重新讀取後再嘗試。");
    }

    // 其他未處理異常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("伺服器錯誤：" + ex.getMessage());
    }
}