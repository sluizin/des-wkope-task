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
			return false;
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
	 * @param string String
	 * @return boolean
	 */
	public static boolean isDigital(String string) {
		if (string == null || string.length() == 0) return false;
		String regEx1 = "\\d+";
		Pattern p = Pattern.compile(regEx1);
		Matcher m = p.matcher(string);
		if (m.matches()) return true;
		else return false;
	}

	/**
	 * 判断文件是否为csv文件，主要是判断扩展名
	 * @param fileName String
	 * @return String
	 */
	public static boolean isCsvFile(String fileName) {
		return fileName.matches("^.+\\.(?i)(csv)$");
	}
}
