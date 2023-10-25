package artoria.data.desensitize.support;

import artoria.data.desensitize.Desensitizer;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class BankCardNumberDesensitizerTest {
    private static final Logger log = LoggerFactory.getLogger(BankCardNumberDesensitizerTest.class);
    private static final Desensitizer bankCardNumberMasker = new BankCardNumberDesensitizer();

    @Test
    public void test1() {
        log.info("{}", bankCardNumberMasker.desensitize("6228482898203884775"));
        log.info("{}", bankCardNumberMasker.desensitize("6228480010594620212"));
        log.info("{}", bankCardNumberMasker.desensitize("9559980210373015416"));
    }

}
