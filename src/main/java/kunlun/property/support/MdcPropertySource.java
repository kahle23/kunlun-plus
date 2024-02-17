/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.property.support;

import kunlun.property.AbstractPropertySource;
import kunlun.util.ObjectUtils;
import org.slf4j.MDC;

import java.util.Map;

@Deprecated // TODO: Deletable
public class MdcPropertySource extends AbstractPropertySource {

    public MdcPropertySource(String name) {

        super(name);
    }

    @Override
    public Map<String, Object> getProperties() {

        return ObjectUtils.cast(MDC.getCopyOfContextMap());
    }

    @Override
    public Object setProperty(String name, Object value) {
        MDC.put(name, String.valueOf(value));
        return null;
    }

    @Override
    public Object getProperty(String name) {

        return MDC.get(name);
    }

    @Override
    public Object removeProperty(String name) {
        MDC.remove(name);
        return null;
    }

}
