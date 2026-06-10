package com.omo.domain.user;

import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTempProfileRepository userTempProfileRepository;

    @InjectMocks
    private UserService userService;

    @DisplayName("ID로 유저를 조회할 때,")
    @Nested
    class GetUser {

        @DisplayName("존재하는 ID면, UserRepository에서 조회한 유저를 반환한다.")
        @Test
        void returnsUser_whenUserExists() {
            // arrange
            User user = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-123");
            when(userRepository.find(1L)).thenReturn(Optional.of(user));

            // act
            User result = userService.getUser(1L);

            // assert
            assertThat(result).isSameAs(user);
            verify(userRepository).find(1L);
        }

        @DisplayName("존재하지 않는 ID면, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwsNotFound_whenUserDoesNotExist() {
            // arrange
            when(userRepository.find(999L)).thenReturn(Optional.empty());

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                userService.getUser(999L)
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.USER_NOT_FOUND);
        }
    }

    @DisplayName("소셜 로그인으로 유저를 조회/생성할 때,")
    @Nested
    class GetOrCreateUser {

        @DisplayName("이미 가입된 유저면, 기존 유저를 반환하고 save를 호출하지 않는다.")
        @Test
        void returnsExistingUser_withoutSave_whenAlreadyRegistered() {
            // arrange
            User existing = new User("test@omo.com", "기존유저", SocialProvider.GOOGLE, "uid-123");
            when(userRepository.findByProviderInfo(SocialProvider.GOOGLE, "uid-123"))
                .thenReturn(Optional.of(existing));

            // act
            User result = userService.getOrCreateUser("test@omo.com", "닉네임변경시도", SocialProvider.GOOGLE, "uid-123");

            // assert
            assertThat(result).isSameAs(existing);
            verify(userRepository, never()).save(any());
        }

        @DisplayName("처음 로그인하는 유저면, save를 호출하고 생성된 유저를 반환한다.")
        @Test
        void savesAndReturnsNewUser_whenFirstLogin() {
            // arrange
            User newUser = new User("new@omo.com", "신규유저", SocialProvider.APPLE, "apple-uid-001");
            when(userRepository.findByProviderInfo(SocialProvider.APPLE, "apple-uid-001"))
                .thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            // act
            User result = userService.getOrCreateUser("new@omo.com", "신규유저", SocialProvider.APPLE, "apple-uid-001");

            // assert
            assertThat(result).isSameAs(newUser);
            verify(userRepository).save(any(User.class));
        }
    }

    @DisplayName("체감 온도 프로필을 초기화할 때,")
    @Nested
    class InitTempProfile {

        @DisplayName("프로필이 없으면, 새로 저장한다.")
        @Test
        void savesNewProfile_whenNotExists() {
            // arrange
            User user = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-1");
            when(userTempProfileRepository.findByUser(user)).thenReturn(Optional.empty());

            // act
            userService.initTempProfile(user, -1.0);

            // assert
            verify(userTempProfileRepository).save(any(UserTempProfile.class));
        }

        @DisplayName("프로필이 이미 있으면, save를 호출하지 않는다.")
        @Test
        void doesNotSave_whenProfileAlreadyExists() {
            // arrange
            User user = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-1");
            UserTempProfile existing = UserTempProfile.of(user, -1.0, 0);
            when(userTempProfileRepository.findByUser(user)).thenReturn(Optional.of(existing));

            // act
            userService.initTempProfile(user, 2.0);

            // assert
            verify(userTempProfileRepository, never()).save(any());
        }
    }
}
