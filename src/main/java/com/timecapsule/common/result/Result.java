package com.timecapsule.common.result;

import com.timecapsule.common.utils.TraceIdUtils;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应格式
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * API版本
     */
    private String version = "1.0.0";

    /**
     * 扩展字段（用于多端适配）
     */
    private Map<String, Object> extra;

    public Result() {
        this.timestamp = System.currentTimeMillis();
        this.traceId = TraceIdUtils.getTraceId();
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> fail() {
        return fail(ResultCode.FAILED.getMessage());
    }

    /**
     * 失败响应（带消息）
     */
    public static <T> Result<T> fail(String message) {
        return fail(ResultCode.FAILED.getCode(), message);
    }

    /**
     * 失败响应（带错误码和消息）
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（使用错误码枚举）
     */
    public static <T> Result<T> fail(IResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 添加扩展字段
     */
    public Result<T> addExtra(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
        return this;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}