package com.jjh.ggumu.domain.ai.controller;

import com.jjh.ggumu.domain.ai.service.AiRoutineService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AiRoutineController.class,
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class
        )
)
class AiRoutineControllerTest {

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
    private AiRoutineService aiRoutineService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken(
                userId.toString(), null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void GET_루틴추천_성공() throws Exception {
        List<String> routines = List.of("물 한 잔 마시기", "5분 스트레칭", "명상", "일기 쓰기", "산책");
        given(aiRoutineService.recommend(userId)).willReturn(routines);

        mockMvc.perform(get("/api/ai/routines/recommend")
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.routines").isArray())
                .andExpect(jsonPath("$.data.routines.length()").value(5))
                .andExpect(jsonPath("$.data.routines[0]").value("물 한 잔 마시기"));
    }

    @Test
    void GET_루틴추천_빈목록반환() throws Exception {
        given(aiRoutineService.recommend(userId)).willReturn(List.of());

        mockMvc.perform(get("/api/ai/routines/recommend")
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.routines").isArray())
                .andExpect(jsonPath("$.data.routines.length()").value(0));
    }

    @Test
    void 인증없이요청_401() throws Exception {
        mockMvc.perform(get("/api/ai/routines/recommend"))
                .andExpect(status().isUnauthorized());
    }
}
