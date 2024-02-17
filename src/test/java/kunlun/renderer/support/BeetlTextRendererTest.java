/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.renderer.support;

import kunlun.data.Dict;
import kunlun.data.tuple.PairImpl;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import kunlun.renderer.RenderUtils;
import org.junit.Test;

import static kunlun.common.constant.Charsets.STR_UTF_8;

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
                new PairImpl<String, String>("classpath://testBeetl.btl", STR_UTF_8), data));
    }

}
