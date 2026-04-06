package com.jjh.ggumu.domain.reward.repository;

import com.jjh.ggumu.domain.reward.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RewardRepository extends JpaRepository<Reward, UUID> {
}
