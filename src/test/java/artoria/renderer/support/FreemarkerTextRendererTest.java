package artoria.renderer.support;

import artoria.data.Dict;
import artoria.data.PairImpl;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.renderer.RenderUtils;
import org.junit.Test;

import static artoria.common.Constants.UTF_8;

public class FreemarkerTextRendererTest {
    private static final Logger log = LoggerFactory.getLogger(FreemarkerTextRendererTest.class);
    private static final String rendererName = "freemarker";
    private static final Dict data = Dict.of();
    private String source = "This is test string \"${testStr}\", \nTest string is \"${testStr}\". ";
    private String source1 = "You name is \"${data.name}\", \nAnd you age is \"${data.age}\". ";

    static {
        RenderUtils.registerRenderer(rendererName, new FreemarkerTextRenderer());
        data.set("testStr", "hello, world! ").set("nullVal", null);
        data.set("data", Dict.of("name", "zhangsan").set("age", "19"));
    }

    @Test
    public void test1() {
        log.info(RenderUtils.renderToString(rendererName, source, data));
        log.info(RenderUtils.renderToString(rendererName, source1, data));
        log.info(RenderUtils.renderToString(rendererName,
                new PairImpl<String, String>("testFreemarker.ftl", UTF_8), data));
    }

}
