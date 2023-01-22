package artoria.time;

public class TimePatterns {

    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";
    public static final String FULL_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String Y4MD2MI = "yyyy-MM-dd";
    public static final String Y4MD2SL = "yyyy/MM/dd";

    public static final String HMS2CO = "HH:mm:ss";
    public static final String HM2CO = "HH:mm";

    public static final String Y4MD2SL_HMS2CO = "yyyy/MM/dd HH:mm:ss";
    public static final String Y4MD2MI_HMS2CO = "yyyy-MM-dd HH:mm:ss";

    public static final String Y4MD2SL_HM2CO = "yyyy/MM/dd HH:mm";
    public static final String Y4MD2MI_HM2CO = "yyyy-MM-dd HH:mm";


    private TimePatterns() {

        throw new UnsupportedOperationException("Don't allow instantiation. ");
    }

}
