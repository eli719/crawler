package cn.eli486.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author eli
 */
public class DateUtil {
	public static String getLastDay(String pattern){
		return getBeforeDayAgainstToday(1, pattern);
	}
	public static String getDateString(Date date, String pattern) {
		return (new SimpleDateFormat(pattern)).format(date);
	}
	
	public static String getBeforeDay(int days, String pattern, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_YEAR) - days;
		cal.set(Calendar.DAY_OF_YEAR, day);
		return getDateString(cal.getTime(), pattern);
	}
	public static String getBeforeDayAgainstToday(int days, String pattern) {
		return getBeforeDay(days, pattern, new Date());
	}
	
}
