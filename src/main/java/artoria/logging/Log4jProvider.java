package artoria.logging;

import artoria.renderer.support.FormatTextRenderer;
import artoria.renderer.support.LoggerTextRenderer;
import artoria.util.Assert;

import static artoria.common.constant.Symbols.EMPTY_STRING;

/**
 * Log4j logger adapter.
 * @author Kahle
 */
public class Log4jProvider implements LoggerProvider {
    /**
     * Root logger.
     */
    private static org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
    /**
     * Logger template engine.
     */
    private FormatTextRenderer textRenderer;

    public Log4jProvider() {

        this(new LoggerTextRenderer());
    }

    public Log4jProvider(FormatTextRenderer textRenderer) {

        setTextRenderer(textRenderer);
    }

    public FormatTextRenderer getTextRenderer() {

        return textRenderer;
    }

    public void setTextRenderer(FormatTextRenderer textRenderer) {
        Assert.notNull(textRenderer, "Parameter \"textRenderer\" must not null. ");
        this.textRenderer = textRenderer;
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
        String name = clazz == null ? EMPTY_STRING : clazz.getName();
        return new Log4jLogger(org.apache.log4j.Logger.getLogger(name), getTextRenderer());
    }

    @Override
    public Logger getLogger(String clazz) {
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(clazz);
        return new Log4jLogger(logger, getTextRenderer());
    }

    @Override
    public Level getLevel() {
        org.apache.log4j.Level level = rootLogger.getLevel();
        if (level == org.apache.log4j.Level.TRACE) {
            return Level.TRACE;
        }
        if (level == org.apache.log4j.Level.DEBUG) {
            return Level.DEBUG;
        }
        if (level == org.apache.log4j.Level.INFO) {
            return Level.INFO;
        }
        if (level == org.apache.log4j.Level.WARN) {
            return Level.WARN;
        }
        if (level == org.apache.log4j.Level.ERROR) {
            return Level.ERROR;
        }
        return null;
    }

    @Override
    public void setLevel(Level level) {
        if (level == Level.TRACE) {
            rootLogger.setLevel(org.apache.log4j.Level.TRACE);
        }
        if (level == Level.DEBUG) {
            rootLogger.setLevel(org.apache.log4j.Level.DEBUG);
        }
        if (level == Level.INFO) {
            rootLogger.setLevel(org.apache.log4j.Level.INFO);
        }
        if (level == Level.WARN) {
            rootLogger.setLevel(org.apache.log4j.Level.WARN);
        }
        if (level == Level.ERROR) {
            rootLogger.setLevel(org.apache.log4j.Level.ERROR);
        }
    }

}
