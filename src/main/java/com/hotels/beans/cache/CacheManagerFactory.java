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

package com.hotels.beans.cache;

import static com.github.benmanes.caffeine.cache.Caffeine.newBuilder;

import static lombok.AccessLevel.PRIVATE;

import com.github.benmanes.caffeine.cache.Cache;

import lombok.NoArgsConstructor;

/**
 * Creates a {@link CacheManager} instance for the given class.
 */
@NoArgsConstructor(access = PRIVATE)
public final class CacheManagerFactory {
    /**
     * Initial capacity for cache manager.
     */
    private static final int INITIAL_CAPACITY = 5;

    /**
     * Cache store.
     */
    private static final Cache<Integer, CacheManager> CACHE_MAP = newBuilder().initialCapacity(INITIAL_CAPACITY).build();

    /**
     * Creates a new {@link CacheManager} instance.
     * @param cacheName the cache name
     * @return a cache manager instance
     */
    public static CacheManager getCacheManager(final int cacheName) {
        return CACHE_MAP.get(cacheName, k -> new CacheManager(newBuilder().build()));
    }
}
