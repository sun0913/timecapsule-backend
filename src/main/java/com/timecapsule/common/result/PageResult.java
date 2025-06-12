package com.timecapsule.common.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * 分页响应格式
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResult<T> extends Result<PageResult.PageData<T>> {

    @Data
    public static class PageData<T> {
        /**
         * 数据列表
         */
        private List<T> list;

        /**
         * 总记录数
         */
        private Long total;

        /**
         * 当前页码
         */
        private Integer pageNum;

        /**
         * 每页大小
         */
        private Integer pageSize;

        /**
         * 总页数
         */
        private Integer pages;

        /**
         * 是否有下一页（移动端使用）
         */
        private Boolean hasNext;

        /**
         * 滚动分页标识（移动端使用）
         */
        private String scrollId;

        public PageData() {
        }

        public PageData(List<T> list, Long total, Integer pageNum, Integer pageSize) {
            this.list = list;
            this.total = total;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.pages = (int) Math.ceil((double) total / pageSize);
            this.hasNext = pageNum < this.pages;
        }
    }

    /**
     * 创建分页响应
     */
    public static <T> PageResult<T> success(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(new PageData<>(list, total, pageNum, pageSize));
        return result;
    }

    /**
     * 创建空分页响应
     */
    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        return success(List.of(), 0L, pageNum, pageSize);
    }
}