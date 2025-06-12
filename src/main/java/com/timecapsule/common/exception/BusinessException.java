package com.timecapsule.common.exception;

import com.timecapsule.common.result.IResultCode;
import com.timecapsule.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAILED.getCode();
    }

    public BusinessException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(IResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.FAILED.getCode();
    }
}