package artoria.mock;

import com.github.jsonzou.jmockdata.DataConfig;
import com.github.jsonzou.jmockdata.MockConfig;
import com.github.jsonzou.jmockdata.mocker.BaseMocker;

import java.lang.reflect.Type;

public class JMockDataProvider implements MockProvider {

    @Override
    public Object mock(Type type, Object... arguments) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.init(type);
        DataConfig config = mockConfig.globalDataConfig();
        //noinspection rawtypes
        return new BaseMocker(type).mock(config);
    }

}
