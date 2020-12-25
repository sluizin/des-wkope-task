package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 对字符串进行分解<br>
 * 以大括号、中括号、小括号进行字符串的前缀与后缀<br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsStringPrefix {

	/**
	 * 从字符中提取[]或{}提出之间的字符串,如果没有找到，则返回null<br>
	 * 返回{IHT} 或[0,1,2]
	 * @param pattern String
	 * @param key String
	 * @return String
	 */
	private static final String geCheckIntervalString(String pattern, String key) {
		if (key == null) return null;
		String newkey = key.trim();
		if (newkey.length() == 0) return null;
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(newkey);
		if (!m.find()) return null;
		return m.group();
	}

	/**
	 * 从字符中提取()或[]或{}提出之间的字符串,如果没有找到，则返回null<br>
	 * 返回 IHT 或0,1,2
	 * @param pattern String
	 * @param key String
	 * @return String
	 */
	public static final String geCheckIntervalVal(String pattern, String key) {
		if (key == null) return null;
		String val = geCheckIntervalString(pattern, key);
		if (val == null) return null;
		return val.substring(1, val.length() - 1);
	}
	/**
	 * 过滤两侧的括号
	 * @param val String
	 * @return String
	 */
	@Deprecated
	public static final String getVal(String val) {
		if (val == null) return null;
		val=val.trim();
		if(val.length()<2)return null;
		return val.substring(1, val.length() - 1);
	}

	/**
	 * 得到第一个前缀
	 * @param line String
	 * @param isEnd boolean
	 * @return String
	 */
	static final String getFirst(String line, boolean isEnd) {
		String area = getBig(line, isEnd);
		if (area != null) return area;
		return getMiddle(line, isEnd);
	}

	/**
	 * 得到大括号前后缀{abc}
	 * @param line String
	 * @return String
	 */
	public static final String getBig(String line, boolean isEnd) {
		return geCheckIntervalString(isEnd ? "\\{[^}]*\\}$" : "^\\{[^}]*\\}", line);
	}

	/**
	 * 得到中括号前后缀 [abc]
	 * @param line String
	 * @return String
	 */
	public static final String getMiddle(String line, boolean isEnd) {
		return geCheckIntervalString(isEnd ? "\\[[^]]*\\]$" : "^\\[[^]]*\\]", line);
	}

	/**
	 * 得到字符串前、后缀字符串 例:[aa] {bb}[cc]<br>
	 * 如不合格，则返回null
	 * @param line String
	 * @param isEnd boolean
	 * @return String
	 */
	public static final String getPrefix(String line, boolean isEnd) {
		if (!isFormat(line)) return null;
		int v = getSize(line, isEnd);
		if (v == 0) return "";
		if (isEnd) return line.substring(line.length() - v);
		return line.substring(0, v);
	}
	/**
	 * 提取所有修饰字符串 如[1,2,3]、{abc,def}
	 * @param line String
	 * @param isEnd boolean
	 * @return List&lt;String&gt;
	 */
	static final List<String> getPrefixList(String line, boolean isEnd) {
		String nline = getPrefix(line, isEnd);
		if (nline == null || nline.length() == 0) return new ArrayList<>(0);
		List<String> list = new ArrayList<>();
		String str = null;
		while ((str = getFirst(nline, isEnd)) != null) {
			list.add(str);
			if (isEnd) nline = nline.substring(0, nline.length() - (str.length()));
			else nline = nline.substring(str.length(), nline.length());
			nline = nline.trim();
		}
		return list;
	}
	/**
	 * 提取修饰字符串 symbol 0:() 1:[] 2:{}
	 * @param line String
	 * @param isEnd boolean
	 * @param symbol int
	 * @return String[]
	 */
	public static final String[] getPrefixValArray(String line, boolean isEnd, int symbol) {
		String[] arr = {};
		List<String> li = getPrefixList(line, isEnd);
		if (li.size() == 0) return arr;
		List<String> list = new ArrayList<>();
		for (String e : li) {
			if(e==null||e.length()==0)continue;
			char c = e.charAt(0);
			if ((symbol == 0 && c == '(') || (symbol == 1 && c == '[') || (symbol == 2 && c == '{')) {
				e=e.substring(1,e.length()-1);
				list.add(e);
			}
		}
		return list.toArray(arr);
	}

	/**
	 * 得到字符串头部或尾部(isEnd)多个修饰字串的长度，如果没有找到，则返回0
	 * @param line String
	 * @param isEnd boolean
	 * @return int
	 */
	static final int getSize(String line, boolean isEnd) {
		int i = 0;
		line = line.trim();
		String str = null;
		while ((str = getFirst(line, isEnd)) != null) {
			i += (str.length()) ;
			if (isEnd) line = line.substring(0, line.length() - (str.length() ));
			else line = line.substring(str.length(), line.length());
			String nline = line.trim();
			if (((str = getFirst(line, isEnd)) != null)) i += line.length() - nline.length();
			else i += line.length() - nline.length();
			line = line.trim();
		}
		return i;
	}

	/**
	 * 过滤两侧的修饰字符串
	 * @param line String
	 * @return String
	 */
	public static final String getValue(String line) {
		if (!isFormat(line)) return null;
		int x = getSize(line, false);
		int y = getSize(line, true);
		return line.substring(x, line.length() - y);
	}

	/**
	 * 格式是否正确<br>
	 * False:"   "<br>
	 * True:	[2,5]{b}XXX[1,2]{c}<br>
	 * True:XXX[]{}<br>
	 * True:[]{}XXX<br>
	 * False:[]{[]{}<br>
	 * @param line String
	 * @return boolean
	 */
	public static final boolean isFormat(String line) {
		if (line == null) return false;
		int x = getSize(line, false);
		int y = getSize(line, true);
		if (x == y && x == line.length()) return false;
		return true;
	}

	public static void main(String[] args) {
		String[] arr = { " [a] e{b}","{C}itembox","itembox{C}","itembox", "{eee} [ff] X [eaa] {tt} ", "[  aa  ] {bb}[xxx]YYY", "ZZ[0]{eww}", "[]YY{}", "{  aa  } [    ]{bb} cc[0] {kk} {cc}", "{aa}[bb]{cc}{ee}[ff]", "{aa}    [bb]    {}e[ff]" };
		for (String e : arr) {
			if (!isFormat(e)) continue;
			String result = getValue(e);
			System.out.println("字符串\t:" + e + "\t中间值:" + result);
			System.out.print("头部整串:" + getPrefix(e, false));
			System.out.print("\t尾部整串:" + getPrefix(e, true));
			System.out.println();
			String[] head=getPrefixValArray(e,false,1);
			for(String ff:head)
				System.out.print(":"+ff+"\t");
			System.out.println();
			System.out.println("====================================");
		}
	}

}
