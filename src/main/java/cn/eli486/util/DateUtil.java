package cn.eli486.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author eli
 */
public class DateUtil {
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
		String date = getBeforeDay(days, pattern, new Date());
		return date.replaceAll("/", "%2F");
	}
	
	public static void main(String[] args) {
		String date = DateUtil.getBeforeDayAgainstToday(-1, "yyyyMMdd");
		System.out.println(date);
	}
}
