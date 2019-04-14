/**
 * Copyright (C) 2019 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hotels.beans.utils;

import static java.util.stream.Collectors.joining;

import static javax.validation.Validation.buildDefaultValidatorFactory;

import static org.apache.commons.lang3.StringUtils.SPACE;

import static com.hotels.beans.cache.CacheManagerFactory.getCacheManager;
import static com.hotels.beans.constant.Punctuation.DOT;
import static com.hotels.beans.constant.Punctuation.SEMICOLON;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.hotels.beans.cache.CacheManager;
import com.hotels.beans.error.InvalidBeanException;

/**
 * Validation utils for Class objects.
 */
public class ValidationUtils {
    /**
     * Cache key for class: {@link ValidationUtils}.
     */
    private static final int VALIDATION_UTILS_CACHE_KEY = 3;

    /**
     * Bean validator cache key.
     */
    private static final String BEAN_VALIDATOR_KEY = "BeanValidator";

    /**
     * CacheManager class {@link CacheManager}.
     */
    private final CacheManager cacheManager;

    /**
     * Default constructor.
     */
    public ValidationUtils() {
        this.cacheManager = getCacheManager(VALIDATION_UTILS_CACHE_KEY);
    }

    /**
     * Validate that the specified argument is not {@code null};
     * otherwise throws an {@link IllegalArgumentException} with the specified message.
     * @param object  the object to check
     * @param <T> the object type
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> void notNull(final T object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Validate that the specified argument is not {@code null};
     * otherwise throws an {@link IllegalArgumentException} with the specified message.
     * @param object  the object to check
     * @param message  the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param <T> the object type
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> void notNull(final T object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks if an object is valid.
     * @param k the object to check
     * @param <K> the object class
     * @throws InvalidBeanException {@link InvalidBeanException} if the validation fails
     */
    public final <K> void validate(final K k) {
        final Set<ConstraintViolation<Object>> constraintViolations = getValidator().validate(k);
        if (!constraintViolations.isEmpty()) {
            final String errors = constraintViolations.stream()
                    .map(cv -> cv.getRootBeanClass().getName()
                            + DOT.getSymbol()
                            + cv.getPropertyPath()
                            + SPACE
                            + cv.getMessage())
                    .collect(joining(SEMICOLON.getSymbol()));
            throw new InvalidBeanException(errors);
        }
    }

    /**
     * Creates the validator.
     * @return a {@link Validator} instance.
     */
    private Validator getValidator() {
        return cacheManager.getFromCache(BEAN_VALIDATOR_KEY, Validator.class)
                .orElseGet(() -> {
                    Validator validator = buildDefaultValidatorFactory().getValidator();
                    cacheManager.cacheObject(BEAN_VALIDATOR_KEY, validator);
                    return validator;
                });
    }
}
