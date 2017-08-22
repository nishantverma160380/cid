package org.common.domain;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.helpers.LogLog;
import org.codehaus.jackson.map.ObjectMapper;
import org.common.constants.CommonConstants;
import org.json.simple.JSONObject;

/**
 * The Class ContextInfo is designed to hold the session/operation information
 * throughout the transaction. This object can be used to track the progress of
 * transaction through logs. Also, this object can be used to do the auditing.
 *
 * Passing this information through request headers between different services/microservices
 * will help you trace the transaction in case of any issues.
 */
public class ContextInfo {

	//Constants for defining toString variables.
	private static final String MODULE_NAME = "moduleName";
	private static final String OPERATION = "operation";
	private static final String SSO_TICKET = "ssoTicket";
	private static final String SESSION_ID = "sessionId";
	private static final String TRANSACTION_ID = "transactionId";
	private static final String TRANSACTION_REQUESTED_BY_USER_ID = "transactionRequestedByUserId";
	private static final String TRANSACTION_REQUESTED_BY_USERNAME = "transactionRequestedByUsername";
	private static final String EVENT_DATA = "eventData";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private String moduleName;
	private String operation;
	private String ssoTicket;
	private String sessionId;
	private String transactionId;
	private String transactionRequestedByUserId;
	private String transactionRequestedByUsername;
	private final JSONObject eventData;

	public ContextInfo() {
		this.eventData = new JSONObject();
		this.transactionId = generateTransactionId();
	}

	public ContextInfo(final String moduleName, final String operation) {
		this();
		this.setModuleName(moduleName);
		this.setOperation(operation);
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(final String moduleName) {
		this.moduleName = moduleName;
	}

	public String getOperation() {
		return this.operation;
	}

	public void setOperation(final String operation) {
		this.operation = operation;
	}

	public String getSsoTicket() {
		return this.ssoTicket;
	}

	public void setSsoTicket(final String ssoTicket) {
		this.ssoTicket = ssoTicket;
	}

	public String getSessionId() {
		return this.sessionId;
	}
	public void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionRequestedByUserId() {
		return this.transactionRequestedByUserId;
	}

	public void setTransactionRequestedByUserId(final String transactionRequestedByUserId) {
		this.transactionRequestedByUserId = transactionRequestedByUserId;
	}

	public String getTransactionRequestedByUsername() {
		return this.transactionRequestedByUsername;
	}

	public void setTransactionRequestedByUsername(final String transactionRequestedByUsername) {
		this.transactionRequestedByUsername = transactionRequestedByUsername;
	}

	public JSONObject getEventData() {
		return this.eventData;
	}

	@SuppressWarnings("unchecked")
	public void addEventData(final String key, final String value) {
		this.eventData.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public void addAllEventData(final Map<?, ?> eventData) {
		this.eventData.putAll(eventData);
	}

	/**
	 * This method generates the unique transaction id which will help in tracing
	 * the transaction through the logs.
	 *
	 * @return the string
	 */
	public static String generateTransactionId() {
		String hostName = StringUtils.EMPTY;

		try {
			hostName = Inet4Address.getLocalHost().getHostName();
		} catch(final UnknownHostException exception) {
			LogLog.debug("Get Host Name Failed", exception);
		}

		final UUID uuid = UUID.randomUUID();

		final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
		String encodedId = new String(Base64.encodeBase64(buffer.array()));

		if(StringUtils.endsWith(encodedId, CommonConstants.WhitespaceLiterals.EQUAL_TO)) {
			encodedId = StringUtils.removeEnd(encodedId, CommonConstants.WhitespaceLiterals.EQUAL_TO);
		}
		final String[] transactionIds = {hostName, CommonConstants.WhitespaceLiterals.HYPHEN, encodedId};
		return StringUtils.join(transactionIds);
	}

	/**
	 * Converts the string format context information to object format.
	 * Can be used in constructing the header context info string
	 * back to context info object.
	 *
	 * @param context the context
	 * @return the context info
	 */
	//TODO: Use JAXB for this marshalling and unmarshalling.
	public static ContextInfo toContextInfo(final String context) {
		final ContextInfo contextInfo = new ContextInfo();
		String stringContextInfo = context;
		if(!StringUtils.endsWith(stringContextInfo, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR)) {
			stringContextInfo += CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR;
		}
		contextInfo.setModuleName(StringUtils.substringBetween(stringContextInfo, MODULE_NAME + CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR));
		contextInfo.setOperation(StringUtils.substringBetween(stringContextInfo, OPERATION + CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR));
		contextInfo.setSsoTicket(StringUtils.substringBetween(stringContextInfo, SSO_TICKET + CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR));
		contextInfo.setSessionId(StringUtils.substringBetween(stringContextInfo, SESSION_ID + CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR));

		final String transactionId = StringUtils.substringBetween(stringContextInfo, TRANSACTION_ID + CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		if(StringUtils.isNotEmpty(transactionId)) {
			contextInfo.setTransactionId(transactionId);
		}

		try {
			final String eventData = StringUtils.substringBetween(stringContextInfo, EVENT_DATA + CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);

			if(StringUtils.isNotEmpty(eventData)) {
				contextInfo.addAllEventData(OBJECT_MAPPER.readValue(eventData, Map.class));
			}
		} catch(final Exception e) {
			LogLog.debug("Event data cannot be converted to Map", e);
		}

		contextInfo.setTransactionRequestedByUserId(StringUtils.substringBetween(stringContextInfo, TRANSACTION_REQUESTED_BY_USER_ID + CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR));
		contextInfo.setTransactionRequestedByUsername(StringUtils.substringBetween(stringContextInfo, TRANSACTION_REQUESTED_BY_USERNAME + CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR, CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR));
		return contextInfo;
	}

	/**
	 * Returns the context info in string format by appending only the values which are not null.
	 *
	 * @return the context info
	 */
	@Override
	public String toString() {
		final StringBuilder stringContextInfo = new StringBuilder();
		if(this.getModuleName() != null) {
			stringContextInfo.append(MODULE_NAME).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(this.getModuleName()).append(CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		}
		if(this.getOperation() != null) {
			stringContextInfo.append(OPERATION).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(this.getOperation()).append(CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		}
		if(this.getSsoTicket() != null) {
			stringContextInfo.append(SSO_TICKET).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(this.getSsoTicket()).append(CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		}
		if(this.getSessionId() != null) {
			stringContextInfo.append(SESSION_ID).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(this.getSessionId()).append(CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		}
		if(this.getTransactionId() != null) {
			stringContextInfo.append(TRANSACTION_ID).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(this.getTransactionId()).append(CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		}
		if(this.getTransactionRequestedByUserId() != null) {
			stringContextInfo.append(TRANSACTION_REQUESTED_BY_USER_ID).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(this.getTransactionRequestedByUserId()).append(CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		}
		if(this.getTransactionRequestedByUsername() != null) {
			stringContextInfo.append(TRANSACTION_REQUESTED_BY_USERNAME).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(this.getTransactionRequestedByUsername()).append(CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		}
		if(!this.getEventData().isEmpty()) {
			stringContextInfo.append(EVENT_DATA).append(CommonConstants.WhitespaceLiterals.NAME_VALUE_SEPERATOR).append(this.getEventData()).append(CommonConstants.WhitespaceLiterals.ATTRIBUTES_SEPERATOR);
		}
		return stringContextInfo.toString();
	}
}