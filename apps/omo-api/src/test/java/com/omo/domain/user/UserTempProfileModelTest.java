package com.omo.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class UserTempProfileModelTest {

    @DisplayName("UserTempProfile을 생성할 때,")
    @Nested
    class Create {

        @DisplayName("User가 주어지면, tempOffset=0, feedbackCount=0으로 초기화된다.")
        @Test
        void initializesWithDefaults_whenUserIsProvided() {
            // arrange
            User user = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-123");

            // act
            UserTempProfile profile = UserTempProfile.init(user);

            // assert
            assertThat(profile.getTempOffset()).isEqualTo(0.0);
            assertThat(profile.getFeedbackCount()).isEqualTo(0);
        }
    }

    @DisplayName("피드백을 적용할 때,")
    @Nested
    class ApplyFeedback {

        @DisplayName("HOT 피드백이면, tempOffset이 +0.3 증가한다.")
        @Test
        void increasesTempOffset_whenFeedbackIsHot() {
            // arrange
            UserTempProfile profile = profileWithOffset(0.0, 10);

            // act
            profile.applyFeedback(FeedbackType.HOT);

            // assert
            assertThat(profile.getTempOffset()).isCloseTo(0.3, within(0.001));
        }

        @DisplayName("COLD 피드백이면, tempOffset이 -0.3 감소한다.")
        @Test
        void decreasesTempOffset_whenFeedbackIsCold() {
            // arrange
            UserTempProfile profile = profileWithOffset(0.0, 10);

            // act
            profile.applyFeedback(FeedbackType.COLD);

            // assert
            assertThat(profile.getTempOffset()).isCloseTo(-0.3, within(0.001));
        }

        @DisplayName("MODERATE 피드백이면, tempOffset이 변하지 않는다.")
        @Test
        void doesNotChangeTempOffset_whenFeedbackIsModerate() {
            // arrange
            UserTempProfile profile = profileWithOffset(1.5, 10);

            // act
            profile.applyFeedback(FeedbackType.MODERATE);

            // assert
            assertThat(profile.getTempOffset()).isCloseTo(1.5, within(0.001));
        }

        @DisplayName("INDOOR 피드백이면, tempOffset이 변하지 않는다.")
        @Test
        void doesNotChangeTempOffset_whenFeedbackIsIndoor() {
            // arrange
            UserTempProfile profile = profileWithOffset(-1.0, 10);

            // act
            profile.applyFeedback(FeedbackType.INDOOR);

            // assert
            assertThat(profile.getTempOffset()).isCloseTo(-1.0, within(0.001));
        }

        @DisplayName("누적 피드백이 5개 미만이면, 변화량이 절반(0.15)으로 줄어든다.")
        @Test
        void appliesHalfWeight_whenFeedbackCountIsUnderThreshold() {
            // arrange
            UserTempProfile profile = profileWithOffset(0.0, 4); // feedbackCount < 5

            // act
            profile.applyFeedback(FeedbackType.HOT);

            // assert
            assertThat(profile.getTempOffset()).isCloseTo(0.15, within(0.001));
        }

        @DisplayName("tempOffset이 +3.0을 초과하지 않는다 (상한 clamp).")
        @Test
        void clampsAtMaxOffset_whenExceedsUpperBound() {
            // arrange
            UserTempProfile profile = profileWithOffset(2.9, 10);

            // act
            profile.applyFeedback(FeedbackType.HOT);

            // assert
            assertThat(profile.getTempOffset()).isCloseTo(3.0, within(0.001));
        }

        @DisplayName("tempOffset이 -3.0 미만으로 내려가지 않는다 (하한 clamp).")
        @Test
        void clampsAtMinOffset_whenExceedsLowerBound() {
            // arrange
            UserTempProfile profile = profileWithOffset(-2.9, 10);

            // act
            profile.applyFeedback(FeedbackType.COLD);

            // assert
            assertThat(profile.getTempOffset()).isCloseTo(-3.0, within(0.001));
        }

        @DisplayName("INDOOR/MODERATE가 아닌 피드백이면, feedbackCount가 1 증가한다.")
        @Test
        void incrementsFeedbackCount_whenMeaningfulFeedbackIsApplied() {
            // arrange
            UserTempProfile profile = profileWithOffset(0.0, 3);

            // act
            profile.applyFeedback(FeedbackType.HOT);

            // assert
            assertThat(profile.getFeedbackCount()).isEqualTo(4);
        }

        @DisplayName("INDOOR 피드백이면, feedbackCount가 증가하지 않는다.")
        @Test
        void doesNotIncrementFeedbackCount_whenFeedbackIsIndoor() {
            // arrange
            UserTempProfile profile = profileWithOffset(0.0, 3);

            // act
            profile.applyFeedback(FeedbackType.INDOOR);

            // assert
            assertThat(profile.getFeedbackCount()).isEqualTo(3);
        }
    }

    // offset과 feedbackCount를 직접 지정해 UserTempProfile을 만드는 헬퍼
    private UserTempProfile profileWithOffset(double offset, int feedbackCount) {
        User user = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-123");
        return UserTempProfile.of(user, offset, feedbackCount);
    }
}
