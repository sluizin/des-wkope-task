package des.wangku.operate.standard.utls;

import static des.wangku.operate.standard.utls.UtilsShiftCompare.isCompare;

/**
 * 字符串的过滤工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsStringFilter {
	/** 小括号 */
	public static final int BRACKETS_SMALL = 1;
	/** 中括号 */
	public static final int BRACKETS_MIDDLE = 2;
	/** 大括号 */
	public static final int BRACKETS_BIG = 4;

	/**
	 * 过滤字符串中的不同括号内容替换成其它字符串
	 * @param content String
	 * @param newValue String
	 * @param type int BRACKETS_SMALL/BRACKETS_MIDDLE/BRACKETS_BIG
	 * @return String
	 */
	public static final String getFileterBrackets(String content, String newValue, int type) {
		if (content == null || content.length() == 0) return content;
		String value = content;
		if (isCompare(type, BRACKETS_SMALL)) {
			value = value.replaceAll("\\(.*?\\)|（.*?）", newValue);
		}
		if (isCompare(type, BRACKETS_MIDDLE)) {
			value = value.replaceAll("\\[.*?\\]", newValue);
		}
		if (isCompare(type, BRACKETS_BIG)) {
			value = value.replaceAll("\\{.*?\\}", newValue);
		}
		return value;
	}

	/**
	 * 把字符串只p标签与br换行标签转成特殊字符串，防止过滤掉
	 * @param content String
	 * @return String
	 */
	public static final String getReplaceHTML(String content) {
		if (content == null || content.length() == 0) return content;
		String str = content.replaceAll("\\<p( .*?)*\\>", REP_PB);
		str = str.replaceAll("\\</p\\>", REP_PC);
		str = str.replaceAll("\\<br/\\>", REP_BR);
		str = str.replaceAll("\\<br\\>", REP_br);
		return str;
	}

	static final String REP_PB = "YYYYY";
	static final String REP_PC = "yyyyy";
	static final String REP_BR = "TTTTT";
	static final String REP_br = "ttttt";

	/**
	 * 把特殊字符串转成html代码中的p标签与br标签
	 * @param content String
	 * @return String
	 */
	public static final String getReplaceHTMLReversal(String content) {
		if (content == null || content.length() == 0) return content;
		String str = content.replaceAll(REP_PB, "<p>");
		str = str.replaceAll(REP_PC, "</p>");
		str = str.replaceAll(REP_BR, "<br/>");
		str = str.replaceAll(REP_br, "<br>");
		return str;
	}
	/**
	 * 过滤掉字符串中的html代码(含小括号)，只保留p标签，br标签
	 * @param contentHtml String
	 * @return String
	 */
	public static final String getHtmlContent(String contentHtml) {
		String content = contentHtml;
		content = UtilsStringFilter.getFileterBrackets(contentHtml, "", UtilsStringFilter.BRACKETS_SMALL);
		content = UtilsStringFilter.getReplaceHTML(content);
		content = UtilsJsoupCase.cleanHtml(content);
		content = UtilsStringFilter.getReplaceHTMLReversal(content);
		content = content.replaceAll("\\<p\\>[\\s|\\t]*\\</p\\>", "").trim();/* 过滤掉<p></p>之间含有多个空格 */
		return content;
	}
	/**
	 * 过滤名称中的非法特殊字符
	 * @param name String
	 * @return String
	 */
	public static final String filterName(String name) {
		if(name==null || name.length()==0)return name;
		name = name.toLowerCase();
		name = name.replace("\\", "");
		name = name.replace("/", "");
		name = name.replace("|", "");
		name = name.replace(":", "");
		name = name.replace("<", "");
		name = name.replace(">", "");
		name = name.replace("*", "");
		name = name.replace("?", "");
		name = name.replace("\"", "");
		return name;
	}

	public static void main(String[] args) {
		System.out.println("Hello World!");
		String content = "D<p>    </p>EF<p></p>1<p>3</p>4<p> 	 </p><p></p>a<div>bc<p>d<br>e</div>f</p>c(def)hij(lmn)km()n[123]d<p>e<div>e<br/>e</p>dd{cc}<p class='t'>123<p a>34<br>5<pl>";
		//String str=content.replaceAll("\\(.*?\\)|\\{.*?}|\\[.*?]|（.*?）", "");
		System.out.println(content);
		System.out.println(getHtmlContent(content));
	}
}
