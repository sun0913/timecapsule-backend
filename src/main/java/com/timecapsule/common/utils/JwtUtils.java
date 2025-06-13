package com.timecapsule.common.utils;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expire-time}")
    private Long expireTime;

    @Value("${app.jwt.refresh-expire-time}")
    private Long refreshExpireTime;


    /**
     * 生成Token
     */
    public String generateToken(String userId, String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime * 1000);

        JwtBuilder builder = Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512);

        if (claims != null && !claims.isEmpty()) {
            builder.addClaims(claims);
        }

        return builder.compact();
    }

    /**
     * 生成Token（简化版）
     */
    public String generateToken(String userId, String username) {
        return generateToken(userId, username, null);
    }

    /**
     * 生成刷新Token
     */
    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpireTime * 1000);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 解析Token
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("Token已过期: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw new JwtException("Token解析失败");
        }
    }

    /**
     * 获取用户ID
     */
    public String getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {

        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("无效的Token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token验证异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取Token剩余有效时间（秒）
     */
    public long getTokenRemainingTime(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remainingMillis = expiration.getTime() - System.currentTimeMillis();
            return remainingMillis > 0 ? remainingMillis / 1000 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 刷新Token（保持原有信息）
     */
    public String refreshToken(String oldToken) {
        try {
            Claims claims = parseToken(oldToken);
            String userId = claims.getSubject();
            String username = claims.get("username", String.class);

            // 移除时间相关的声明
            claims.remove(Claims.ISSUED_AT);
            claims.remove(Claims.EXPIRATION);
            claims.remove(Claims.SUBJECT);
            claims.remove("username");

            return generateToken(userId, username, claims);
        } catch (Exception e) {
            log.error("刷新Token失败: {}", e.getMessage());
            throw new JwtException("刷新Token失败");
        }
    }

    /**
     * 获取密钥
     */
    /**
     * 获取密钥（改进版）
     */
    private SecretKey getSecretKey() {
        // 验证密钥长度
        validateSecretKey();

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 验证密钥是否符合要求
     */
    @PostConstruct
    private void validateSecretKey() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT密钥不能为空");
        }

        // 检查密钥长度（HS512需要至少64字节，但最小要求是32字节）
        int keyLength = secret.getBytes(StandardCharsets.UTF_8).length;
        if (keyLength < 32) {
            throw new IllegalStateException(
                    String.format("JWT密钥太短！当前长度：%d字节，最小要求：32字节（256位）。" +
                            "建议使用64字节（512位）以获得更好的安全性。", keyLength)
            );
        }

        // 警告：如果密钥长度小于64字节（针对HS512）
        if (keyLength < 64) {
            log.warn("JWT密钥长度为{}字节，建议使用64字节（512位）以获得更好的安全性", keyLength);
        }

        log.info("JWT密钥验证通过，密钥长度：{}字节", keyLength);
    }
    /**
     * 从请求头中提取Token
     */
    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}