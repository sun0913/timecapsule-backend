package com.timecapsule.common.result;

/**
 * 响应码接口
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
public interface IResultCode {
    /**
     * 获取响应码
     */
    Integer getCode();

    /**
     * 获取响应消息
     */
    String getMessage();
}