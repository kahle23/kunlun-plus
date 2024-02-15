package artoria.codec.support;

import artoria.util.ArrayUtils;

import static artoria.common.constant.Numbers.MINUS_ONE;
import static artoria.common.constant.Numbers.ZERO;

public class Java8Base64 extends Base64 {
    private final java.util.Base64.Encoder encoder;
    private final java.util.Base64.Decoder decoder;

    protected Java8Base64() {

        this(false, false, MINUS_ONE, null);
    }

    protected Java8Base64(boolean urlSafe) {

        this(urlSafe, false, MINUS_ONE, null);
    }

    protected Java8Base64(boolean mime, int lineLength, byte[] lineSeparator) {

        this(false, mime, lineLength, lineSeparator);
    }

    protected Java8Base64(boolean urlSafe, boolean mime, int lineLength, byte[] lineSeparator) {
        super(urlSafe, mime, lineLength, lineSeparator);
        if (urlSafe) {
            encoder = java.util.Base64.getUrlEncoder();
            decoder = java.util.Base64.getUrlDecoder();
        }
        else if (mime) {
            encoder = lineLength > ZERO && ArrayUtils.isNotEmpty(lineSeparator)
                    ? java.util.Base64.getMimeEncoder(lineLength, lineSeparator)
                    : java.util.Base64.getMimeEncoder();
            decoder = java.util.Base64.getMimeDecoder();
        }
        else {
            encoder = java.util.Base64.getEncoder();
            decoder = java.util.Base64.getDecoder();
        }
    }

    @Override
    public byte[] encode(byte[] source) {
        if (ArrayUtils.isEmpty(source)) { return source; }
        return encoder.encode(source);
    }

    @Override
    public byte[] decode(byte[] source) {
        if (ArrayUtils.isEmpty(source)) { return source; }
        return decoder.decode(source);
    }

}
