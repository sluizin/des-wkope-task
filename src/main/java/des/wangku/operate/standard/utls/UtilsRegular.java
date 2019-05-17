package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;
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
		/*
		 * String[] arr= {"+12","12","1 2","-25","-522","- 105"," - 58 ","+  52","+	60","+0"};
		 * String pattern = "^[\\s\\S]*([+-]+)\\s*(\\d+)\\s*$";
		 * for(String e:arr) {
		 * Pattern r = Pattern.compile(pattern);
		 * Matcher m = r.matcher(e);
		 * //System.out.println(e+"\t\t"+m.find());
		 * if(m.find()) {
		 * System.out.println("Found value: " + m.group(0) );
		 * System.out.println("Found value: " + m.group(1) );
		 * System.out.println("Found value: " + m.group(2) );
		 * }
		 * System.out.println("-----------------------------------------------");
		 * }
		 */
		String content = "[1]sadsaasfdweqew[100]effewasdf[022]asdfas[a]fa[d00]sdf[5]asf[580]rr[005]";
		String pattern = "\\[[0-9]+\\]";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(content);
		while (m.find()) {
			System.out.println("[" + m.start() + "," + m.end() + "]Found value: " + m.group(0));
		}
		System.out.println(content);
		System.out.println(getPatternNumDisCount(content));
		System.out.println("" + getPatternReplaceNumDis(content, 0, "(abc)"));
		System.out.println("" + getPatternReplaceNumDis(content, 1, "(abc)"));
		System.out.println("" + getPatternReplaceNumDis(content, 2, "(abc)"));
		System.out.println("" + getPatternReplaceNumDis(content, 3, "(abc)"));
		System.out.println("" + getPatternReplaceNumDis(content, 4, "(abc)"));
		System.out.println("" + getPatternReplaceNumDis(content, 5, "(abc)"));
		int[] arr=getArrayNum(content);
		for(int a:arr) {
			System.out.println("a:"+a);
		}

	}
	private static final String pattern = "\\[[0-9]+\\]";
	/**
	 * 得到字符串提取的[102]此类的子串，得到指定下标
	 * @param content String
	 * @param index int
	 * @return int
	 */
	public static final int getArrayNumNo(String content,int index) {
		int [] arr=getArrayNum(content);
		if(index<0 ||index>=arr.length)return -1;
		return arr[index];
	}
	
	/**
	 * 从字符串中提取所有[102]此类的子串，并得到数值数组
	 * @param content String
	 * @return int[]
	 */
	public static final int[] getArrayNum(String content) {
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(content);
		List<Integer> list=new ArrayList<>();
		while (m.find()) {
			String str=m.group(0);
			list.add(Integer.parseInt(str.substring(1,str.length()-1)));
		}
		int[] arr=new int[list.size()];
		for(int i=0;i<list.size();i++) {
			arr[i]=list.get(i);
		}
		return arr;
	}
	/**
	 * 从字符串中提取所有[102],[a102]此类的子串，并得到字符串
	 * @param content String
	 * @return String
	 */
	public static final String getArrayIDFirst(String content) {
		String[] arr=getArrayID(content);
		if(arr==null || arr.length==0)return null;
		return arr[0];
	}
	/**
	 * 从字符串中提取所有[102],[a102]此类的子串，并得到字符串数组
	 * @param content String
	 * @return String[]
	 */
	public static final String[] getArrayID(String content) {
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(content);
		List<String> list=new ArrayList<>();
		while (m.find()) {
			String str=m.group(0);
			list.add(str.substring(1,str.length()-1));
		}
		String[] arr= {};
		return list.toArray(arr);
	}

	/**
	 * 把正则检索出来的某个位置转为value
	 * @param content String
	 * @param index int
	 * @param value String
	 * @return String
	 */
	public static final String getPatternReplaceNumDis(final String content, int index, String value) {
		if (content == null || index < 0 || value == null) return content;
		Pattern r = Pattern.compile(ACC_NumDisPattern);
		Matcher m = r.matcher(content);
		int i = 0;
		while (m.find()) {
			if ((i++) != index) continue;
			int contentlen = content.length();
			StringBuilder sb = new StringBuilder(contentlen + value.length());
			if (m.start() > 0) sb.append(content.substring(0, m.start()));
			sb.append(value);
			if (m.end() < contentlen) sb.append(content.substring(m.end(), contentlen));
			return sb.toString();
		}
		return content;
	}

	/**
	 * 判断字符串中含有多个数字显示样式
	 * @param content String
	 * @return int
	 */
	public static final int getPatternNumDisCount(String content) {
		if (content == null || content.length() == 0) return 0;
		Pattern r = Pattern.compile(ACC_NumDisPattern);
		Matcher m = r.matcher(content);
		int count = 0;
		while (m.find())
			count++;
		return count;
	}

	/**
	 * 判断字符串中含有多个子串 支持正则
	 * @param content String
	 * @param keyword String
	 * @return int
	 */
	public static final int getPatternCount(String content,String keyword) {
		if (content == null || content.length() == 0) return 0;
		Pattern r = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);
		int count = 0;
		while (m.find())
			count++;
		return count;
	}
	/**
	 * 判断字符串中含有多个子串 多个子串使用|进行间隔
	 * @param content String
	 * @param keyword String
	 * @return int
	 */
	public static final int getPatternMultiKeyCount(String content,String keyword) {
		return getPatternCount(content, "(?:"+keyword+")");
	}
	/** 在样式中显示格式： [220] */
	public static final String ACC_NumDisPattern = "\\[[0-9]+\\]";
}
