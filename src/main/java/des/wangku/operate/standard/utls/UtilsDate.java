package des.wangku.operate.standard.utls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 日期型工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsDate {
	/** 日志 */
	static Logger logger = Logger.getLogger(UtilsDate.class);
	/** yyyyMMddHHmmss */
	static final SimpleDateFormat ACC_DateFormatBase = new SimpleDateFormat("yyyyMMddHHmmss");
	static final SimpleDateFormat ACC_DateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 得到当前日期的某种格式
	 * @param dateform String "yyyyMMddHHmmss"
	 * @return String "20180601092822"
	 */
	public final static String getDateTimeNow(final String dateform) {
		return (new SimpleDateFormat(dateform)).format(new Date());
	}

	/**
	 * -1:null
	 * 0:空
	 * 1:乱码
	 * 2:之前(最后一位为 前 字)
	 * 3:含有有效的日期，否则返回乱码(1)
	 * @param value String
	 * @return int
	 */
	public static final int isDateState(String value) {
		if (value == null) return -1;
		value = value.trim();
		if (value.length() == 0) return 0;
		String date = getDateString(value);
		if (date != null) return 3;
		if (value.substring(value.length() - 1).equals("前")) return 2;
		return 1;
	}

	/**
	 * 判断字符串中是否有效的日期，如果有则返回日期字符串2014-12-1否则返回null
	 * @param value String
	 * @return String
	 */
	public static String getDateString(String value) {
		if (value == null || value.length() == 0) return null;
		String date = null;
		Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
		Matcher m = p.matcher(value);
		if (m.find()) date = combination(m.group(1), m.group(2), m.group(3));
		Pattern p2 = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})");
		Matcher m2 = p2.matcher(value);
		if (m2.find()) date = combination(m2.group(1), m2.group(2), m2.group(3));
		if (date == null) return null;
		try {
			Date date1 = (Date) ACC_DateFormat.parse(date);
			if (date.equals(ACC_DateFormat.format(date1))) return date;
			return null;
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 以-合成日期型字符串
	 * @param year String
	 * @param month String
	 * @param day String
	 * @return String
	 */
	public static final String combination(String year, String month, String day) {
		if (month.length() == 1) month = "0" + month;
		if (day.length() == 1) day = "0" + day;
		return year + "-" + month + "-" + day;
	}

	/**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致<br>
	 * 忽略开始与结束时间的位置
	 * @param time1 String
	 * @param time2 String
	 * @return boolean
	 */
	public static boolean isEffectiveDateTime(String time1, String time2) {
		try {
			Date nowDate = new Date();
			Date date1 = (Date) ACC_DateFormat.parse(time1);
			Date date2 = (Date) ACC_DateFormat.parse(time2);
			return isEffectiveDateTime(nowDate, date1, date2);
		} catch (ParseException e) {
			return false;
		}

	}

	/**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致<br>
	 * 忽略开始与结束时间的位置
	 * @param nowTime String
	 * @param time1 String
	 * @param time2 String
	 * @return boolean
	 */
	public static boolean isEffectiveDateTime(String nowTime, String time1, String time2) {
		try {
			Date nowDate = (Date) ACC_DateFormat.parse(nowTime);
			Date date1 = (Date) ACC_DateFormat.parse(time1);
			Date date2 = (Date) ACC_DateFormat.parse(time2);
			return isEffectiveDateTime(nowDate, date1, date2);
		} catch (ParseException e) {
			return false;
		}

	}

	/**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致<br>
	 * 忽略开始与结束时间的位置
	 * @param nowTime Date 当前时间
	 * @param time1 Date 时间端
	 * @param time2 Date 时间端
	 * @return boolean
	 */
	public static boolean isEffectiveDateTime(Date nowTime, Date time1, Date time2) {
		if (nowTime.getTime() == time1.getTime() || nowTime.getTime() == time2.getTime()) return true;
		long t = nowTime.getTime();
		long t1 = time1.getTime();
		long t2 = time2.getTime();
		long min = t1, max = t2;
		if (t1 > t2) {
			min = t2;
			max = t1;
		}
		return t >= min && t <= max;
		/*
		 * Calendar date = Calendar.getInstance();
		 * date.setTime(nowTime);
		 * Calendar begin = Calendar.getInstance();
		 * begin.setTime(time1);
		 * Calendar end = Calendar.getInstance();
		 * end.setTime(time2);
		 * if ((date.after(begin) && date.before(end)) || (date.after(end) && date.before(begin))) return true;
		 * else return false;
		 */
	}

	static final long ACC_DaySection = 1000 * 60 * 60 * 24;
	static final long ACC_HourSection = 1000 * 60 * 60;
	static final long ACC_MinutesSection = 1000 * 60;
	static final long ACC_SecondsSection = 1000;
	static final char ACC_AutoNumberHead = '0';

	/**
	 * 经过的时间长度。 如输入 ：65212000 输出：XX天XX小时XX分钟XX秒
	 * @param mss long
	 * @return String
	 */
	public final static String formatDuring(final long mss) {
		long days = mss / ACC_DaySection;
		long hours = (mss % ACC_DaySection) / ACC_HourSection;
		long minutes = (mss % ACC_HourSection) / ACC_MinutesSection;
		long seconds = (mss % ACC_MinutesSection) / ACC_SecondsSection;
		StringBuilder out = new StringBuilder(30);
		if (days > 0) {
			if (days < 10) out.append(ACC_AutoNumberHead);
			out.append(days);
			out.append("天");
		}
		if (hours > 0) {
			if (hours < 10) out.append(ACC_AutoNumberHead);
			out.append(hours);
			out.append("小时");
		}
		if (minutes > 0) {
			if (minutes < 10) out.append(ACC_AutoNumberHead);
			out.append(minutes);
			out.append("分钟");
		}
		if (seconds > 0) {
			if (seconds < 10) out.append(ACC_AutoNumberHead);
			out.append(seconds);
			out.append("秒");
		}
		return out.toString();
	}
}
