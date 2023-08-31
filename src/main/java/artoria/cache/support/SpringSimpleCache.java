package artoria.cache.support;

import artoria.data.ReferenceType;
import artoria.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

import static artoria.common.Constants.FIFTY;
import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.SOFT;
import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.WEAK;

public class SpringSimpleCache extends SimpleCache {

    public SpringSimpleCache(String name) {

        super(name);
    }

    public SpringSimpleCache(String name, Object cacheConfig) {

        super(name, cacheConfig);
    }

    @Override
    protected Map<Object, ValueWrapper> buildStorage(ReferenceType referenceType) {
        Assert.isTrue(
            ReferenceType.SOFT.equals(referenceType) || ReferenceType.WEAK.equals(referenceType),
            "Parameter \"referenceType\" must be only soft reference or weak reference. "
        );
        if (ReferenceType.SOFT.equals(referenceType)) {
            return new ConcurrentReferenceHashMap<Object, ValueWrapper>(FIFTY, SOFT);
        }
        else {
            return new ConcurrentReferenceHashMap<Object, ValueWrapper>(FIFTY, WEAK);
        }
    }

}
