package org.common.domain;

import org.junit.Assert;
import org.junit.Test;

public class ContextInfoTest {

	@Test
	public void testContextInfoConstruction() {
		ContextInfo contextInfo = new ContextInfo("moduleName", "operation");
		contextInfo.setSessionId("sessionId");
		contextInfo.setSsoTicket("ssoTicket");
		contextInfo.setTransactionId("transactionId");
		contextInfo.setTransactionRequestedByUserId("transactionRequestedByUserId");
		contextInfo.setTransactionRequestedByUsername("transactionRequestedByUsername");
		contextInfo.addEventData("key1", "value1");
		contextInfo.addEventData("key2", "value2");
		contextInfo.addEventData("key3", "value3");

		String contextInfoString = contextInfo.toString();
		ContextInfo convertedContextInfo = ContextInfo.toContextInfo(contextInfoString);

		Assert.assertEquals("ContextInfo and ConvertedContextInfo ModuleName should be equal", contextInfo.getModuleName(), convertedContextInfo.getModuleName());
		Assert.assertEquals("ContextInfo and ConvertedContextInfo Operation should be equal", contextInfo.getOperation(), convertedContextInfo.getOperation());
		Assert.assertEquals("ContextInfo and ConvertedContextInfo SessionId should be equal", contextInfo.getSessionId(), convertedContextInfo.getSessionId());
		Assert.assertEquals("ContextInfo and ConvertedContextInfo SsoTicket should be equal", contextInfo.getSsoTicket(), convertedContextInfo.getSsoTicket());
		Assert.assertEquals("ContextInfo and ConvertedContextInfo TransactionId should be equal", contextInfo.getTransactionId(), convertedContextInfo.getTransactionId());
		Assert.assertEquals("ContextInfo and ConvertedContextInfo TransactionRequestedByUserId should be equal", contextInfo.getTransactionRequestedByUserId(), convertedContextInfo.getTransactionRequestedByUserId());
		Assert.assertEquals("ContextInfo and ConvertedContextInfo TransactionRequestedByUsername should be equal", contextInfo.getTransactionRequestedByUsername(), convertedContextInfo.getTransactionRequestedByUsername());
		Assert.assertEquals("ContextInfo and ConvertedContextInfo EventData should be equal", contextInfo.getEventData(), convertedContextInfo.getEventData());
	}
}
