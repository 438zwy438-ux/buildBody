package com.cdp.zwy.buildbody.common.exception;

import com.cdp.zwy.buildbody.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zwy
 * @version 1.0
 * @description: GlobalExceptionHandler
 * @date 2026/2/14 17:12
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("系统运行异常: ", e);
        return Result.error(e.getMessage() != null ? e.getMessage() : "系统繁忙，请稍后再试");
    }

    // 这里以后可以扩展自定义异常，如 BusinessException
}