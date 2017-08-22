package org.common.exception.manager;

import java.util.ResourceBundle;

import org.common.domain.ContextInfo;

public interface ExceptionManager {

	void logErrorEvent(Throwable exception);

	void logErrorEvent(String messageId, Throwable exception);

	void logErrorEvent(Throwable exception, ContextInfo contextInfo);

	void logErrorEvent(String messageId, Throwable exception, ContextInfo contextInfo);

	void logFatalEvent(Throwable exception);

	void logFatalEvent(String messageId, Throwable exception);

	void logFatalEvent(Throwable exception, ContextInfo contextInfo);

	void logFatalEvent(String messageId, Throwable exception, ContextInfo contextInfo);

	ResourceBundle getMessageResourceBundle();
}
