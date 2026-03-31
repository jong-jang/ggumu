package com.jjh.ggumu.domain.follow.controller;

import com.jjh.ggumu.domain.follow.dto.FollowUserResponse;
import com.jjh.ggumu.domain.follow.service.FollowService;
import com.jjh.ggumu.global.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = FollowController.class,
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class
        )
)
class FollowControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                    .exceptionHandling(e -> e
                            .authenticationEntryPoint((req, res, ex) -> res.setStatus(401)));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FollowService followService;

    private UUID userId;
    private UUID targetUserId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
    }

    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken(
                userId.toString(), null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void POST_팔로우_성공() throws Exception {
        mockMvc.perform(post("/api/follows/{targetUserId}", targetUserId)
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(followService).follow(userId, targetUserId);
    }

    @Test
    void DELETE_언팔로우_성공() throws Exception {
        mockMvc.perform(delete("/api/follows/{targetUserId}", targetUserId)
                        .with(authentication(auth())))
                .andExpect(status().isOk());

        verify(followService).unfollow(userId, targetUserId);
    }

    @Test
    void GET_팔로워목록_성공() throws Exception {
        FollowUserResponse response = new FollowUserResponse(UUID.randomUUID(), "팔로워", "http://img.com/a.jpg");
        given(followService.getFollowers(userId)).willReturn(List.of(response));

        mockMvc.perform(get("/api/follows/followers")
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nickname").value("팔로워"));
    }

    @Test
    void GET_팔로잉목록_성공() throws Exception {
        FollowUserResponse response = new FollowUserResponse(UUID.randomUUID(), "팔로잉", "http://img.com/b.jpg");
        given(followService.getFollowings(userId)).willReturn(List.of(response));

        mockMvc.perform(get("/api/follows/followings")
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nickname").value("팔로잉"));
    }

    @Test
    void 인증없이요청_401() throws Exception {
        mockMvc.perform(get("/api/follows/followers"))
                .andExpect(status().isUnauthorized());
    }
}
