package org.springframework.data.util;

/**
 * Compatibility stub for Spring Data 4.x migration.
 * ClassTypeInformation moved to org.springframework.data.core.ClassTypeInformation in Spring Data 4.x.
 * springdoc-openapi 2.x still references the old package; this stub allows class loading to succeed.
 */
@SuppressWarnings("all")
public class ClassTypeInformation<T> implements TypeInformation<T> {

    private final Class<T> type;

    private ClassTypeInformation(Class<T> type) {
        this.type = type;
    }

    public static <T> ClassTypeInformation<T> from(Class<T> type) {
        return new ClassTypeInformation<>(type);
    }

    @Override
    public TypeInformation<?> getRequiredActualType() {
        return this;
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
