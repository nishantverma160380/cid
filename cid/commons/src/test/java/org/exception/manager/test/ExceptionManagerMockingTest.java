package org.exception.manager.test;

import org.common.exception.impl.DefaultWrappedException;
import org.common.exception.manager.ExceptionManager;
import org.common.exception.manager.ExceptionManagerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionManagerMockingTest {

	@Test
	public void testLogErrorEvent_FATAL_NoStacktrace() {
		final ExceptionManager exceptionManager = ExceptionManagerImpl.getExceptionManager();
		Assert.assertNotNull(exceptionManager);
		exceptionManager.logErrorEvent(new DefaultWrappedException(), null);
	}
}