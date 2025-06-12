package com.timecapsule.modules.user.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timecapsule.common.exception.BusinessException;
import com.timecapsule.common.result.ResultCode;
import com.timecapsule.common.utils.JwtUtils;
import com.timecapsule.modules.user.dto.request.UserLoginRequest;
import com.timecapsule.modules.user.dto.request.UserRegisterRequest;
import com.timecapsule.modules.user.entity.User;
import com.timecapsule.modules.user.entity.LoginUser;
import com.timecapsule.modules.user.mapper.UserMapper;
import com.timecapsule.modules.user.service.UserService;
import com.timecapsule.modules.user.vo.UserLoginVO;
import com.timecapsule.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 继承 ServiceImpl 后，自动拥有所有 IService 的实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO register(UserRegisterRequest request) {
        // 使用 MyBatis Plus 的 Lambda 查询
        long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXIST);
        }

        // 检查邮箱（使用 lambdaQuery）
        if (StrUtil.isNotBlank(request.getEmail())) {
            boolean exists = this.lambdaQuery()
                    .eq(User::getEmail, request.getEmail())
                    .exists();
            if (exists) {
                throw new BusinessException(ResultCode.EMAIL_ALREADY_EXIST);
            }
        }

        // 创建用户
        User user = new User();
        user.setUserId(generateUserId());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getUsername());
        user.setLevel(1);
        user.setExp(0);
        user.setReputation(100);
        user.setStatus(1);
        user.setRegisterSource(request.getPlatform());

        // 使用 MyBatis Plus 的 save 方法
        this.save(user);

        // 生成Token并返回
        return buildLoginVO(user, true);
    }

    @Override
    public UserLoginVO login(UserLoginRequest request) {
        // 使用 lambdaQuery 查询用户
        User user = this.lambdaQuery()
                .eq(User::getUsername, request.getUsername())
                .one();

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 检查状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 使用 lambdaUpdate 更新最后登录时间
        this.lambdaUpdate()
                .eq(User::getId, user.getId())
                .set(User::getLastLoginTime, LocalDateTime.now())
                .set(User::getLastLoginIp, "127.0.0.1")
                .update();

        return buildLoginVO(user, false);
    }

    @Override
    public UserLoginVO refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public UserVO getCurrentUser() {
        return null;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    // 其他业务方法实现...

    /**
     * 构建登录响应
     */
    private UserLoginVO buildLoginVO(User user, boolean isNewUser) {
        String token = jwtUtils.generateToken(user.getUserId(), user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId());

        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setToken(token);
        vo.setRefreshToken(refreshToken);
        vo.setExpiresIn(7200L);
        vo.setIsNewUser(isNewUser);
        vo.setNeedBindWallet(true);

        return vo;
    }

    private String generateUserId() {
        return "U" + IdUtil.getSnowflakeNextIdStr();
    }

    /**
     * 根据用户名加载用户信息（Spring Security认证核心方法）
     * @param username 用户名
     * @return UserDetails 用户详情
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.lambdaQuery()
                .eq(User::getUsername, username)
                .one();
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        // 权限可根据实际业务扩展
        return new LoginUser(user, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}