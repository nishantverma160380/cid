package org.common.utils;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.common.constants.CommonConstants;
import org.common.domain.ContextInfo;

/**
 * The Class LoggingUtil provides the utility methods for loggers.
 */
public class LoggingUtil {

	private static final Logger LOGGER = LogManager.getLogger(LoggingUtil.class);

	/** The resource bundle which needs to be used to read the log messages. */
	private static ResourceBundle resourceBundle;

	static {
		resourceBundle = ResourceBundle.getBundle(CommonConstants.MessageBundles.LOG_MESSAGE_RESOURCE_BUNDLE);
	}

	/**
	 * Gets the message description by reading it from log-messages-store.properties file using configured key.
	 *
	 * @param messageId the key to search in log-messages-store.properties
	 * @param param the parameters to be replaced in the message description
	 * @param contextInfo the context information
	 * @return the message description which has been fetched from log-messages-store.properties by replacing the parameters if any
	 */
	public static String getMessageDescription(final String messageId, final Object[] param, final ContextInfo contextInfo) {
		final StringBuilder messageBuffer = new StringBuilder();

		if(contextInfo != null) {
			messageBuffer.append(contextInfo.toString());
		}

		if(resourceBundle != null) {
			if(param != null) {
				messageBuffer.append(MessageFormat.format(resourceBundle.getString(messageId), param));
			} else {
				messageBuffer.append(resourceBundle.getString(messageId));
			}
		} else {
			messageBuffer.append(messageId);
		}
		return messageBuffer.toString();
	}

	/**
	 * Gets the message description by reading it from log-messages-store.properties file using configured key.
	 *
	 * @param messageId the key to search in log-messages-store.properties
	 * @param param the parameters to be replaced in the message description
	 * @return the message description which has been fetched from log-messages-store.properties by replacing the parameters if any
	 */
	public static String getMessageDescription(final String messageId, final Object[] param) {
		return getMessageDescription(messageId, param, null);
	}

	/**
	 * This method allows you to change the logging level of the application
	 * (for all loggers) dynamically without restarting the JVM/Server.
	 * If the logLevel value does not convert to any enum listed in Level,
	 * it will default it to DEBUG level.
	 *
	 * @param logLevel the log level in string format
	 * @return whether the operation is successful by setting the log level to given or DEFAULT level
	 */
	public static String changeLogLevels(String logLevel) {
		Level level = Level.toLevel(logLevel);
		return changeLogLevel(level);
	}

	/**
	 * This method allows you to change the logging level of the application
	 * dynamically without restarting the JVM/Server.
	 *
	 * @param the log level to which the logging level to be set
	 * @return whether the operation is successful by setting the log level
	 */
	public static String changeLogLevel(Level level) {
		LOGGER.info("Setting the log level to :" + level);
		Enumeration<?> loggers = LogManager.getCurrentLoggers();
		while(loggers.hasMoreElements()) {
			Logger logger = (Logger) loggers.nextElement();
			//Change logging level of only those loggers which are configured in your log4j.properties file
			if(logger.getLevel() != null) {
				logger.setLevel(level);
			}
		}
		return "Successfully set the log level to " + level;
	}
}
