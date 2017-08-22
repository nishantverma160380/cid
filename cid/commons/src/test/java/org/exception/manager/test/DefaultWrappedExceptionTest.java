package org.exception.manager.test;

import org.common.constants.CommonConstants;
import org.common.domain.ContextInfo;
import org.common.exception.impl.DefaultWrappedException;
import org.common.exception.manager.ExceptionManager;
import org.common.exception.manager.ExceptionManagerImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultWrappedExceptionTest {

	private static ExceptionManager exceptionManager;

	@BeforeClass
	public static void setUp() {
		exceptionManager = ExceptionManagerImpl.getExceptionManager();
	}

	@Test
	public void testDefaultWrappedException() {
		DefaultWrappedException exception = null;
		try {
			Class.forName("DummyUnknowClass");
		} catch(final Exception e) {
			exception = new DefaultWrappedException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE_WITH_PARAMETERS, e, new String[] {"param-1"});
			ContextInfo contextInfo = new ContextInfo();
			contextInfo.setSessionId("sessionId");
			exceptionManager.logErrorEvent(exception, contextInfo);
		} finally {
			Assert.assertNotNull(exception);
			Assert.assertEquals(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE_WITH_PARAMETERS + "param-1", exception.getMessage());
		}
	}

	@Test(expected = DefaultWrappedException.class)
	public void testDefaultWrappedExceptionWithoutId() throws DefaultWrappedException {
		throw new DefaultWrappedException();
	}

	@Test
	public void testCreateDefaultWrappedException() {
		final DefaultWrappedException exception = new DefaultWrappedException(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE);
		Assert.assertNotNull(exception);
		Assert.assertEquals(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE, exception.getMessage());
	}

	@Test
	public void testDefaultWrappedExceptionWithThrowable() {
		final DefaultWrappedException exception = new DefaultWrappedException(new Throwable());
		Assert.assertNotNull(exception);
	}

	@Test
	public void testDefaultWrappedExceptionWithThrowableAndExceptionId() {
		final DefaultWrappedException exception = new DefaultWrappedException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE, new Throwable());
		Assert.assertNotNull(exception);
		Assert.assertEquals(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE, exception.getMessage());
	}

	@Test
	public void testDefaultWrappedExceptionWithThrowableAndExceptionIdAndParameters() {
		final DefaultWrappedException exception = new DefaultWrappedException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE_WITH_PARAMETERS, new Exception(), new String[] {"param-1"});
		Assert.assertNotNull(exception);
		Assert.assertEquals(CommonConstants.ErrorMessageValues.DEFAULT_MESSAGE_WITH_PARAMETERS + "param-1", exception.getMessage());
		Assert.assertEquals(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE_WITH_PARAMETERS, exception.getExceptionId());
	}

	@Test
	public void testDefaultWrappedExceptionEvent() {
		final DefaultWrappedException exception = new DefaultWrappedException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE, new Throwable(), null);
		exceptionManager.logErrorEvent(exception);
	}

	@Test
	public void testDefaultWrappedExceptionError() {
		final DefaultWrappedException exception = new DefaultWrappedException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE, new Throwable(), null);
		exceptionManager.logErrorEvent(exception, new ContextInfo("Sample", "Sample"));
	}

	@Test
	public void testDefaultWrappedExceptionFatal() {
		final DefaultWrappedException exception = new DefaultWrappedException(CommonConstants.ErrorMessageKeys.DEFAULT_MESSAGE, new Throwable(), null);
		exceptionManager.logErrorEvent(exception, new ContextInfo("Sample", "Sample"));
	}
}