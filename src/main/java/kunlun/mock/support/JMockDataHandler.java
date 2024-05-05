/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.mock.support;

import com.github.jsonzou.jmockdata.DataConfig;
import com.github.jsonzou.jmockdata.MockConfig;
import com.github.jsonzou.jmockdata.mocker.BaseMocker;
import kunlun.data.mock.MockHandler;

import java.lang.reflect.Type;

public class JMockDataHandler implements MockHandler {

    @Override
    public Object mock(Type type, Object... arguments) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.init(type);
        DataConfig config = mockConfig.globalDataConfig();
        //noinspection rawtypes
        return new BaseMocker(type).mock(config);
    }

}
