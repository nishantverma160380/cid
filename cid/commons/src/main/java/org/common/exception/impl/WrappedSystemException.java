package org.common.exception.impl;

import java.io.Serializable;

import org.common.exception.WrappedException;
import org.common.exception.manager.ExceptionManagerImpl;

@SuppressWarnings("serial")
public class WrappedSystemException extends RuntimeException implements Serializable, WrappedException {
	private final String exceptionId;

	public WrappedSystemException() {
		super();
		this.exceptionId = null;
	}

	public WrappedSystemException(final String exceptionId, final Throwable throwable, final Object[] messageParameters) {
		super(ExceptionManagerImpl.getExceptionDescription(exceptionId, messageParameters), throwable);
		this.exceptionId = exceptionId;
	}

	public WrappedSystemException(final String exceptionId, final Throwable throwable) {
		this(exceptionId, throwable, null);
	}

	public WrappedSystemException(final Throwable throwable) {
		this(null, throwable);
	}

	public WrappedSystemException(final String exceptionId) {
		this(exceptionId, null);
	}

	@Override
	public String getExceptionId() {
		return this.exceptionId;
	}
}