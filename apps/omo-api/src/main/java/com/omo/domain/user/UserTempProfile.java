package com.omo.domain.user;

import com.omo.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_temp_profile")
public class UserTempProfile extends BaseEntity {

    private static final double OFFSET_STEP = 0.3;
    private static final double EARLY_LEARNING_MULTIPLIER = 0.5;
    private static final int EARLY_LEARNING_THRESHOLD = 5;
    private static final double OFFSET_MIN = -3.0;
    private static final double OFFSET_MAX = 3.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "temp_offset", nullable = false)
    private double tempOffset;

    @Column(name = "feedback_count", nullable = false)
    private int feedbackCount;

    protected UserTempProfile() {}

    public static UserTempProfile init(User user) {
        return of(user, 0.0, 0);
    }

    // 테스트 및 복원 시 사용
    public static UserTempProfile of(User user, double tempOffset, int feedbackCount) {
        UserTempProfile profile = new UserTempProfile();
        profile.user = user;
        profile.tempOffset = tempOffset;
        profile.feedbackCount = feedbackCount;
        return profile;
    }

    /**
     * 피드백을 체감 보정값에 반영한다.
     * INDOOR는 데이터 오염 방지를 위해 무시하고, MODERATE는 변화 없음.
     * 초기 5회 미만 피드백에서는 성급한 학습을 막기 위해 변화량을 절반으로 줄인다.
     */
    public void applyFeedback(FeedbackType type) {
        if (type == FeedbackType.INDOOR || type == FeedbackType.MODERATE) {
            return;
        }

        double delta = feedbackCount < EARLY_LEARNING_THRESHOLD
                ? OFFSET_STEP * EARLY_LEARNING_MULTIPLIER
                : OFFSET_STEP;

        if (type == FeedbackType.HOT) {
            this.tempOffset = Math.min(OFFSET_MAX, this.tempOffset + delta);
        } else {
            this.tempOffset = Math.max(OFFSET_MIN, this.tempOffset - delta);
        }

        this.feedbackCount++;
    }

    public double getTempOffset() { return tempOffset; }
    public int getFeedbackCount() { return feedbackCount; }
    public User getUser() { return user; }
}
