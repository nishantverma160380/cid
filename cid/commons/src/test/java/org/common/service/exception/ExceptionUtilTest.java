package org.common.service.exception;

import org.common.exception.impl.DefaultWrappedException;
import org.junit.Assert;
import org.junit.Test;

public class ExceptionUtilTest {

	@Test
	public void createExceptionTest() {
		final String userUUID = "123412341234";
		final DefaultWrappedException exception = new DefaultWrappedException("USER_WITH_UUID_NOT_FOUND_EXCEPTION", null, new Object[] {userUUID});
		final DefaultWrappedException returnedException = new DefaultWrappedException(exception.getExceptionId(), exception, null);
		Assert.assertEquals("Exception objects should be equal.", exception.getExceptionId(), returnedException.getExceptionId());
	}
}