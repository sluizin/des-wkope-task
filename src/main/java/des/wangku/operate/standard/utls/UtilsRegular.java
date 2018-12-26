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
	/**
	 * 判断字符串中正则的结果，是否存在
	 * @param Content String
	 * @param regEx String
	 * @return boolean
	 */
	public static final boolean getRegExBoolean(String Content, String regEx) {
		if (Content == null) return false;
		if (regEx == null) return false;
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(Content);
		return mat.find();
	}

	public static void main(String[] args) {
		String[] arr= {"+12","12","1 2","-25","-522","- 105"," - 58 ","+  52","+	60","+0"};
		String pattern = "^[\\s\\S]*([+-]+)\\s*(\\d+)\\s*$";
		for(String e:arr) {
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(e);
			//System.out.println(e+"\t\t"+m.find());
			if(m.find()) {
		         System.out.println("Found value: " + m.group(0) );
		         System.out.println("Found value: " + m.group(1) );
		         System.out.println("Found value: " + m.group(2) );
			}
			System.out.println("-----------------------------------------------");
		}
		
		
	}
}
