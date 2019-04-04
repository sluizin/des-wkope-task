package des.wangku.operate.standard.utls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsVerification {
	/**
	 * 判断是否是“02-十一月-2006”格式的日期类型
	 * @param str String
	 * @return boolean
	 */
	public static boolean isDate(String str) {
		String[] dataArr = str.split("-");
		if (dataArr.length != 3) return false;
		try {
			int x = Integer.parseInt(dataArr[0]);
			String y = dataArr[1];
			int z = Integer.parseInt(dataArr[2]);
			if (x > 0 && x < 32 && z > 0 && z < 10000 && y.endsWith("月")) return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 判断数值数组是否在最小与最大值之间，有一个元素成功则返回true
	 * @param min int
	 * @param max int
	 * @param arrs int[]
	 * @return boolean
	 */
	public static final boolean isEffective(int min, int max, int... arrs) {
		for (int e : arrs) {
			if (e >= min && e <= max) return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否整型
	 * @param str String
	 * @return boolean
	 */
	public static boolean isDigital(String str) {
		if (str == null || str.length() == 0) return false;
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(str);
		if (m == null) return false;
		return m.matches();
	}

	/**
	 * 利用正则表达式判断字符串是否是整数型数字
	 * @param str String
	 * @return boolean
	 */
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) { return false; }
		return true;
	}

	/**
	 * 从字符串提取出第一个"[****]"中括号中的字符串<br>
	 * 如果没有查到，则返回null
	 * @param content String
	 * @return String
	 */
	public static final String getStringID(String content) {
		Pattern pattern = Pattern.compile("\\[[\\s\\S]+\\]");
		Matcher isNum = pattern.matcher(content);
		if (isNum.find()) {
			String str = isNum.group(0);
			return str.substring(1, str.length() - 1);
		}
		return null;
	}

	public static void main(String[] args) {
		String content = "abc[p10中a]";
		System.out.println("Hello World!:" + getStringID(content));
	}

	/**
	 * 判断文件是否为csv文件，主要是判断扩展名
	 * @param fileName String
	 * @return String
	 */
	public static boolean isCsvFile(String fileName) {
		return fileName.matches("^.+\\.(?i)(csv)$");
	}

	/**
	 * 判断字符串是否为QQ
	 * @param qq String
	 * @return boolean
	 */
	public final static boolean isQQ(final String qq) {
		return Pattern.matches("^\\d{5,10}$", qq);
	}

	/**
	 * 判断字符串是否是IP
	 * @param ip String
	 * @return boolean
	 */
	public final static boolean isIp(final String ip) {// 判断是否是一个IP
		if (ip == null || !ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) return false;
		final String s[] = ip.split("\\.");
		return (Integer.parseInt(s[0]) < 255 && Integer.parseInt(s[1]) < 255 && Integer.parseInt(s[2]) < 255 && Integer.parseInt(s[3]) < 255) ? true : false;
	}

	/**
	 * 判断是否为浮点数，包括double和float
	 * @param doubleStr String
	 * @return boolean
	 */
	public final static boolean isDouble(final String doubleStr) {
		return Pattern.compile("^[-\\+]?[.\\d]*$").matcher(doubleStr).matches();
	}

	/**
	 * 判断是否是6位的颜色值
	 * @param color String
	 * @return boolean
	 */
	public final static boolean isColor(String color) {
		if (color == null) return false;
		color = color.trim();
		if (color.length() != 6 && color.length() != 7) return false;
		if (color.length() == 7) {
			if (color.charAt(0) != '#') return false;
			color = color.substring(1);
		}
		final char[] IsColorChar = color.toCharArray();
		for (int i = 0; i < 6; i++) {
			int v = (int) IsColorChar[i];
			/* 0-9 a-f A-F */
			if ((v >= 48 && v <= 57) || (v >= 97 && v <= 102) || (v >= 65 && v <= 70)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

}
