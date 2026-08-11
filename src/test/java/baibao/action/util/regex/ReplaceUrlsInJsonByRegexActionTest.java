package baibao.action.util.regex;

import kunlun.common.constant.Nil;
import kunlun.core.function.Function;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static kunlun.common.constant.Numbers.ONE;

public class ReplaceUrlsInJsonByRegexActionTest {
    private final ReplaceUrlsInJsonByRegexAction action = new ReplaceUrlsInJsonByRegexAction();
    private final List<String> urlPrefixes = Arrays.asList("https://www.test.com", "https://t1.test.com");
    private final Function<String, String> urlProcessor = new Function<String, String>() {
        @Override
        public String apply(String str) {
            return str + "???test=test";
        }
    };

    private void doTest(String data, Integer layer) {
        System.out.println(data);
        data = (String) action.execute(Nil.STR, data, new Object[]{ urlPrefixes, layer, urlProcessor});
        System.out.println(data);
    }

    @Test
    public void test1() {
        String data = "[\"https://www.test.com/d/2026/01/30/17/41/2601301140006001.jpg\"]";
        doTest(data, Nil.INT);
    }

    @Test
    public void test2() {
        String data = "[\"test\":\"https://t1.test.com/d/2026/01/30/17/41/2601301140006001.jpg\"]";
        doTest(data, Nil.INT);
    }

    @Test
    public void test3() {
        String data = "\"https://www.test.com/d/2026/01/30/17/41/2601301140006001.jpg\"";
        doTest(data, Nil.INT);
        data = "https://www.test.com/d/2026/01/30/17/41/2601301140006001.jpg";
        doTest(data, Nil.INT);
    }

    @Test
    public void test4() {
        String data = "aaa,bbb,ccc\\\"https://www.test.com/d/2026/01/30/17/41/2601301140006001.jpg\\\"aaa,bbb,ccc\\\\\"https://t1.test.com/d/2026/01/30/17/41/2601301140006001.jpg\\\\\"eee,,fff,,ddd\"https://www.test.com/d/2026/01/30/17/41/2601301140006001.jpg\"abcdefg,,";
        doTest(data, Nil.INT);
        data = "aaa,bbb,ccc\\\"https://www.test.com/d/2026/01/30/17/41/2601301140006001.jpg\\\"aaa,bbb,ccc\\\"https://t1.test.com/d/2026/01/30/17/41/2601301140006001.jpg\\\"eee,,fff,,ddd\\\"https://www.test.com/d/2026/01/30/17/41/2601301140006001.jpg\\\"abcdefg,,";
        doTest(data, ONE);
    }

}
