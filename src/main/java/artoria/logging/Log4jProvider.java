package artoria.logging;

import static artoria.common.Constants.EMPTY_STRING;

/**
 * Log4j logger adapter.
 * @author Kahle
 */
public class Log4jProvider implements LoggerProvider {
	private static org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();

	public Log4jProvider() {
	}

	@Override
	public Logger getLogger(Class<?> clazz) {
		String name = clazz == null ? EMPTY_STRING : clazz.getName();
		return new Log4jLogger(org.apache.log4j.Logger.getLogger(name));
	}

	@Override
	public Logger getLogger(String clazz) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(clazz);
		return new Log4jLogger(logger);
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
