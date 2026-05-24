package org.springframework.data.util;

/**
 * Compatibility stub for Spring Data 4.x migration.
 * TypeInformation moved to org.springframework.data.core.TypeInformation in Spring Data 4.x.
 * springdoc-openapi 2.x still references the old package; this stub allows class loading to succeed.
 */
@SuppressWarnings("all")
public interface TypeInformation<S> {
    TypeInformation<?> getRequiredActualType();
    Class<S> getType();
}
