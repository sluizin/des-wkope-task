package des.wangku.operate.standard.utls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期型工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsDate {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsDate.class);
	/** yyyyMMddHHmmss */
	public static final SimpleDateFormat ACC_DateFormatBase = new SimpleDateFormat("yyyyMMddHHmmss");
	/** yyyy-MM-dd HH:mm:ss */
	public static final SimpleDateFormat ACC_DateFormatStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/** yyyy-MM-dd */
	public static final SimpleDateFormat ACC_DateFormat = new SimpleDateFormat("yyyy-MM-dd");
	/** yyyy-MM */
	public static final SimpleDateFormat ACC_DateFormatMonth = new SimpleDateFormat("yyyy-MM");
	/** HH:mm:ss */
	public static final SimpleDateFormat ACC_DateFormatTime = new SimpleDateFormat("HH:mm:ss");
	static {
		ACC_DateFormatBase.setLenient(false);
		ACC_DateFormatStandard.setLenient(false);
		ACC_DateFormat.setLenient(false);
		ACC_DateFormatMonth.setLenient(false);
	}
	/**
	 * 得到当前时间 yyyy-MM-dd HH:mm:ss
	 * @return String
	 */
	public final static String getDateTimeNow() {
		return ACC_DateFormatStandard.format(new Date());
	}
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
	 * @param time1 String
	 * @param time2 String
	 * @return boolean
	 */
	public static boolean isEffectiveStandard(String time1, String time2) {
		Date d=new Date();
		return isEffective(ACC_DateFormatStandard,ACC_DateFormatStandard.format(d),time1,time2);
	}
	/**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致<br>
	 * 忽略开始与结束时间的位置
	 * @param sdf SimpleDateFormat
	 * @param nowTime String
	 * @param time1 String
	 * @param time2 String
	 * @return boolean
	 */
	public static boolean isEffective(SimpleDateFormat sdf, String nowTime, String time1, String time2) {
		if (sdf == null) return false;
		Date date0 = null;
		Date date1 = null;
		Date date2 = null;
		try {
			date0 = (Date) sdf.parse(nowTime);
		} catch (ParseException e) {
		}
		try {
			date1 = (Date) sdf.parse(time1);
		} catch (ParseException e) {
		}
		try {
			date2 = (Date) sdf.parse(time2);
		} catch (ParseException e) {
		}
		return isEffective(date0, date1, date2);
	}

	/**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致<br>
	 * 忽略开始与结束时间的位置
	 * @param nowTime Date 当前时间
	 * @param time1 Date 时间端
	 * @param time2 Date 时间端
	 * @return boolean
	 */
	public static boolean isEffective(Date nowTime, Date time1, Date time2) {
		if (time1 == null && time2 == null) return true;
		if (nowTime == null) return false;
		long t = nowTime.getTime();
		long t1 = (time1 == null ? 0 : time1.getTime());
		long t2 = (time2 == null ? 0 : time2.getTime());
		long min = t1, max = t2;
		if (t1 > t2) {
			min = t2;
			max = t1;
		}
		if (min == 0 && max > 0) return t <= max;
		if (min > 0 && max == 0) return t >= min;
		if (min > 0 && max > 0) return t >= min && t <= max;
		return false;
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

	public static final boolean testSimpleDateFormat(SimpleDateFormat t, String date) {
		try {
			t.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}

	}

	/**
	 * 判断字符串是否为日期型 如:2018-09-12<br>
	 * yyyy-MM-dd : 0<br>
	 * yyyy-MM : 1<br>
	 * +N :2
	 * @param date String
	 * @return int
	 */
	public static final int isType(String date) {
		if (testSimpleDateFormat(ACC_DateFormat, date)) return 0;
		if (testSimpleDateFormat(ACC_DateFormatMonth, date)) return 1;
		if (UtilsRegular.getRegExBoolean(date, DateInterruptedPattern)) return 2;
		return -1;
	}

	/**
	 * 判断字符串是否为日期型 如:2018-09-12<br>
	 * type:0 -- 即默认为yyyy-MM-dd<br>
	 * type:1 -- 即为yyyy-MM
	 * @param date String
	 * @param type int
	 * @return boolean
	 */
	public static final boolean isSafeDate2(String date, int type) {
		try {
			if (type == 0) {
				ACC_DateFormat.parse(date);
			} else {
				ACC_DateFormatMonth.parse(date);
			}
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * 依据date的格式，选用不同的 SimpleDateFormat <br>
	 * 如格式不对，则返回null
	 * @param date String
	 * @return SimpleDateFormat
	 */
	public static final SimpleDateFormat getSimpleDateFormat(String date) {
		int type = isType(date);
		if (type == 0) return ACC_DateFormat;
		if (type == 1) return ACC_DateFormatMonth;
		return null;
	}

	/**
	 * 通过date得到Calendar
	 * @param date String
	 * @return Calendar
	 */
	public static final Calendar getCalendar(String date) {
		try {
			SimpleDateFormat t = getSimpleDateFormat(date);
			if (t == null) return null;
			int type = isType(date);
			if (type == 1) date = date + "-01";
			Date date2 = t.parse(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date2);
			return calendar;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过日期型字符串得到当天的日期增量的日期字符串
	 * @param date String
	 * @return String
	 */
	public static final String getDateAddDay(String date) {
		int type = isType(date);
		if (type != 2) return null;
		String symbol = UtilsRegular.getRegExContent(date, DateInterruptedPattern, 1);
		String value = UtilsRegular.getRegExContent(date, DateInterruptedPattern, 2);
		int v = Integer.parseInt(value);
		if ("-".equals(symbol)) v = -v;
		Date d = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.DATE, v);
		return ACC_DateFormat.format(calendar.getTime());
	}

	/**
	 * 通过日期型字符串得到当月的第一天的日期字符串
	 * @param date String
	 * @return String
	 */
	public static final String getDateMonthFirstDay(String date) {
		Calendar calendar = getCalendar(date);
		if (calendar == null) return null;
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int type = isType(date);
		if (type == 1) date = date + "-01";
		SimpleDateFormat t = getSimpleDateFormat(date);
		if (t == null) return null;
		String firstday = t.format(calendar.getTime());
		return firstday;
	}

	/**
	 * 通过日期型字符串得到当月的最后一天的日期字符串
	 * @param date String
	 * @return String
	 */
	public static final String getDateMonthEndDay(String date) {
		Calendar calendar = getCalendar(date);
		if (calendar == null) return null;
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		int type = isType(date);
		if (type == 1) date = date + "-01";
		SimpleDateFormat t = getSimpleDateFormat(date);
		if (t == null) return null;
		String lastday = t.format(calendar.getTime());
		return lastday;
	}

	/**
	 * 判断字符串是否为日期格式
	 * @param dateStr String
	 * @return boolean
	 */
	public static final boolean isDateFormat(String dateStr) {
		return getDateArray(dateStr) != null;
	}

	/**
	 * 把日期字符串转成区间<br>
	 * <code>
	 * 2018-12             转成  2018-12-01 | 2018-12-31<br>
	 * 2018-12-15          转成  2018-12-15 | 2018-12-15<br>
	 * 2018-12:2018-12     转成  2018-12-01 | 2018-12-31<br>
	 * 2018-12-15:2018-12  转成  2018-12-15 | 2018-12-31<br>
	 * 2018-12:2018-12-15  转成  2018-12-01 | 2018-12-15<br>
	 * </code>
	 * @param dateStr String
	 * @return String[]
	 */
	public static final String[] getDateArray(String dateStr) {
		String[] result = { "", "" };
		int type;
		if (dateStr.indexOf(':') > 0) {
			String[] arr = dateStr.split(":");
			if (arr.length <= 1) return null;
			String date1 = arr[0];
			String date2 = arr[1];
			type = isType(date1);
			if (type == -1) return null;
			if (type == 1) date1 = getDateMonthFirstDay(date1);
			if (type == 2) date1 = getDateAddDay(date1);
			type = isType(date2);
			if (type == -1) return null;
			if (type == 1) date2 = getDateMonthEndDay(date2);
			if (type == 2) date2 = getDateAddDay(date2);

			result[0] = date1;
			result[1] = date2;
			if (getCompareDate(date1, date2) < 0) {
				result[0] = date2;
				result[1] = date1;
			}
			return result;
		}
		type = isType(dateStr);
		if (type == 0) {
			result[0] = dateStr;
			result[1] = dateStr;
			return result;
		}
		if (type == 1) {
			result[0] = getDateMonthFirstDay(dateStr);
			result[1] = getDateMonthEndDay(dateStr);
			return result;
		}
		if (type == 2) {
			String date = getDateAddDay(dateStr);
			result[0] = date;
			result[1] = date;
			return result;
		}
		return null;
	}

	/**
	 * 日期型数据比较
	 * @param date1 String
	 * @param date2 String
	 * @return int
	 */
	public static final int getCompareDate(String date1, String date2) {
		if (date1 == null || date2 == null || date1.equals(date2)) return 0;
		try {
			if (isType(date1) == 1) date1 += "-01";
			if (isType(date2) == 1) date2 += "-01";
			Date timestamp1 = ACC_DateFormat.parse(date1);
			Date timestamp2 = ACC_DateFormat.parse(date2);
			if (timestamp1.before(timestamp2)) {
				return 1;
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * SWT中DateTime控件，得到Date日期型数据
	 * @param dt DateTime
	 * @return Date
	 */
	public static final Date getDate(DateTime dt) {
		if (dt == null) return null;
		try {
			int style = dt.getStyle();
			if (UtilsShiftCompare.isCompare(style, SWT.CALENDAR)) {
				String strDate = dt.getYear() + "-" + dt.getMonth() + "-" + dt.getDay() + " " + dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
				Date date = ACC_DateFormatStandard.parse(strDate);
				return date;
			}
			if (UtilsShiftCompare.isCompare(style, SWT.DATE)) {
				String strDate = dt.getYear() + "-" + dt.getMonth() + "-" + dt.getDay();
				Date date = ACC_DateFormat.parse(strDate);
				return date;
			}
			if (UtilsShiftCompare.isCompare(style, SWT.TIME)) {
				String strDate = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
				Date date = ACC_DateFormatTime.parse(strDate);
				return date;
			}
			return null;
		} catch (ParseException px) {
			px.printStackTrace();
			return null;
		}
	}

	/** 日期间断正则判断 如 +25,-2,+0,-0 */
	public static final String DateInterruptedPattern = "^[\\s\\S]*([+-]+)\\s*(\\d+)\\s*$";

	public static void main(String[] args) {
		String str;
		str = "2018-12-01";
		System.out.println(str + ":" + getDateMonthFirstDay(str));
		str = "2018-7-1";
		System.out.println(str + ":" + getDateMonthEndDay(str));
		str = "2018-4-12";
		System.out.println(str + ":" + getDateMonthEndDay(str));
		str = "2018-11";
		System.out.println(str + ":" + getDateMonthEndDay(str) + "\t" + getDateMonthFirstDay(str));
		str = "2018-12";
		System.out.println(str + ":" + getDateMonthEndDay(str) + "\t" + getDateMonthFirstDay(str));
		str = "2018-13";
		System.out.println(str + ":" + getDateMonthEndDay(str));
	}
}
