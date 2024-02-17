/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util;

import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Test;

public class PinyinUtilsTest {
    private static Logger log = LoggerFactory.getLogger(PinyinUtilsTest.class);

    @Test
    public void test1() {
        log.info("{}", PinyinUtils.convertChineseToPinyin("世界，你好！", false));
        log.info("{}", PinyinUtils.convertChineseToPinyin("世界，你好！", true));
    }

}
