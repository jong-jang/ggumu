package com.jjh.ggumu.domain.user.service;

import com.jjh.ggumu.domain.user.entity.User;
import com.jjh.ggumu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = String.valueOf(attributes.get("id"));
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        String profileImageUrl = (String) profile.get("profile_image_url");

        User user = userRepository.findByProviderAndProviderId("kakao", providerId)
                .map(existing -> {
                    existing.updateProfile(nickname, profileImageUrl);
                    return userRepository.save(existing);
                })
                .orElseGet(() -> userRepository.save(User.ofKakao(providerId, nickname, profileImageUrl)));

        return new KakaoUserDetails(user, attributes);
    }
}
