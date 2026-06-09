package com.omo.domain.user;

import java.util.Optional;

public interface UserTempProfileRepository {
    Optional<UserTempProfile> findByUser(User user);
    UserTempProfile save(UserTempProfile profile);
}
