package com.jjh.ggumu.domain.routine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjh.ggumu.domain.routine.dto.RoutineCreateRequest;
import com.jjh.ggumu.domain.routine.dto.RoutineItemResponse;
import com.jjh.ggumu.domain.routine.dto.RoutineResponse;
import com.jjh.ggumu.domain.routine.dto.RoutineUpdateRequest;
import com.jjh.ggumu.domain.follow.service.FollowService;
import com.jjh.ggumu.domain.routine.service.RoutineService;
import com.jjh.ggumu.global.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = RoutineController.class,
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class
        )
)
class RoutineControllerTest {

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
    private RoutineService routineService;

    @MockitoBean
    private FollowService followService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private UUID userId;
    private RoutineResponse sampleResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        sampleResponse = new RoutineResponse(
                UUID.randomUUID(), "아침 루틴", "설명", true, 0, 0,
                List.of(new RoutineItemResponse(UUID.randomUUID(), "물 마시기", 1)),
                LocalDateTime.now()
        );
    }

    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken(
                userId.toString(), null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void POST_루틴생성_성공() throws Exception {
        RoutineCreateRequest request = new RoutineCreateRequest("아침 루틴", "설명", true, List.of("물 마시기"));

        given(routineService.create(eq(userId), any())).willReturn(sampleResponse);

        mockMvc.perform(post("/api/routines")
                        .with(authentication(auth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("아침 루틴"))
                .andExpect(jsonPath("$.data.items[0].title").value("물 마시기"));
    }

    @Test
    void POST_루틴생성_제목없음_400() throws Exception {
        RoutineCreateRequest request = new RoutineCreateRequest("", "설명", false, List.of());

        mockMvc.perform(post("/api/routines")
                        .with(authentication(auth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GET_내루틴목록_성공() throws Exception {
        given(routineService.getMyRoutines(userId)).willReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/routines/me")
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("아침 루틴"));
    }

    @Test
    void GET_루틴단건조회_성공() throws Exception {
        UUID routineId = sampleResponse.id();

        given(routineService.getRoutine(routineId)).willReturn(sampleResponse);

        mockMvc.perform(get("/api/routines/{routineId}", routineId)
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(routineId.toString()));
    }

    @Test
    void PUT_루틴수정_성공() throws Exception {
        UUID routineId = sampleResponse.id();
        RoutineUpdateRequest request = new RoutineUpdateRequest("수정된 제목", "수정된 설명", false);
        RoutineResponse updated = new RoutineResponse(
                routineId, "수정된 제목", "수정된 설명", false, 0, 0, List.of(), LocalDateTime.now()
        );

        given(routineService.update(eq(userId), eq(routineId), any())).willReturn(updated);

        mockMvc.perform(put("/api/routines/{routineId}", routineId)
                        .with(authentication(auth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("수정된 제목"));
    }

    @Test
    void DELETE_루틴삭제_성공() throws Exception {
        UUID routineId = sampleResponse.id();

        mockMvc.perform(delete("/api/routines/{routineId}", routineId)
                        .with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void 인증없이요청_401() throws Exception {
        mockMvc.perform(get("/api/routines/me"))
                .andExpect(status().isUnauthorized());
    }
}
