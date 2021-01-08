package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串工具类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsString {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsString.class);

	/**
	 * 把字符串中，所有在字符串末尾的数组中含有的全部清除<br>
	 * ("a.0bc.00.0.000.0.0.0.0",".0",".00",".000") 结果:a.0bc ,
	 * @param source String
	 * @param arrs String[]
	 * @return String
	 */
	public static final String removeEndsStr(String source, String... arrs) {
		if (source == null || source.length() == 0 || arrs.length == 0) return source;
		String key = null;
		for (String e : arrs)
			if (source.endsWith(e)) {
				key = e;
				break;
			}
		if (key == null) return source;
		int index = source.lastIndexOf(key);
		return removeEndsStr(source.substring(0, index), arrs);
	}

	/**
	 * 过滤字符串中的所有中括号
	 * @param word String
	 * @return String
	 */
	public static final String splitWordMiddleBrackets(String word) {
		if (word == null || word.length() == 0) return word;
		return word.replaceAll("\\[[^]]*\\]", "");
	}

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
	 * 判断关键字是否含有在数组中，区分大小写
	 * @param key String
	 * @param arrs String[]
	 * @return boolean
	 */
	public static final boolean isExistIndexOf(String key, String... arrs) {
		if (key == null) return false;
		for (String e : arrs)
			if (e != null && e.indexOf(key) > -1) return true;
		return false;
	}

	/**
	 * 判断关键字是否含有Set数组中，区分大小写
	 * @param key String
	 * @param set Set&lt;String&gt;
	 * @return boolean
	 */
	public static final boolean isExistIndexOf(String key, Set<String> set) {
		if (key == null) return false;
		for (String e : set)
			if (e != null && e.indexOf(key) > -1) return true;
		return false;
	}

	/**
	 * 判断关键字是否在数组中，区分大小写
	 * @param key String
	 * @param arrs String[]
	 * @return boolean
	 */
	public static final boolean isExist(String key, String... arrs) {
		if (key == null) return false;
		for (String e : arrs)
			if (key.equals(e)) return true;
		return false;
	}

	/**
	 * 判断数组中的多个字符串是否在line中出现(indexof)，区分大小写
	 * @param line String
	 * @param arrs String[]
	 * @return boolean
	 */
	public static final boolean isExistKey(String line, String... arrs) {
		if (line == null) return false;
		for (String e : arrs)
			if (line.indexOf(e) > -1) return true;
		return false;
	}

	/**
	 * 判断 数值 是否在数组中
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

	public static void main2(String[] args) {
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
		System.out.println("======" + UtilsReadURL.getUrlDomain(url));
		System.out.println("======" + UtilsReadURL.getUrlDomain(url2));
		System.out.println("======" + UtilsReadURL.getUrlDomain("   "));
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

	private static final char ACC_ShowStringkeySplit = '\t';

	/**
	 * 多个对象整合成一个字符串，以\t以间隔，可以含有多个数组
	 * @param arrs T[]
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static final <T> String showString(T... arrs) {
		StringBuilder sb = new StringBuilder();
		for (T e : arrs) {
			if (e == null) {
				if (sb.length() != 0) sb.append(ACC_ShowStringkeySplit);
				sb.append("null");
				continue;
			}
			if (e.getClass().isArray()) {
				Object[] arr = (Object[]) e;
				sb.append(showString(arr));
			} else {
				if (sb.length() != 0) sb.append(ACC_ShowStringkeySplit);
				sb.append(e.toString());
			}
		}
		return sb.toString();
	}

	/**
	 * 数组的转向
	 * @param arrs T[]
	 * @return T[]
	 */
	public static final <T> T[] reverseOrder(T[] arrs) {
		int len = arrs.length;
		if (len < 2) return arrs;
		for (int i = 0, size = len / 2; i < size; i++) {
			T obj = arrs[i];
			int to = len - 1 - i;
			arrs[i] = arrs[to];
			arrs[to] = obj;
		}
		return arrs;
	}

	/**
	 * 数组按长度从小到大排序
	 * @param arrs String[]
	 * @return String[]
	 */
	public static final String[] sortStringArrayByLenReverse(String... arrs) {
		String[] arr = sortStringArrayByLen(arrs);
		return reverseOrder(arr);
	}

	/**
	 * 数组按长度从大到小排序
	 * @param arrs String[]
	 * @return String[]
	 */
	public static final String[] sortStringArrayByLen(String... arrs) {
		int len = arrs.length;
		if (len <= 1) return arrs;
		for (int i = 0, end = len - 1; i < end; i++) {
			String key = arrs[i];
			int index = getIndexMaxLength(arrs, key == null ? -1 : key.length(), i + 1);
			if (index > -1) {
				arrs[i] = arrs[index];
				arrs[index] = key;
			}
		}
		return arrs;
	}

	/**
	 * 得到数组中长度最长单元的下标
	 * @param arr Object[]
	 * @param maxLen int
	 * @param start int
	 * @return int
	 */
	public static final int getIndexMaxLength(Object[] arr, int maxLen, int start) {
		return getIndexMaxLength(arr, maxLen, start, arr.length - 1);
	}

	/**
	 * 得到数组中长度最长单元的下标
	 * @param arr Object[]
	 * @param maxLen int
	 * @param start int
	 * @param end int
	 * @return int
	 */
	public static final int getIndexMaxLength(Object[] arr, int maxLen, int start, int end) {
		int len = arr.length;
		if (start < 0 || start >= len) start = 0;
		if (end >= len) end = len - 1;
		int index = -1;
		for (int i = start; i <= end; i++) {
			Object v = arr[i];
			if (v == null) continue;
			if (v.toString().length() <= maxLen) continue;
			index = i;
			maxLen = v.toString().length();
		}
		return index;
	}

	/**
	 * 格式化正整数
	 * @param val int
	 * @param size int
	 * @return String
	 */
	static final String formatNumber(int val, int size) {
		if (val < 0) return val + "";
		String s = String.valueOf(val);
		if (s.length() >= size) return s;
		char[] arr = s.toCharArray();
		char[] newarr = new char[size];
		int start = size - s.length();
		for (int i = 0; i < start; i++)
			newarr[i] = '0';
		System.arraycopy(arr, 0, newarr, start, s.length());
		;
		return new String(newarr);
	}

	/**
	 * 识别[]之间的数据，并得到数组
	 * @param key String
	 * @return String[]
	 */
	static final String[] keySplit(String key) {
		List<String> list = new ArrayList<>();
		String[] arr = {};
		String val = key.substring(1, key.length() - 1);
		if (val.length() == 0) return arr;
		String[] keys = val.split(",");
		for (String e : keys) {
			int index = e.indexOf('-');
			if (index == 0) continue;
			if (index == -1) {
				list.add(e);
				continue;
			}
			String[] cuts = e.split("-");
			String c1 = cuts[0];
			String c2 = cuts[1];
			/* 数字之间进行泛型组合 */
			if (UtilsVerification.isNumeric(c1) && UtilsVerification.isNumeric(c2)) {
				int first = Integer.valueOf(c1);
				int end = Integer.valueOf(c2);
				boolean isFormat = false;/* 数字格式化 */
				if (c1.length() == c2.length()) isFormat = true;
				if (first > end) {
					for (int i = first; i >= end; i--)
						list.add(isFormat ? formatNumber(i, c1.length()) : "" + i);
				} else {
					for (int i = first; i <= end; i++)
						list.add(isFormat ? formatNumber(i, c1.length()) : "" + i);
				}
				continue;
			}
			/* 字符之间进行泛型组合 */
			if (c1.length() == 1 && c2.length() == 1) {
				char first = c1.charAt(0);
				char end = c2.charAt(0);
				if (first > end) {
					for (int i = first; i >= end; i--)
						list.add("" + (char) i);
				} else {
					for (int i = first; i <= end; i++)
						list.add("" + (char) i);
				}
			}
		}
		list = UtilsList.distinct(list);/* 去重 */
		return list.toArray(arr);
	}

	/**
	 * 把字符串http://www.books.net/fenlei/[a-c]_[a,1-2].html转成不同组合
	 * @param str String
	 * @return String[]
	 */
	public static final String[] splitString(String str) {
		String[] arr = {};
		if (str == null || str.trim().length() == 0) return arr;
		String newstr = str.trim();
		List<String> list = new ArrayList<>();
		combinationString(list, newstr);
		list = UtilsList.distinct(list);/* 去重 */
		return list.toArray(arr);
	}

	/** 正则 间括号 */
	static final String ACC_Combination = "\\[[^]]*\\]";

	/**
	 * 把字符串http://www.books.net/fenlei/[a-c]_[a,1-2].html转成不同组合并放在List中<br>
	 * 使用递归调用<br>
	 * @param list List&lt;String&gt;
	 * @param str String
	 */
	private static final void combinationString(List<String> list, String str) {
		final Pattern r = Pattern.compile(ACC_Combination);
		final Matcher m = r.matcher(str);
		if (!m.find()) {
			list.add(str);
			return;
		}
		String key = m.group();
		String[] arr = keySplit(key);
		for (String e : arr) {
			String newString = m.replaceFirst(e);
			combinationString(list, newString);
		}
	}

	/**
	 * 得到key字符串在content中的重复率，即content中有多少key中的字符<br>
	 * 如返回80，则key6中有百分之80的字符出现在content中
	 * @param content String
	 * @param key String
	 * @return String
	 */
	public static final int repetitionRate(String content, String key) {
		if (content == null || content.length() == 0) return 0;
		if (key == null || key.length() == 0) return 0;
		int len = key.length();
		int sort = 0;
		for (int i = 0; i < len; i++) {
			char c = key.charAt(i);
			if (content.indexOf(c) > -1) sort++;
		}
		float v = (float) sort / (float) len * 100;
		//System.out.println(sort+"="+len+"\t"+v);
		return (int) (v);
	}

	/**
	 * 截断字符串，如果没有找到，则返回null，如果找到多个，则返回第1个
	 * @param source String
	 * @param start String
	 * @param end String
	 * @return String
	 */
	public static final String cutString(String source, String start, String end) {
		String[] arr=cutStrings(source,start,end);
		if(arr.length==0)return null;
		return arr[0];
	}
	/**
	 * 截取字符串，产生多个字符串，为开始与结尾之间的字符串<br>
	 * 如果开始为空串，则以最始点为开始<br>
	 * 如果结尾为空串，则以最后点为结尾
	 * @param source String
	 * @param start String
	 * @param end String
	 * @return String[]
	 */
	public static final String[] cutStrings(String source, String start, String end) {
		String[] arrs = {};
		if (source == null || source.length() == 0) return arrs;
		if (start == null || end == null) return arrs;
		if (start.length()==0 && end.length() == 0) return arrs;
		String cut=null;
		if(start.length()==0) {
			int index=source.indexOf(end);
			if(index==-1)return arrs;
			cut = source.substring(0, index);
		}
		if(end.length()==0) {
			int index=source.indexOf(start);
			if(index==-1)return arrs;
			cut = source.substring(index+start.length(), source.length());	
		}
		if(cut!=null) {
			String[] a= {cut};
			return a;
		}
		int index1 = 0, index2 = 0;
		List<String> list = new ArrayList<>();
		while ((index1 = source.indexOf(start, index1)) > -1) {
			index2 = source.indexOf(end, index1);
			if (index2 == -1) break;
			cut = source.substring(index1 + start.length(), index2);
			int lastStart = cut.lastIndexOf(start);
			if (lastStart > -1) cut = cut.substring(lastStart + start.length());
			list.add(cut);
			index1 = index2 + end.length();
		}
		if (list.size() == 0) return arrs;
		return list.toArray(arrs);
	}

	public static String removeStr(String src, String str) {
		if (src == null || str == null) return src;
		int idx = src.indexOf(str);
		if (idx == -1) return src;
		int pst = 0;
		char[] cs = src.toCharArray();
		char[] rs = new char[src.length() - str.length()];
		for (int i = 0; i < cs.length; i++) {
			if (i >= idx && i < idx + str.length()) continue;
			rs[pst] = cs[i];
			pst++;
		}
		return new String(rs);
	}

	public static String replaceStr(String src, String target, String replacement) {
		if (src == null || target == null || replacement == null) return src;
		int idx = src.indexOf(target);
		if (idx == -1) return src;
		int pst = 0;
		char[] cs = src.toCharArray();
		char[] rs = new char[src.length() - target.length() + replacement.length()];
		for (int i = 0; i < cs.length; i++) {
			if (i == idx) {
				for (char c : replacement.toCharArray()) {
					rs[pst] = c;
					pst++;
				}
				continue;
			}
			if (i > idx && i < idx + target.length()) continue;
			rs[pst] = cs[i];
			pst++;
		}
		return new String(rs);
	}

	/**
	 * @param src
	 * @param target
	 * @param replacement
	 * @return
	 */
	public static String replaceAllStr(String src, String target, String replacement) {
		if (src == null || target == null || replacement == null) return src;
		int idx = src.indexOf(target);
		if (idx == -1) return src;
		int pst = 0;
		char[] cs = src.toCharArray();
		char[] rs = new char[src.length() - target.length() + replacement.length()];
		for (int i = 0; i < cs.length; i++) {
			if (i == idx) {
				for (char c : replacement.toCharArray()) {
					rs[pst] = c;
					pst++;
				}
				continue;
			}
			if (i > idx && i < idx + target.length()) continue;
			rs[pst] = cs[i];
			pst++;
		}
		return replaceAllStr(new String(rs), target, replacement);
	}

	public static void main(String[] args) {
		String content="aa<b>bb<b>cc</b>dd<b>ee</b>ff<b>xx<b>hh</b>gg";
		String[] arr=cutStrings(content,"<b>","</b>");
		for(String e:arr) {
			System.out.println("e:"+e);
		}
		String str="abcd";
		System.out.println("result:"+str.substring(2,4));
		
		/*
		String content = "abcaa" + ACC_ENTER + "bbccaaee" + ACC_ENTER + "aaaaa";
		String val = ACC_ENTER;
		System.out.println("count:" + UtilsRegular.getPatternCount(content, val));
	*/
		/*
		 * String content="aa<font>bb<font>cc</font>dd</font>";
		 * String str=cutString(content,"<font>","</font>");
		 * System.out.println("::"+str);
		 * content="aabbccdeabefhjuabcdefzklaccggkfg";
		 * String key="abcdefzklaccgg";
		 * System.out.println("repetitionRate:"+repetitionRate(content,key));
		 */
		/*
		 * String s ="\\u79fb\\u52a8\\u4e92\\u8054&gt;&amp;\\u7f51\\u5e94\\u7528&ldquo;";
		 * System.out.println(s);
		 * System.out.println(StringEscapeUtils.unescapeJava(s));
		 * System.out.println(StringEscapeUtils.unescapeHtml3(s));
		 * System.out.println(StringEscapeUtils.unescapeHtml4(s));
		 * String str = "http://www.17books.net/fenlei/[a,009-100].html";
		 * String[] arrs = splitString(str);
		 * for (int i = 0; i < arrs.length; i++)
		 * System.out.println(i + ":" + arrs[i]);
		 * String[] ar=keySplit("[110,a-b,c,2-4,30-1,c-a]");
		 * for(int i=0;i<ar.length;i++)
		 * System.out.println(i+":"+ar[i]);
		 */

		/*
		 * String[] arrs=UtilsConstsRequestHeader.User_Agent;
		 * String[] arr=sortStringArrayByLenReverse(arrs);
		 * for(String e:arr) {
		 * System.out.println("\t\t\""+e+"\",");
		 * }
		 * Object[] ar= {"ee",null};
		 * Object[] arr= {5,"abc",ar,'c',"txt",null,15.2,10};
		 * System.out.println(showString(arr));
		 * String[] arrs= {null,"abc",null,"00",null,"1234","5566788","0","abcdef"};
		 * System.out.println(showString(arrs));
		 * String[] arrsa=sortStringArrayByLen(arrs);
		 * System.out.println(showString(arrsa));
		 * System.out.println();
		 * String[] arrs2=sortStringArrayByLenReverse(arrs);
		 * System.out.println(showString(arrs2));
		 * String path = System.getProperty("java.library.path");
		 * System.out.println(path);
		 * Map<String,String> map=System.getenv();
		 * for (Map.Entry<String, String> entry : map.entrySet()) {
		 * System.out.println("map:Key = " + entry.getKey() + ", Value = " + entry.getValue());
		 * }
		 * Properties properties=System.getProperties();
		 * Set<Object> keys = properties.keySet();//返回属性key的集合
		 * for (Object key : keys) {
		 * System.out.println("properties:"+key.toString() + "=" + properties.get(key));
		 * }
		 */
	}
}
