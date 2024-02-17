/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.polyglot.support;

import kunlun.data.Dict;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import kunlun.polyglot.PolyglotUtils;
import kunlun.util.ObjectUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The polyglot execution tools Test (Graal.JS).
 * @author Kahle
 * @see org.graalvm.polyglot.Context
 * @see org.graalvm.polyglot.Value
 */
public class GraalJavaScriptTest {
    private static final Logger log = LoggerFactory.getLogger(GraalJavaScriptTest.class);
    private final String name = "js";
    static { PolyglotUtils.setPolyglotProvider(new GraalPolyglotProvider()); }

    @Test
    public void test1() {
        String script = "a = 2; b = 3;\n" +
                "var c = a + b;\n" +
                "c;";
        Dict data = Dict.of("a", 1).set("b", 2);
        Object result = PolyglotUtils.eval(name, script, data);
        log.info("result: {}", result);
        // Assert.
        assertTrue("a = 2; b = 3; a + b should be 5", ObjectUtils.equals(result, 5));
        assertSame("data must not updated", 1, data.get("a"));
        assertSame("data must not updated", 2, data.get("b"));
    }

    @Test
    public void test2() {
        String script = "a.get(\"b\");";
        Dict data = Dict.of("a", Dict.of("b", 4));
        Object result = PolyglotUtils.eval(name, script, data);
        log.info("result: {}", result);
        assertTrue(ObjectUtils.equals(result, 4));
    }

    @Test
    public void test3() {
        // https://www.graalvm.org/latest/reference-manual/js/JavaInteroperability/
        String script = "var System = Java.type(\"java.lang.System\");\n" +
                "var time = System.currentTimeMillis();\n" +
                "time;";
        Dict data = Dict.of();
        Object result = PolyglotUtils.eval(name, script, data);
        log.info("result: {}", result);
        assertNotNull(result);
    }

    @Test
    public void test4() {
        Dict dict = Dict.of();
        dict.set("e", 5);
        String script; Object result;

        script = "var a = 1; var b = 2; a + b + e;";
        result = PolyglotUtils.eval(name, script, dict);
        log.info("result: {}", result);
        assertTrue(ObjectUtils.equals(result, 8));

        script = "var c = 3; var d = 4; c + d + e;";
        result = PolyglotUtils.eval(name, script, dict);
        log.info("result: {}", result);
        assertTrue(ObjectUtils.equals(result, 12));
    }

    @Test
    public void test5() {
        String script = "function test(arg1, arg2, arg3) {\n" +
                "return arg1 + arg2 + arg3;\n" +
                "}\n";
        Object result = PolyglotUtils.invoke(name, script, "test", 1, 2, 3);
        log.info("result: {}", result);
        assertTrue(ObjectUtils.equals(result, 6));
    }

    @Test
    public void test6() {
        String script = "function test(arg1, arg2, arg3) {\n" +
                "return arg1 + arg2 + arg3;\n" +
                "}\n";
        script += "function test1(arg1, arg2) {\n" +
                "return arg1 * arg2;\n" +
                "}\n";
        script += "var a = 1, b = 2, c = 3;\n" +
                "test(a, b, c) + test1(b, c);";
        Dict data = Dict.of();
        Object result = PolyglotUtils.eval(name, script, data);
        log.info("result: {}", result);
        assertTrue(ObjectUtils.equals(result, 12));
    }

    @Test
    public void test7() {
        String script = "var LinkedHashMap = Java.type(\"java.util.LinkedHashMap\");" +
                "var data = new LinkedHashMap({a:1,\n" +
                "b:2,\n" +
                "c:3});\n" +
                "data;";
        Dict data = Dict.of();
        Object result = PolyglotUtils.eval(name, script, data);
        log.info("result: {}", result);
    }

}
