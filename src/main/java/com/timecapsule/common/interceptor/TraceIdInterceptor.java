package com.timecapsule.common.interceptor;

import com.timecapsule.common.utils.TraceIdUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 链路追踪ID拦截器
 */
@Slf4j
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取traceId，如果没有则生成一个
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = TraceIdUtils.generateTraceId();
        }

        // 设置traceId到MDC
        TraceIdUtils.setTraceId(traceId);

        // 设置到响应头
        response.setHeader(TRACE_ID_HEADER, traceId);

        log.debug("请求开始 - URL: {}, Method: {}, TraceId: {}",
                request.getRequestURI(), request.getMethod(), traceId);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        // 请求处理完成
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 清理MDC
        TraceIdUtils.clear();

        if (ex != null) {
            log.error("请求异常 - URL: {}, Error: {}", request.getRequestURI(), ex.getMessage());
        } else {
            log.debug("请求完成 - URL: {}, Status: {}", request.getRequestURI(), response.getStatus());
        }
    }
}