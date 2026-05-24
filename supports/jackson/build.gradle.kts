plugins {
    `java-library`
}

dependencies {
    // spring-boot-jackson provides JsonMapperBuilderCustomizer + spring-context + jackson-databind 3.x
    implementation("org.springframework.boot:spring-boot-jackson")
    // jackson modules
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}
