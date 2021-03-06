package org.common.logging.appender;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.helpers.LogLog;

public class HostFileAppender extends FileAppender {

	private static final String HOSTNAME_VALUE = "HOSTNAME_VALUE";

	public HostFileAppender() {
		super();
		try {
			System.setProperty(HOSTNAME_VALUE, Inet4Address.getLocalHost().getHostName());
		} catch(final UnknownHostException unknownHostException) {
			LogLog.error("Error getting the HostName of the env", unknownHostException);
		}
	}
}