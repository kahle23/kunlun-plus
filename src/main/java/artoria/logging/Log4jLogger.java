package artoria.logging;

import org.apache.log4j.Logger;

/**
 * Log4j logger.
 * @author Kahle
 */
public class Log4jLogger implements artoria.logging.Logger {
    private final Logger logger;

    public Log4jLogger(Logger logger) {

        this.logger = logger;
    }

    private void log(org.apache.log4j.Level level, String message, Throwable t) {
        StackTraceElement element = new Throwable().getStackTrace()[2];
        String clazzName = element.getClassName();
        // TODO: render arguments
        logger.log(clazzName, level, message, t);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (!this.isTraceEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.TRACE, format, null);
    }

    @Override
    public void trace(String message, Throwable throwable) {
        if (!this.isTraceEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.TRACE, message, throwable);
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (!this.isDebugEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.DEBUG, format, null);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        if (!this.isDebugEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.DEBUG, message, throwable);
    }

    @Override
    public void info(String format, Object... arguments) {
        if (!this.isInfoEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.INFO, format, null);
    }

    @Override
    public void info(String message, Throwable throwable) {
        if (!this.isInfoEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.INFO, message, throwable);
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (!this.isWarnEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.WARN, format, null);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        if (!this.isWarnEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.WARN, message, throwable);
    }

    @Override
    public void error(String format, Object... arguments) {
        if (!this.isErrorEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.ERROR, format, null);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (!this.isErrorEnabled()) {
            return;
        }
        this.log(org.apache.log4j.Level.ERROR, message, throwable);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isEnabledFor(org.apache.log4j.Level.TRACE);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isEnabledFor(org.apache.log4j.Level.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isEnabledFor(org.apache.log4j.Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(org.apache.log4j.Level.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(org.apache.log4j.Level.ERROR);
    }

}
