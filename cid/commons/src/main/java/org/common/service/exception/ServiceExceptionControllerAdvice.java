package org.common.service.exception;

import java.net.ConnectException;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.common.exception.impl.DefaultWrappedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServiceExceptionControllerAdvice extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = LogManager.getLogger(ServiceExceptionControllerAdvice.class);

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		final BindingResult result = ex.getBindingResult();
		final List<FieldError> fieldErrors = result.getFieldErrors();
		final StringBuilder validationErrors = new StringBuilder();
		for(final FieldError fieldError : fieldErrors) {
			validationErrors.append("Field error in object '" + fieldError.getObjectName() + "' on field '" + fieldError.getField() + "': rejected value [" + fieldError.getRejectedValue() + "] with cause: [" + fieldError.getDefaultMessage() + " ]");
		}
		final Exception e = new DefaultWrappedException(validationErrors.toString(), ex, null);
		ExceptionUtil.logException(e);
		return this.handleExceptionInternal(e, validationErrors.toString(), headers, status, request);
	}

	@ExceptionHandler({PersistenceException.class, CannotCreateTransactionException.class, TransactionSystemException.class})
	protected ResponseEntity<Object> handlePersistenceException(final Exception ex, final WebRequest request) {
		final Exception wrappedException = new DefaultWrappedException(ExceptionUtil.ExceptionCodes.Database.PERSISTENCE_EXCEPTION, ex, null);
		ExceptionUtil.logException(wrappedException);
		return this.handleExceptionInternal(wrappedException, wrappedException.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler({ResourceAccessException.class, ConnectException.class})
	protected ResponseEntity<Object> handleServiceConnectionException(final Exception ex, final WebRequest request) {
		String exceptionMessageName = getExceptionMessageName(ex.getMessage());
		final Exception wrappedException = new DefaultWrappedException(exceptionMessageName, ex, null);
		ExceptionUtil.logException(wrappedException);
		return this.handleExceptionInternal(wrappedException, wrappedException.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(DefaultWrappedException.class)
	protected ResponseEntity<Object> handleDefaultWrappedException(final Exception ex, final WebRequest request) {
		final Exception wrappedException = new DefaultWrappedException(ex.getMessage(), ex, null);
		ExceptionUtil.logException(wrappedException);
		return this.handleExceptionInternal(wrappedException, wrappedException.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	private String getExceptionMessageName(final String message) {
		try {
			String serviceName = message.split("/")[4];
			return StringUtils.isNotEmpty(serviceName) ? serviceName.toUpperCase() + "_SERVICE_CONNECTION_REFUSED" : message;
		} catch(Exception e) {
			LOGGER.error("Exception while fetching the service name", e);
			return message;
		}
	}

	@ExceptionHandler(HttpServerErrorException.class)
	protected ResponseEntity<Object> handleDefaultWrappedException(final HttpServerErrorException ex, final WebRequest request) {
		final Exception wrappedException = new DefaultWrappedException(ex.getMessage(), ex, null);
		ExceptionUtil.logException(wrappedException);
		return this.handleExceptionInternal(wrappedException, wrappedException.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	/**
	 * Handle all other service exceptions.
	 * @param ex the exception
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleServiceException(final Exception ex, final WebRequest request) {
		final Exception wrappedException = new DefaultWrappedException(ex.getMessage(), ex, null);
		ExceptionUtil.logException(wrappedException);
		return this.handleExceptionInternal(wrappedException, wrappedException.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
}