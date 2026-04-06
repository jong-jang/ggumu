package com.jjh.ggumu.domain.ai.service;

import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@Disabled("AI API 콘텐츠 필터링 이슈로 임시 비활성화")
@ExtendWith(MockitoExtension.class)
class AiRoutineServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    @InjectMocks
    private AiRoutineService aiRoutineService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.ofKakao("kakao-123", "테스터", "http://img.com/photo.jpg");
        ReflectionTestUtils.setField(user, "id", userId);
    }

    @Test
    void recommend_설문결과있는사용자_루틴목록반환() {
        ReflectionTestUtils.setField(user, "surveyResult", "{\"wakeUpTime\":\"6am\",\"goal\":\"건강\"}");
        String claudeResponse = """
                {"content":[{"type":"text","text":"[\\"물 한 잔 마시기\\",\\"5분 스트레칭\\",\\"명상\\",\\"일기 쓰기\\",\\"산책\\"]"}]}
                """;

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        doReturn(claudeResponse).when(aiRoutineService).callClaudeApi(anyString());

        List<String> result = aiRoutineService.recommend(userId);

        assertThat(result).hasSize(5);
        assertThat(result.get(0)).isEqualTo("물 한 잔 마시기");
    }

    @Test
    void recommend_설문결과없는사용자_기본루틴반환() {
        String claudeResponse = """
                {"content":[{"type":"text","text":"[\\"물 마시기\\",\\"스트레칭\\",\\"명상\\",\\"독서\\",\\"산책\\"]"}]}
                """;

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        doReturn(claudeResponse).when(aiRoutineService).callClaudeApi(anyString());

        List<String> result = aiRoutineService.recommend(userId);

        assertThat(result).hasSize(5);
    }

    @Test
    void recommend_존재하지않는사용자_예외() {
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> aiRoutineService.recommend(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    void recommend_응답에JSON배열없음_빈목록반환() {
        String claudeResponse = """
                {"content":[{"type":"text","text":"추천 루틴을 생성할 수 없습니다."}]}
                """;

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        doReturn(claudeResponse).when(aiRoutineService).callClaudeApi(anyString());

        List<String> result = aiRoutineService.recommend(userId);

        assertThat(result).isEmpty();
    }
}
