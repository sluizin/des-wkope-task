package des.wangku.operate.standard.utls;

import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.subengineering.reediting.Badword;

/**
 * 针对html代码进行过滤工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsHtmlFilter {
	/**
	 * 过滤script之间的内容
	 * @param content
	 * @return
	 */
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

	/**
	 * 过滤掉空格与间括号换行符,制表符
	 * @param content String
	 * @return String
	 */
	public static final String filterHtmlSymbol(String content) {
		content = content.replaceAll("&nbsp;", "");
		content = content.replaceAll("<([^>]*)>", "");
		content = content.replaceAll(" ", "");
		content = content.replaceAll("\\s*|\t|\r|\n", "");//去除字符串中的空格,回车,换行符,制表符
		return content;
	}

	/**
	 * 各节点过滤掉空格与间括号换行符,制表符
	 * @param es Elements
	 * @return Elements
	 */
	public static final Elements filterHtmlSymbol(Elements es) {
		for (Element e : es) {
			String content = e.text();
			content = filterHtmlSymbol(content);
			e.html(content);
		}
		return es;
	}

	/**
	 * 更改特殊字符
	 * @param content String
	 * @return String
	 */
	public static final String filterBookHtmlSymbol(String content) {
		content = content.replaceAll("sè", "色");
		content = content.replaceAll("０", "0");
		content = content.replaceAll("１", "1");
		content = content.replaceAll("２", "2");
		content = content.replaceAll("３", "3");
		content = content.replaceAll("４", "4");
		content = content.replaceAll("５", "5");
		content = content.replaceAll("６", "6");
		content = content.replaceAll("７", "7");
		content = content.replaceAll("８", "8");
		content = content.replaceAll("９", "9");

		return content;
	}

	/**
	 * 把内容字符串过滤掉特殊的字符如script,img标签
	 * @param content String
	 * @return String
	 */
	public static final String filter(String content) {
		if (content == null || content.length() == 0) return "";
		content = filterscript(content);
		content = filterscriptvar(content);
		content = filterTag(content, "img");
		content = filterHtmlEscapecharacter(content);
		content = filterHtmlSymbol(content);
		content = filterSpecialCharacter(content);
		return content;
	}

	static final String SpecialChar = "＊◆★↑↗▅◢☆" 
	+ "↑ ↓ ← → ↖ ↗ ↙ ↘ ↔ ↕ ➻ ➼ ➽ ➸ ➳ ➺ ➻" 
	+ " ➴ ➵ ➶ ➷ ➹▶ ➩ ➪ ➫ ➬ ➭ ➮➯ ➱ ➲ ➾ ➔ ➘ ➙ ➚ ➛ ➜" 
	+ "┌┍┎┏┐┑┒┓—┄┈├┝┞┟┠┡┢┣|┆┊┬┭┮┯┰┱┲┳┼┽┾┿╀╂╁╃";

	/**
	 * 把特殊字符转成空串
	 * @param content String
	 * @return String
	 */
	public static final String filterSpecialCharacter(String content) {
		for (int i = 0, len = SpecialChar.length(); i < len; i++) {
			char c = SpecialChar.charAt(i);
			content.replace(c, ' ');
		}
		//String[] arr= {"＊","◆","★","↑","↗","▅","◢","☆"};
		//for(String e:arr)
		//content=content.replaceAll(e, "");
		return content;
	}

	public static final String filterSymbol(String content, String... arr) {
		if (content == null || content.length() == 0) return content;
		for (String e : arr)
			content = content.replaceAll(e, " ");
		return content;
	}

	//
	static final String FilterBadWordTitle = "卫健委,新冠,肺炎,疫情,疫,部门,卫建委,病例,美国,国家,企业,股份,有限,公司,集团,组织,工厂,厂家，商家,厂商,厂,企业,医疗,董事,疗效,功效,国";

	public static final String filterBadWord(String content) {
		if (content == null || content.length() == 0) return content;
		String[] arr = FilterBadWordTitle.split(",");
		for (String e : arr)
			content = content.replaceAll(e, Badword.rep(e));
		return content;
	}

	/**
	 * 把转义字符串转成实际字符串或空
	 * @param content String
	 * @return String
	 */
	public static final String filterHtmlEscapecharacter(String content) {
		if (content == null || content.length() == 0) return content;
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("&amp", "");
		content = content.replaceAll("amp;", "");

		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&lt", "");
		content = content.replaceAll("lt;", "");

		content = content.replaceAll("&gt;", ">");
		content = content.replaceAll("&gt", "");
		content = content.replaceAll("gt;", "");

		content = content.replaceAll("&apos;", "'");
		content = content.replaceAll("&apos", "'");
		content = content.replaceAll("apos;", "'");

		content = content.replaceAll("&quot;", "\"");
		content = content.replaceAll("&quot", "\"");
		content = content.replaceAll("quot;", "\"");

		return content;
	}

	public static void main(String[] args) {
		String content = "123<scriptvar> aaa</scriptvar>bb<img src='abc'>b<img src='aaa' />b";
		content = filterscriptvar(content);
		System.out.println(content);
		content = filterTag(content, "img");

		System.out.println(content);
		content = "在新疆亿茂纺织品**公司生产车间";
		System.out.println(content.replaceAll("公司", "**"));
	}
}
