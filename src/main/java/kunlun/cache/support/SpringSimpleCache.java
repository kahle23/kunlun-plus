/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support;

import kunlun.data.ReferenceType;
import kunlun.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

import static kunlun.common.constant.Numbers.SIXTEEN;
import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.SOFT;
import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.WEAK;

/**
 * The memory cache simple implement by spring map.
 * @author Kahle
 */
public class SpringSimpleCache extends SimpleCache {

    public SpringSimpleCache(SimpleCacheConfig cacheConfig) {

        super(cacheConfig);
    }

    public SpringSimpleCache() {

        super();
    }

    @Override
    protected Map<Object, ValueWrapper> buildStorage(ReferenceType referenceType) {
        Assert.isTrue(
            ReferenceType.SOFT.equals(referenceType) || ReferenceType.WEAK.equals(referenceType),
            "Parameter \"referenceType\" must be only soft reference or weak reference. "
        );
        if (ReferenceType.SOFT.equals(referenceType)) {
            return new ConcurrentReferenceHashMap<Object, ValueWrapper>(SIXTEEN, SOFT);
        }
        else {
            return new ConcurrentReferenceHashMap<Object, ValueWrapper>(SIXTEEN, WEAK);
        }
    }

}
