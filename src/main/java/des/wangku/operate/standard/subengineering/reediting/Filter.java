package des.wangku.operate.standard.subengineering.reediting;

import java.util.regex.Pattern;

/**
 * 过滤html中的部分标记
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Filter {
	public static final String filterscript(String content) {
		return filterTagBetween(content, "script");
	}

	public static final String filterscriptvar(String content) {
		return filterTagBetween(content, "scriptvar");
	}

	/**
	 * 过滤某个标签 例如:script标签
	 * @param content String
	 * @param tag String
	 * @return String
	 */
	public static final String filterTagBetween(String content, String tag) {
		if (content == null || content.length() == 0 || tag == null || tag.length() == 0) return content;
		return Pattern.compile("<" + tag + "[\\s\\S]*?>[\\s\\S]*?<\\/" + tag + ">", Pattern.CASE_INSENSITIVE).matcher(content).replaceAll("");
	}

	/**
	 * 过滤某个标签 例如img标签
	 * @param content String
	 * @param tag String
	 * @return String
	 */
	public static final String filterTag(String content, String tag) {
		if (content == null || content.length() == 0 || tag == null || tag.length() == 0) return content;
		return Pattern.compile("(<" + tag + "\\b.*?(?:\\>|\\/>))", Pattern.CASE_INSENSITIVE).matcher(content).replaceAll("");
	}
	public static final String filterHtmlSymbol(String content) {
		content=content.replaceAll("&nbsp;","");
		content=content.replaceAll("<([^>]*)>","");
		content=content.replaceAll(" ","");
		return content;
	}
	public static final String filterHtmlEscapecharacter(String content) {
		content=content.replaceAll("&amp;","&");
		content=content.replaceAll("&amp","");
		content=content.replaceAll("amp;","");
		
		
		content=content.replaceAll("&lt;","<");
		content=content.replaceAll("&lt","");
		content=content.replaceAll("lt;","");
		
		
		content=content.replaceAll("&gt;",">");
		content=content.replaceAll("&gt","");
		content=content.replaceAll("gt;","");
		
		content=content.replaceAll("&apos;","'");
		content=content.replaceAll("&apos","");
		content=content.replaceAll("apos;","");
		
		content=content.replaceAll("&quot;","\"");
		content=content.replaceAll("&quot","");
		content=content.replaceAll("quot;","");
		
		return content;
	}

	public static void main(String[] args) {
		String content = "123<scriptvar> aaa</scriptvar>bb<img src='abc'>b<img src='aaa' />b";
		content = filterscriptvar(content);
		System.out.println(content);
		content = filterTag(content, "img");

		System.out.println(content);
	}
}
