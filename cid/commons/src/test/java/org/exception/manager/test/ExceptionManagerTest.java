package org.exception.manager.test;

import org.common.exception.manager.ExceptionManager;
import org.common.exception.manager.ExceptionManagerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionManagerTest {

	@Mock
	private static ExceptionManager exceptionManager = null;

	@Test
	public void testSingletonImplementation() {
		final ExceptionManager newExceptionManager = ExceptionManagerImpl.getExceptionManager();
		final ExceptionManager newExceptionManager1 = ExceptionManagerImpl.getExceptionManager();
		Assert.assertEquals(newExceptionManager1, newExceptionManager);
	}
}