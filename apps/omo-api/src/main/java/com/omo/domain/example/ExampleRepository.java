package com.omo.domain.example;

import java.util.Optional;

public interface ExampleRepository {
    Optional<ExampleModel> find(Long id);
}
