package com.omo.domain.user;

import com.omo.domain.BaseEntity;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    protected User() {}

    public User(String email, String nickname, SocialProvider provider, String providerId) {
        if (email == null || email.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 비어있을 수 없습니다.");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "닉네임은 비어있을 수 없습니다.");
        }
        if (provider == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "소셜 프로바이더는 필수입니다.");
        }
        if (providerId == null || providerId.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "프로바이더 ID는 비어있을 수 없습니다.");
        }

        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void updateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "닉네임은 비어있을 수 없습니다.");
        }
        this.nickname = nickname;
    }

    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public SocialProvider getProvider() { return provider; }
    public String getProviderId() { return providerId; }
}
