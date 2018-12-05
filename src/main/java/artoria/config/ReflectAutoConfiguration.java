package artoria.config;

import artoria.aop.Enhancer;
import artoria.aop.Interceptor;
import artoria.reflect.ReflectUtils;
import artoria.reflect.Reflecter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ConcurrentReferenceHashMap;
import artoria.spring.InitializingDisposableBean;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Reflect auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureAfter(CglibAutoConfiguration.class)
public class ReflectAutoConfiguration implements InitializingDisposableBean {
    private static Logger log = LoggerFactory.getLogger(ReflectAutoConfiguration.class);
    private static final List<String> METHOD_NAMES;

    static {
        // TODO: Do cache optimize by cacheManager
        List<String> list = new ArrayList<String>();
        Collections.addAll(list, "forName"
                , "findConstructors", "findConstructor"
                , "findFields", "findDeclaredFields"
                , "findAccessFields", "findField"
                , "findMethods", "findDeclaredMethods"
                , "findAccessMethods", "findReadMethods"
                , "findWriteMethods", "findMethod"
                , "findSimilarMethod");
        METHOD_NAMES = Collections.unmodifiableList(list);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Reflecter reflecter = ReflectUtils.getReflecter();
        if (reflecter != null) {
            ReflecterInterceptor intr = new ReflecterInterceptor(reflecter);
            Reflecter instance = (Reflecter) Enhancer.enhance(reflecter, intr);
            ReflectUtils.setReflecter(instance);
            log.info("Add cache to \"ReflectUtils\" success. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

    private static class ReflecterInterceptor implements Interceptor {
        private Map<String, Object> cache;
        private Reflecter original;

        ReflecterInterceptor(Reflecter original) {
            ConcurrentReferenceHashMap.ReferenceType type =
                    ConcurrentReferenceHashMap.ReferenceType.WEAK;
            this.cache =
                    new ConcurrentReferenceHashMap<String, Object>(64, type);
            this.original = original;
        }

        @Override
        public Object intercept(Object proxyObject, Method method, Object[] args) throws Throwable {
            if (METHOD_NAMES.contains(method.getName())) {
                String key = method.getName() + Arrays.toString(args);
                Object val = this.cache.get(key);
                if (val == null) {
                    val = method.invoke(this.original, args);
                    this.cache.put(key, val);
                }
                return val;
            }
            else {
                return method.invoke(this.original, args);
            }
        }

    }

}
