package artoria.renderer.support;

import artoria.data.Dict;
import artoria.data.tuple.PairImpl;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.renderer.RenderUtils;
import org.junit.Test;

import static artoria.common.Constants.UTF_8;

public class BeetlTextRendererTest {
    private static final Logger log = LoggerFactory.getLogger(BeetlTextRendererTest.class);
    private static final String rendererName = "beetl";
    private static final Dict data = Dict.of();
    private String source = "This is test string \"${testStr}\", \nTest string is \"${testStr}\". ";
    private String source1 = "You name is \"${data.name}\", \nAnd you age is \"${data.age}\". ";

    static {
        RenderUtils.registerRenderer(rendererName, new BeetlTextRenderer());
        data.set("testStr", "hello, world! ").set("nullVal", null);
        data.set("data", Dict.of("name", "zhangsan").set("age", "19"));
    }

    @Test
    public void test1() {
        log.info(RenderUtils.renderToString(rendererName, source, data));
        log.info(RenderUtils.renderToString(rendererName, source1, data));
        log.info(RenderUtils.renderToString(rendererName,
                new PairImpl<String, String>("classpath://testBeetl.btl", UTF_8), data));
    }

}
