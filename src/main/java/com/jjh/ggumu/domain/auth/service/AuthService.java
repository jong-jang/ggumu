package com.jjh.ggumu.domain.auth.service;

import com.jjh.ggumu.domain.auth.dto.TokenResponse;
import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import com.jjh.ggumu.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public TokenResponse reissue(String refreshToken) {
        if (!jwtProvider.isValid(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        UUID userId = jwtProvider.extractUserId(refreshToken);
        String stored = redisTemplate.opsForValue().get("refresh:" + userId);

        if (!refreshToken.equals(stored)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtProvider.generateAccessToken(userId, user.getRole().name());
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                newRefreshToken,
                Duration.ofMillis(refreshTokenExpiration)
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String accessToken) {
        UUID userId = jwtProvider.extractUserId(accessToken);
        redisTemplate.delete("refresh:" + userId);
    }
}
