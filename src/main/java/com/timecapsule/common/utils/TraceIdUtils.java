package com.timecapsule.common.utils;

import org.slf4j.MDC;
import java.util.UUID;

/**
 * 链路追踪ID工具类
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
public class TraceIdUtils {

    private static final String TRACE_ID = "traceId";

    /**
     * 生成追踪ID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置追踪ID
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 获取追踪ID
     */
    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID);
        if (traceId == null) {
            traceId = generateTraceId();
            setTraceId(traceId);
        }
        return traceId;
    }

    /**
     * 清除追踪ID
     */
    public static void clear() {
        MDC.remove(TRACE_ID);
    }
}