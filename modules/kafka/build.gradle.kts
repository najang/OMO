plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api("org.springframework.kafka:spring-kafka")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:testcontainers-kafka")

    testFixturesImplementation("org.testcontainers:testcontainers-kafka")
}
