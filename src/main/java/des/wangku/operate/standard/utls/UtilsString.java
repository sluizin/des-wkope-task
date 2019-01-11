package des.wangku.operate.standard.utls;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * 字符串工具类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsString {

	/**
	 * 得到字符串的宽度
	 * @param string String
	 * @param font Font
	 * @return int
	 */
	public static int getStringWidth(String string, Font font) {
		int width = 0;
		Shell shell = new Shell();
		Label label = new Label(shell, SWT.NONE);
		label.setFont(font);
		GC gc = new GC(label);
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			width += gc.getAdvanceWidth(c);
		}
		gc.dispose();
		shell.dispose();
		return width;
	}

	/**
	 * 判断关键字是否在数组中，区分大小写
	 * @param key String
	 * @param arrs String[]
	 * @return boolean
	 */
	public static final boolean isExist(String key, String... arrs) {
		if (key == null) return false;
		for (int i = 0; i < arrs.length; i++)
			if (key.equals(arrs[i])) return true;
		return false;
	}

	/**
	 * 判断关键字是否在数组中
	 * @param key int
	 * @param arrs int[]
	 * @return boolean
	 */
	public static final boolean isExist(int key, int... arrs) {
		if (arrs == null) return false;
		for (int i = 0; i < arrs.length; i++)
			if (key == arrs[i]) return true;
		return false;
	}

	/**
	 * 判断字符串中是否含有关键字数组中的元素
	 * @param str String
	 * @param arrs String[]
	 * @return boolean
	 */
	public static final boolean isContain(String str, String... arrs) {
		if (str == null || str.length() == 0) return false;
		for (String key : arrs)
			if (str.indexOf(key) > -1) return true;
		return false;
	}

	/**
	 * 得到字符串第2个以上的位置的下标
	 * @param str String
	 * @param key String
	 * @param point int
	 * @return int
	 */
	public static int getStringPosition(String str, String key, int point) {
		Matcher slashMatcher = Pattern.compile(key).matcher(str); //这里是获取"/"符号的位置
		int mIdx = 0;
		while (slashMatcher.find())
			if ((mIdx++) == point) return slashMatcher.start();
		return -1;
	}

	public static void main(String[] args) {
		String strInput = "3a7s10@5d2a6s17s56;3553";
		String regEx = "[^0-9]";//匹配指定范围内的数字

		//Pattern是一个正则表达式经编译后的表现模式
		Pattern p = Pattern.compile(regEx);

		// 一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
		Matcher m = p.matcher(strInput);

		//将输入的字符串中非数字部分用空格取代并存入一个字符串
		String string = m.replaceAll(" ").trim();

		//以空格为分割符在讲数字存入一个字符串数组中
		String[] strArr = string.split(" ");

		//遍历数组转换数据类型输出
		for (String s : strArr) {
			System.out.println(Integer.parseInt(s));
		}
		String str = " 11第12页 第28位 第31条  ";
		System.out.println("-----" + getNumbers(str, "第\\d+页"));
		System.out.println("-----" + getNumbers(str, "第\\d+条"));
		System.out.println("-----" + getNumbers(str, "第\\d+位"));

		String title = "6月上新 (40)";
		System.out.println("======" + UtilsString.getNumbersInt(title, "\\(", "\\)"));
		String url = "http://www.sohu.com/abc/def";
		String url2 = "https://www.sohu.com\\abc\\def";
		System.out.println("======" + UtilsString.getUrlDomain(url));
		System.out.println("======" + UtilsString.getUrlDomain(url2));
		System.out.println("======" + UtilsString.getUrlDomain("   "));
		String shortstr = "abcdefghijklmnopqrestuvwxyz123456789";
		String result = getShortenedString(shortstr, 19);
		System.out.println("result:" + result);
	}

	/**
	 * 第50(页/条/位)<br>
	 * getNumbersInt(str, "第","页")
	 * @param content String
	 * @param before String
	 * @param after String
	 * @return int
	 */
	public static int getNumbersInt(String content, String before, String after) {
		return getNumbersInt(content, before + "\\d+" + after);
	}

	/**
	 * 第50(页/条/位)<br>
	 * getNumbers(str, "第\\d+页")
	 * @param content String
	 * @param str String
	 * @return int
	 */
	public static int getNumbersInt(String content, String str) {
		String result = getNumbers(content, str);
		if (result.length() == 0) return -1;
		return Integer.parseInt(result);
	}

	/**
	 * 第50(页/条/位)<br>
	 * getNumbers(str, "第\\d+页")
	 * @param content String
	 * @param str String
	 * @return String
	 */
	public static String getNumbers(String content, String str) {
		Pattern pat = Pattern.compile(str);
		Matcher mat = pat.matcher(content);
		if (!mat.find()) return "";
		String con = mat.group(0);
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(con);
		while (matcher.find())
			return matcher.group(0);
		return "";
	}

	/**
	 * 爱站中排名的转换 "第1页 第7位"
	 * @param content String
	 * @return int
	 */
	public static int getNumbersIntTemplateAIZhan(String content) {
		int page = getNumbersInt(content, "第\\d+页");
		int p = getNumbersInt(content, "第\\d+位");
		return (page - 1) * 10 + p;
	}

	/**
	 * 是否存在过滤字符串
	 * @param key String
	 * @param arr String[]
	 * @return boolean
	 */
	public static final boolean isfilterArr(String key, String[] arr) {
		for (int i = 0; i < arr.length; i++)
			if (key.indexOf(arr[i]) > -1) return true;
		return false;
	}

	static final Pattern pattern = Pattern.compile("^[0-9]*$");

	/**
	 * 判断value是否可以转成数值型
	 * @param value String
	 * @return boolean
	 */
	public static final boolean isNumber(String value) {
		if (value == null || value.length() == 0) return false;
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	/**
	 * 从地址中提取 http://www.sohu.com
	 * @param url String
	 * @return String
	 */
	public static final String getUrlDomain(String url) {
		if (url == null || url.trim().length() == 0) return "";
		try {
			String newUrl = url.trim().replaceAll("\\\\", "/");
			URL urln = new URL(newUrl);
			return urln.getProtocol() + "://" + urln.getHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 判断字符串含有多少个字符串，即字符串重复数量
	 * @param str String
	 * @param patternStr String
	 * @return int
	 */
	public static int getCountInnerStr(final String str, final String patternStr) {
		int count = 0;
		final Pattern r = Pattern.compile(patternStr);
		final Matcher m = r.matcher(str);
		while (m.find()) {
			count++;
		}
		return count;
	}

	static final char[] SHORTARR = { '.', '.', '.' };

	/**
	 * 把长字符串缩短成指定长度的字符串，中间以...进行省略
	 * @param sourceStr String
	 * @param maxlen int
	 * @return String
	 */
	public static final String getShortenedString(final String sourceStr, int maxlen) {
		if (sourceStr == null) return sourceStr;
		String str = sourceStr.trim();
		if (str.length() == 0 || str.length() <= maxlen) return str;
		char[] arr = new char[maxlen];
		int p = (maxlen - 3) / 2;
		char[] h = str.substring(0, p).toCharArray();
		char[] e = str.substring(str.length() - (maxlen - (h.length + 3)), str.length()).toCharArray();
		System.out.println("p:" + p);
		System.out.println("e:" + e.length);
		System.arraycopy(h, 0, arr, 0, p);
		System.arraycopy(SHORTARR, 0, arr, p, 3);
		System.arraycopy(e, 0, arr, p + 3, e.length);
		return new String(arr);
	}

	/**
	 * 判断字符串是否含有以 intervalkey 为间隔的字符数组
	 * @param str String
	 * @param arr String
	 * @param intervalkey String
	 * @return boolean
	 */
	public static final boolean isContainSplit(String str, String arr, String intervalkey) {
		if (str == null || str.length() == 0) return false;
		String[] arrs = arr.split(intervalkey);
		for (String e : arrs) {
			if (str.indexOf(e) != -1) return true;
		}
		return false;
	}

	/**
	 * 过滤掉小数点右侧数字。保留完整整数
	 * @param str String
	 * @return String
	 */
	public static final String getLeftPoint(String str) {
		if (str == null) return null;
		int index = str.indexOf(".");
		if (index == -1) return str;
		return str.substring(0, index);
	}
}
