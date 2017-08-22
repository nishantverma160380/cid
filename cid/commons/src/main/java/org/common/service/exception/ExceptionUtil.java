package org.common.service.exception;

import org.common.domain.ContextInfo;
import org.common.domain.ContextThreadLocal;
import org.common.exception.manager.ExceptionManager;
import org.common.exception.manager.ExceptionManagerImpl;

public final class ExceptionUtil {

	private static ExceptionManager exceptionManager = ExceptionManagerImpl.getExceptionManager();

	public interface ExceptionCodes {
		public interface Database {
			String PERSISTENCE_EXCEPTION = "DATABASE_PERSISTENCE_EXCEPTION";
		}

		public interface Connection {
			String CONNECTION_EXCEPTION = "CONNECTION_EXCEPTION";
		}
	}

	private ExceptionUtil() {
	}

	public static void logException(final Exception exception, final ContextInfo contextInfo) {
		exceptionManager.logErrorEvent(exception, contextInfo);
	}

	public static void logException(final Exception exception) {
		exceptionManager.logErrorEvent(exception, ContextThreadLocal.get());
	}
}