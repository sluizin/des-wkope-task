package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Jsoup工具<br>
 * 提取文本 只针对文本
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsJsoupText {
	/**
	 * 得到网址中指定下标的text内容
	 * @param doc Element
	 * @param index int
	 * @param arr String[]
	 * @return String
	 */
	public static final String getText(Element doc, int index, String... arr) {
		List<String> list = getTextAll(doc, arr);
		if (index >= 0 && index < list.size()) return list.get(index);
		return null;
	}

	/**
	 * 得到网址中指定下标的text内容
	 * @param url String
	 * @param index int
	 * @param arr String[]
	 * @return String
	 */
	public static final String getText(String url, int index, String... arr) {
		List<String> list = getTextAll(url, arr);
		if (index >= 0 && index < list.size()) return list.get(index);
		return null;
	}

	/**
	 * 从网址中提取所有text内容
	 * @param doc Element
	 * @param arr String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getTextAll(Element doc, String... arr) {
		if (doc == null || arr.length == 0) return new ArrayList<>();
		Elements es = UtilsJsoup.getElementAll(doc, arr);
		List<String> list = new ArrayList<>(es.size());
		for (Element e : es)
			list.add(e.text());
		return list;
	}

	/**
	 * 从网址中提取所有text内容
	 * @param url String
	 * @param arr String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getTextAll(String url, String... arr) {
		if (url == null || url.length() == 0 || arr.length == 0) return new ArrayList<>();
		Elements es = UtilsJsoup.getElementAll(url, arr);
		List<String> list = new ArrayList<>(es.size());
		for (Element e : es)
			list.add(e.text());
		return list;
	}

	/**
	 * 得到网址中第一个text内容
	 * @param doc Element
	 * @param arr String[]
	 * @return String
	 */
	public static final String getTextFirst(Element doc, String... arr) {
		return getText(doc, 0, arr);
	}

	/**
	 * 得到网址中第一个text内容
	 * @param url String
	 * @param arr String[]
	 * @return String
	 */
	public static final String getTextFirst(String url, String... arr) {
		return getText(url, 0, arr);
	}
}
