package des.wangku.operate.standard.utls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsRegular {

	/**
	 * 从字符串中得到某个变量组
	 * @param Content String
	 * @param regEx String
	 * 
	 * @param group int
	 * @return String
	 */
	public static final String getRegExContent(String Content, String regEx, int group) {
		if (Content == null) return null;
		if (regEx == null) return null;
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(Content);
		if (!mat.find()) return null;
		if (group < 0 || group > mat.groupCount()) return null;
		return mat.group(group);
	}
}
