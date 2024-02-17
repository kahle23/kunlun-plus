/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.mock.support;

import java.util.Map;

public class ClassMockerConfig {
    private Map<String, Mocker> mockerMap;
    private Class<?> type;

    public Map<String, Mocker> getMockerMap() {
        return mockerMap;
    }

    public void setMockerMap(Map<String, Mocker> mockerMap) {
        this.mockerMap = mockerMap;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

}
