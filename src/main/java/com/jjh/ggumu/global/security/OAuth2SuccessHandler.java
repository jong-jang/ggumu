package com.jjh.ggumu.global.security;

import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.service.KakaoUserDetails;
import com.jjh.ggumu.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${oauth2.redirect-url}")
    private String redirectUrl;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        KakaoUserDetails userDetails = (KakaoUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        redisTemplate.opsForValue().set(
                "refresh:" + user.getId(),
                refreshToken,
                Duration.ofMillis(refreshTokenExpiration)
        );

        boolean isNewUser = !user.isOnboardingCompleted();

        String targetUrl = redirectUrl
                + "?access_token=" + accessToken
                + "&refresh_token=" + refreshToken
                + "&is_new_user=" + isNewUser;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
