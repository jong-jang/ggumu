package com.jjh.ggumu.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    @Column(length = 50)
    private String nickname;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(length = 200)
    private String bio;

    @Column(name = "survey_result", columnDefinition = "JSON")
    private String surveyResult;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static User ofKakao(String providerId, String nickname, String profileImageUrl) {
        User user = new User();
        user.provider = "kakao";
        user.providerId = providerId;
        user.nickname = nickname;
        user.profileImageUrl = profileImageUrl;
        return user;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void completeOnboarding(String surveyResult) {
        this.surveyResult = surveyResult;
        this.onboardingCompleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
