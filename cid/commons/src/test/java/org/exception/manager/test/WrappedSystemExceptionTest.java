package org.exception.manager.test;

import org.common.constants.CommonConstants;
import org.common.domain.ContextInfo;
import org.common.exception.impl.WrappedSystemException;
import org.common.exception.manager.ExceptionManager;
import org.common.exception.manager.ExceptionManagerImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WrappedSystemExceptionTest {

	private static ExceptionManager exceptionManager;

	@BeforeClass
	public static void setUp() {
		exceptionManager = ExceptionManagerImpl.getExceptionManager();
	}

	@Test
	public void testWrappedSystemException() {
		WrappedSystemException exception = null;
		try {
			Class.forName("DummyUnknowClass");
		} catch(final Exception e) {
			exception = new WrappedSystemException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE_WITH_PARAMETERS, e, new String[] {"param-1"});
			exceptionManager.logErrorEvent(exception, new ContextInfo("Sample", "Sample"));
		} finally {
			Assert.assertNotNull(exception);
			Assert.assertEquals(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE_WITH_PARAMETERS + "param-1", exception.getMessage());
		}
	}

	@Test(expected = WrappedSystemException.class)
	public void testWrappedSystemExceptionWithoutId() throws WrappedSystemException {
		throw new WrappedSystemException();
	}

	@Test
	public void testCreateWrappedSystemException() {
		final WrappedSystemException exception = new WrappedSystemException(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE);
		Assert.assertNotNull(exception);
		Assert.assertEquals(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE, exception.getMessage());
	}

	@Test
	public void testWrappedSystemExceptionWithThrowable() {
		final WrappedSystemException exception = new WrappedSystemException(new Throwable());
		Assert.assertNotNull(exception);
	}

	@Test
	public void testWrappedSystemExceptionWithThrowableAndExceptionId() {
		final WrappedSystemException exception = new WrappedSystemException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE, new Throwable());
		Assert.assertNotNull(exception);
		Assert.assertEquals(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE, exception.getMessage());
	}

	@Test
	public void testWrappedSystemExceptionWithThrowableAndExceptionIdAndParameters() {
		final WrappedSystemException exception = new WrappedSystemException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE_WITH_PARAMETERS, new Exception(), new String[] {"param-1"});
		Assert.assertNotNull(exception);
		Assert.assertEquals(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE_WITH_PARAMETERS + "param-1", exception.getMessage());
		Assert.assertEquals(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE_WITH_PARAMETERS, exception.getExceptionId());
	}

	@Test
	public void testWrappedSystemExceptionEvent() {
		final WrappedSystemException exception = new WrappedSystemException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE, new Throwable(), null);
		exceptionManager.logErrorEvent(exception);
	}

	@Test
	public void testWrappedSystemExceptionError() {
		final WrappedSystemException exception = new WrappedSystemException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE, new Throwable(), null);
		exceptionManager.logErrorEvent(exception, new ContextInfo("Sample", "Sample"));
	}
}