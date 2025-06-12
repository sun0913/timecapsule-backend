package com.timecapsule.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timecapsule.modules.user.dto.request.UserLoginRequest;
import com.timecapsule.modules.user.dto.request.UserRegisterRequest;
import com.timecapsule.modules.user.entity.User;
import com.timecapsule.modules.user.vo.UserLoginVO;
import com.timecapsule.modules.user.vo.UserVO;

/**
 * 用户服务接口
 * 继承 IService<User> 后自动拥有以下方法：
 * - save(T entity)
 * - saveBatch(Collection<T> entityList)
 * - removeById(Serializable id)
 * - removeByMap(Map<String, Object> columnMap)
 * - remove(Wrapper<T> queryWrapper)
 * - removeByIds(Collection<? extends Serializable> idList)
 * - updateById(T entity)
 * - update(Wrapper<T> updateWrapper)
 * - update(T entity, Wrapper<T> updateWrapper)
 * - updateBatchById(Collection<T> entityList)
 * - saveOrUpdate(T entity)
 * - getById(Serializable id)
 * - listByIds(Collection<? extends Serializable> idList)
 * - listByMap(Map<String, Object> columnMap)
 * - getOne(Wrapper<T> queryWrapper)
 * - getMap(Wrapper<T> queryWrapper)
 * - count()
 * - count(Wrapper<T> queryWrapper)
 * - list()
 * - list(Wrapper<T> queryWrapper)
 * - page(IPage<T> page)
 * - page(IPage<T> page, Wrapper<T> queryWrapper)
 */
public interface UserService extends IService<User> {

    // 只定义业务方法，基础 CRUD 不需要定义

    /**
     * 用户注册
     */
    UserLoginVO register(UserRegisterRequest request);

    /**
     * 用户登录
     */
    UserLoginVO login(UserLoginRequest request);

    /**
     * 刷新Token
     */
    UserLoginVO refreshToken(String refreshToken);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser();

    /**
     * 修改密码
     */
    void changePassword(String oldPassword, String newPassword);
}