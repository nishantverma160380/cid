package org.common.exception.impl;

import java.io.Serializable;

import org.common.exception.WrappedException;
import org.common.exception.manager.ExceptionManagerImpl;

@SuppressWarnings("serial")
public class DefaultWrappedException extends Exception implements Serializable, WrappedException {

	protected final String exceptionId;

	public DefaultWrappedException() {
		super();
		this.exceptionId = null;
	}

	public DefaultWrappedException(final String exceptionId, final Throwable throwable, final Object[] messageParameters) {
		super(ExceptionManagerImpl.getExceptionDescription(exceptionId, messageParameters), throwable);
		this.exceptionId = exceptionId;
	}

	public DefaultWrappedException(final String exceptionId, final Throwable throwable) {
		this(exceptionId, throwable, null);
	}

	public DefaultWrappedException(final Throwable throwable) {
		this(null, throwable);
	}

	public DefaultWrappedException(final String exceptionId) {
		this(exceptionId, null);
	}

	@Override
	public String getExceptionId() {
		return this.exceptionId;
	}
}