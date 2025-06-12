package com.timecapsule.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Slf4j
@Configuration
@MapperScan("com.timecapsule.**.mapper")
public class MybatisPlusConfig {

    /**
     * MyBatis Plus插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L); // 最大查询限制
        paginationInnerInterceptor.setOptimizeJoin(true); // 优化JOIN查询
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 添加乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }

    /**
     * 自动填充处理器
     */
    @Component
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            log.debug("开始插入填充...");

            // 创建时间
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            // 更新时间
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            // 删除标记
            this.strictInsertFill(metaObject, "deleted", Integer.class, 0);

            // 如果有创建人字段，可以从安全上下文获取
            // String createBy = SecurityUtils.getCurrentUsername();
            // this.strictInsertFill(metaObject, "createBy", String.class, createBy);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            log.debug("开始更新填充...");

            // 更新时间
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

            // 如果有更新人字段，可以从安全上下文获取
            // String updateBy = SecurityUtils.getCurrentUsername();
            // this.strictUpdateFill(metaObject, "updateBy", String.class, updateBy);
        }
    }
}