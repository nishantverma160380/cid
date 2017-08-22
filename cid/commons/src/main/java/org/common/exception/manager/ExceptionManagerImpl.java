package org.common.exception.manager;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.LogLog;
import org.common.constants.CommonConstants;
import org.common.domain.ContextInfo;

public final class ExceptionManagerImpl implements ExceptionManager {

	private static ExceptionManager exceptionManager = null;
	private static final Logger LOGGER = LogManager.getLogger(ExceptionManagerImpl.class);

	private ResourceBundle messageResourceBundle;

	private ExceptionManagerImpl() {

		LOGGER.info("Initialising the  Exception Manager Start.");
		try {
			LOGGER.info("Loading Exception Resource Bundle :" + CommonConstants.MessageBundles.LOG_MESSAGE_RESOURCE_BUNDLE);
			this.messageResourceBundle = ResourceBundle.getBundle(CommonConstants.MessageBundles.LOG_MESSAGE_RESOURCE_BUNDLE);
			LOGGER.setResourceBundle(this.messageResourceBundle);
		} catch(final Exception ex) {
			LOGGER.fatal("Exception in intialisation of Exception Manager -- Failed to set Resoutce bundle.", ex);
			throw ex;
		}
		LOGGER.debug("Exception resource bundle name:" + CommonConstants.MessageBundles.LOG_MESSAGE_RESOURCE_BUNDLE);
		LOGGER.info("Initialising the Exception Manager End.");
	}

	/**
	 * Singleton implementation for exception manager.
	 * @return the exception manager
	 */
	public static synchronized ExceptionManager getExceptionManager() {
		if(exceptionManager == null) {
			exceptionManager = new ExceptionManagerImpl();
		}
		return exceptionManager;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void logErrorEvent(final String messageId, final Throwable exception, final ContextInfo contextInfo) {
		final Object[] contextParam = createParameters(exception, contextInfo);
		LOGGER.l7dlog(Priority.ERROR, messageId, contextParam, exception);
	}

	@Override
	public void logErrorEvent(final Throwable exception, final ContextInfo contextInfo) {
		this.logErrorEvent(CommonConstants.AUDIT_MESSAGE, exception, contextInfo);
	}

	@Override
	public void logErrorEvent(final String messageId, final Throwable exception) {
		this.logErrorEvent(messageId, exception, null);
	}

	@Override
	public void logErrorEvent(final Throwable exception) {
		this.logErrorEvent(exception, null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void logFatalEvent(final String messageId, final Throwable exception, final ContextInfo contextInfo) {
		final Object[] contextParam = createParameters(exception, contextInfo);
		LOGGER.l7dlog(Priority.ERROR, messageId, contextParam, exception);
	}

	@Override
	public void logFatalEvent(final Throwable exception, final ContextInfo contextInfo) {
		this.logFatalEvent(CommonConstants.AUDIT_MESSAGE, exception, contextInfo);
	}

	@Override
	public void logFatalEvent(final String messageId, final Throwable exception) {
		this.logFatalEvent(messageId, exception, null);
	}

	@Override
	public void logFatalEvent(final Throwable exception) {
		this.logErrorEvent(exception, null);
	}

	@Override
	public ResourceBundle getMessageResourceBundle() {
		return this.messageResourceBundle;
	}

	private Object[] createParameters(final Throwable exception, final ContextInfo contextInfo) {
		Object[] returnValues = null;
		final StringBuilder stringContextInfo = new StringBuilder();
		if(contextInfo != null) {
			stringContextInfo.append(contextInfo.toString());
		}
		if(exception != null) {
			stringContextInfo.append(CommonConstants.ERROR_MESSAGE).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(exception.getMessage());
		}
		returnValues = new Object[1];
		returnValues[0] = stringContextInfo.toString();
		return returnValues;
	}

	public static String getExceptionDescription(final String exceptionId, final Object[] messageParameters) {
		final StringBuilder exceptionDescription = new StringBuilder(exceptionId + CommonConstants.WhitespaceLiterals.BLANK_SPACE + CommonConstants.WhitespaceLiterals.HYPHEN + CommonConstants.WhitespaceLiterals.BLANK_SPACE);
		final ResourceBundle messageResources = ExceptionManagerImpl.getExceptionManager().getMessageResourceBundle();
		if(exceptionId != null) {
			try {
				if(messageParameters != null) {
					exceptionDescription.append(MessageFormat.format(messageResources.getString(exceptionId), messageParameters));
				} else {
					exceptionDescription.append(messageResources.getString(exceptionId));
				}
			} catch(final MissingResourceException e) {
				LogLog.debug("Get Message Failed", e);
				return exceptionId;
			}
		} else {
			return exceptionId;
		}
		return exceptionDescription.toString();
	}
}