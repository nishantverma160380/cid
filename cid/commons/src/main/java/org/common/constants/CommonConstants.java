package org.common.constants;


public interface CommonConstants {

	String CONTEXT_INFORMATION_REQUEST_PARAMETER = "context";

	String AUDIT_MESSAGE = "AUDIT_MESSAGE";
	String ERROR_MESSAGE = "errorMessage";

	interface Separators {
		String DOLLAR_SEPARATOR = "$";
		String URL_SEPARATOR = "/";
		String BLANK_SPACE_SEPARATOR = " ";
		String ATTRIBUTES_SEPERATOR = " | ";
		String NAME_VALUE_SEPERATOR = " : ";
		String EQUAL_TO_SEPARATOR = "==";
		String HYPHEN_SEPARATOR = "-";
		String AMPERSAND_SEPARATOR = "&";
	}

	interface MessageBundles {
		String LOG_MESSAGE_RESOURCE_BUNDLE = "log-messages-store";
	}

	interface WhitespaceLiterals {
		String BLANK_SPACE = " ";
		String ATTRIBUTES_SEPERATOR = " | ";
		String NAME_VALUE_SEPERATOR = " : ";
		String EQUAL_TO = " = ";
		String HYPHEN = "-";
	}

	interface ErrorMessageKeys {
		String DEFAULT_MESSAGE = "DEFAULT_MESSAGE";
		String DEFAULT_MESSAGE_WITH_PARAMETERS = "DEFAULT_MESSAGE_WITH_PARAMETERS";
	}

	interface ErrorMessageValues {
		String DEFAULT_MESSAGE = ErrorMessageKeys.DEFAULT_MESSAGE + WhitespaceLiterals.BLANK_SPACE + WhitespaceLiterals.HYPHEN + WhitespaceLiterals.BLANK_SPACE + "Exception occurred";
		String DEFAULT_MESSAGE_WITH_PARAMETERS = ErrorMessageKeys.DEFAULT_MESSAGE_WITH_PARAMETERS + WhitespaceLiterals.BLANK_SPACE + WhitespaceLiterals.HYPHEN + WhitespaceLiterals.BLANK_SPACE + "Exception occurred with parameter1=";
	}

	public enum LoggingPoint {
		START, END, ERROR
	}
}
