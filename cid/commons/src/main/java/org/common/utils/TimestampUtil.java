package org.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class TimestampUtil {

	public static final String DD_MM_YYY = "dd/MM/yyyy";
	public static final String DD_MM_YYY_HH_MM_SS_SSS = "dd/MM/yyyy hh:mm:ss.SSS";

	private TimestampUtil() {
	}

	public static Timestamp getCurentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static Timestamp getCurentPlusAdditionalTimestamp(final long additionalTime) {
		return new Timestamp(System.currentTimeMillis() + additionalTime);
	}

	public static int getYear(final Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
		return calendar.get(Calendar.YEAR);
	}

	public static Timestamp getTimestamp(String string, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date parsedDate = dateFormat.parse(string);
		return new Timestamp(parsedDate.getTime());
	}
}