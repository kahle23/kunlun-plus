/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.reflect.support;

import kunlun.aop.AbstractInterceptor;
import kunlun.aop.ProxyUtils;
import kunlun.spring.config.data.bean.BeanToolsAutoConfiguration;
import kunlun.reflect.ReflectService;
import kunlun.reflect.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.WEAK;

/**
 * Reflect auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureAfter(BeanToolsAutoConfiguration.class)
public class ReflectAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ReflectAutoConfiguration.class);
    private static final List<String> METHOD_NAMES;

    static {
        // TODO: Do cache optimize by CacheUtils
        List<String> list = new ArrayList<String>();
        Collections.addAll(list, "findConstructors"
                , "findConstructor"
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
        ReflectService reflectService = ReflectUtils.getReflectService();
        if (reflectService != null) {
            ReflectService instance = ProxyUtils.proxy(new ReflectProviderInterceptor(reflectService));
            ReflectUtils.setReflectService(instance);
            log.info("Add cache to reflect provider success. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

    private static class ReflectProviderInterceptor extends AbstractInterceptor<ReflectService> {
        private final Map<String, Object> cache;

        public ReflectProviderInterceptor(ReflectService originalObject) {
            super(originalObject);
            this.cache = new ConcurrentReferenceHashMap<String, Object>(64, WEAK);
        }


        @Override
        public Object intercept(Object proxyObject, Method method, Object[] args) throws Throwable {
            if (METHOD_NAMES.contains(method.getName())) {
                String key = method.getName() + Arrays.toString(args);
                Object val = this.cache.get(key);
                if (val == null) {
                    val = method.invoke(getOriginalObject(), args);
                    this.cache.put(key, val);
                }
                return val;
            }
            else {
                return method.invoke(getOriginalObject(), args);
            }
        }

    }

}
