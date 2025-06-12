// UserMapper.java - 优化版
package com.timecapsule.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.timecapsule.modules.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 * 继承 BaseMapper 后自动拥有以下方法：
 * - insert(T entity)
 * - deleteById(Serializable id)
 * - deleteByMap(Map<String, Object> columnMap)
 * - delete(Wrapper<T> wrapper)
 * - deleteBatchIds(Collection<? extends Serializable> idList)
 * - updateById(T entity)
 * - update(T entity, Wrapper<T> updateWrapper)
 * - selectById(Serializable id)
 * - selectBatchIds(Collection<? extends Serializable> idList)
 * - selectByMap(Map<String, Object> columnMap)
 * - selectOne(Wrapper<T> queryWrapper)
 * - selectCount(Wrapper<T> queryWrapper)
 * - selectList(Wrapper<T> queryWrapper)
 * - selectMaps(Wrapper<T> queryWrapper)
 * - selectObjs(Wrapper<T> queryWrapper)
 * - selectPage(IPage<T> page, Wrapper<T> queryWrapper)
 * - selectMapsPage(IPage<Map<String, Object>> page, Wrapper<T> queryWrapper)
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 只需要定义 MyBatis Plus 没有提供的特殊查询方法
    // 如果使用 LambdaQueryWrapper，以下方法都可以不要

    /**
     * 使用注解方式的简单查询（可选）
     */
    @Select("SELECT * FROM tc_user WHERE username = #{username} AND deleted = 0 LIMIT 1")
    User selectByUsername(@Param("username") String username);

    // 复杂查询建议使用 XML 文件
}
