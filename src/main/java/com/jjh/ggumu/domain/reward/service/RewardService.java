package com.jjh.ggumu.domain.reward.service;

import com.jjh.ggumu.domain.reward.entity.Reward;
import com.jjh.ggumu.domain.reward.repository.RewardRepository;
import com.jjh.ggumu.domain.routine.repository.RoutineRepository;
import com.jjh.ggumu.domain.routine.repository.projection.UserScoreProjection;
import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RewardRepository rewardRepository;

    /**
     * 월별 리워드 배분
     * - 사용자 점수 = 좋아요 수 × 3 + 조회수 × 1
     * - 상위 10% 유저에게 구독 수익의 30%를 점수 비례로 배분
     */
    @Transactional
    public void distributeMonthlyRewards(YearMonth yearMonth, int totalSubscriptionRevenue) {
        List<UserScoreProjection> userScores = routineRepository.findUserScores();
        if (userScores.isEmpty()) return;

        int topCount = Math.max(1, (int) Math.ceil(userScores.size() * 0.1));
        List<UserScoreProjection> topUsers = userScores.subList(0, topCount);

        long totalScore = topUsers.stream().mapToLong(UserScoreProjection::getScore).sum();
        if (totalScore == 0) return;

        int rewardPool = (int) (totalSubscriptionRevenue * 0.3);
        String reason = yearMonth + " 월별 리워드";

        for (UserScoreProjection projection : topUsers) {
            int amount = (int) (rewardPool * projection.getScore() / totalScore);
            if (amount <= 0) continue;

            User user = userRepository.findById(projection.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            rewardRepository.save(Reward.of(user, amount, reason));
        }
    }
}
