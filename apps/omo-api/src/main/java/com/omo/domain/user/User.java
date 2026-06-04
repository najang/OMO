package com.omo.domain.user;

import com.omo.domain.BaseEntity;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.regex.Pattern;

@Entity
@Table(name = "user", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_provider", columnNames = {"provider", "provider_id"})
})
public class User extends BaseEntity {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z]{2,6}$");

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    protected User() {}

    public User(String email, String nickname, SocialProvider provider, String providerId) {
        if (email == null || email.isBlank()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "이메일은 비어있을 수 없습니다.");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "닉네임은 비어있을 수 없습니다.");
        }
        if (provider == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "소셜 프로바이더는 필수입니다.");
        }
        if (providerId == null || providerId.isBlank()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "프로바이더 ID는 비어있을 수 없습니다.");
        }

        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void completeOnboarding(String nickname) {
        if (nickname == null || !NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "닉네임은 한글 또는 영어 2~6자여야 합니다.");
        }
        this.nickname = nickname;
        this.onboardingCompleted = true;
    }

    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public SocialProvider getProvider() { return provider; }
    public String getProviderId() { return providerId; }
    public boolean isOnboardingCompleted() { return onboardingCompleted; }
}
