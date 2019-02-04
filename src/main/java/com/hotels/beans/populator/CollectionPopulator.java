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

package com.hotels.beans.populator;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.Collection;

import com.hotels.beans.transformer.Transformer;

/**
 * Populator for Collections types object {@link java.util.Collections}.
 * @param <K> the class of the collection elements.
 */
class CollectionPopulator<K> extends Populator<Collection> implements ICollectionPopulator<Collection> {

    /**
     * Default constructor.
     * @param beanTransformer the bean transformer containing the field name mapping and transformation functions
     */
    CollectionPopulator(final Transformer beanTransformer) {
        super(beanTransformer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<K> getPopulatedObject(final Field field, final Collection fieldValue) {
        final Class<?> genericClass = getReflectionUtils().getArgumentTypeClass(fieldValue, field.getDeclaringClass().getName(), field.getName(), true);
        return getPopulatedObject(getReflectionUtils().getGenericFieldType(field), fieldValue, genericClass);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<K> getPopulatedObject(final Class<?> genericFieldType, final Object fieldValue, final Class<?> nestedGenericClass) {
        final Collection res;
        if (getClassUtils().isPrimitiveOrSpecialType(isNull(nestedGenericClass) ? genericFieldType : nestedGenericClass)) {
            res = (Collection) fieldValue;
        } else {
            res = ((Collection<K>) fieldValue)
                    .parallelStream()
                    .map(elem -> transform(elem, (Class<K>) genericFieldType))
                    .collect(toList());
        }
        return res;
    }
}