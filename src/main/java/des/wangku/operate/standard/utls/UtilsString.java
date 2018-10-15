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
	 * 判断关键字是否在数组中
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
		Pattern p;
		Matcher m;
		p = Pattern.compile(regEx1);
		m = p.matcher(string);
		if (m.matches()) return true;
		else return false;
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
}
