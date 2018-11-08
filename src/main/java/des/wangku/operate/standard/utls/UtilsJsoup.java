package des.wangku.operate.standard.utls;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static des.wangku.operate.standard.utls.UtilsShiftCompare.isCompare;

/**
 * Jsoup工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsJsoup {
	/** 日志 */
	static Logger logger = Logger.getLogger(UtilsJsoup.class);

	/**
	 * 得到.text()
	 * @param content String
	 * @return String
	 */
	public static String cleanHtml(String content) {
		if (content == null) return "";
		Element ee = Jsoup.parse(content);
		return ee.text();
	}

	/**
	 * 得到meta中的值
	 * @param doc Document
	 * @param key String
	 * @return String
	 */
	public static final String getMetaContent(Document doc, String key) {
		Elements elements = doc.select("meta[name=" + key + "]");
		if (elements.size() == 0) return "";
		String value = elements.get(0).attr("content");
		return value == null ? "" : value;

	}

	/**
	 * 提取jsoup &gt; Document socket&gt;URL 全部信息
	 * @param url URL
	 * @return Document
	 */
	public static final Document getDoc(URL url) {
		String newCode = getCode(url);
		try {
			Connection connect = Jsoup.connect(url.toString()).headers(UtilsConsts.header_a);
			Document document = connect.timeout(20000).maxBodySize(0).get();
			if (document != null) return document;
		} catch (IOException e) {
			e.printStackTrace();
		}

		String content = UtilsReadURL.getSocketContent(url, newCode, 20000);
		if (content != null && content.length() > 0) return Jsoup.parse(content);

		content = UtilsReadURL.getUrlContent(url, newCode, 20000);
		if (content != null && content.length() > 0) return Jsoup.parse(content);
		return null;
	}

	public static final int MODE_Jsoup = 1;
	public static final int MODE_Socket = 2;
	public static final int MODE_URL = 4;

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息
	 * @param url URL
	 * @param mode int
	 * @return Document
	 */
	public static final Document getDoc(URL url, int mode) {
		if (isCompare(mode, MODE_Jsoup)) {
			try {
				Connection connect = Jsoup.connect(url.toString()).headers(UtilsConsts.header_a);
				Document document = connect.timeout(20000).maxBodySize(0).get();
				if (document != null) return document;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		String newCode = getCode(url);
		logger.debug("newCode:" + newCode);
		if (isCompare(mode, MODE_Socket)) {
			String content = UtilsReadURL.getSocketContent(url, newCode, 20000);
			if (content != null && content.length() > 0) return Jsoup.parse(content);
			return null;
		}
		if (isCompare(mode, MODE_URL)) {
			String content = UtilsReadURL.getUrlContent(url, newCode, 20000);
			if (content != null && content.length() > 0) return Jsoup.parse(content);
			return null;
		}
		return null;
	}

	/**
	 * 得到编码。 自检索 &gt; 默认[utf-8]
	 * @param url URL
	 * @return String
	 */
	public final static String getCode(URL url) {
		String cpcode = UtilsCpdetector.getUrlEncode(url);
		if (cpcode != null) return cpcode;
		return "utf-8";
	}

	/**
	 * 按照className从body中提取链接组 &lt; a href="" &gt; &lt; /a &gt;
	 * @param body Element
	 * @param className String
	 * @return String[]
	 */
	public static final String[] getHrefArrayByClass(Element body, String className) {
		List<String> list = new ArrayList<String>();
		Elements els = body.getElementsByClass(className);
		for (Element t : els) {
			Elements as = t.select("a[href]");
			for (Element t1 : as)
				list.add(t1.attr("abs:href"));
		}
		String[] arr = {};
		return list.toArray(arr);
	}

	/**
	 * 从数组中提取第一个text()值，如为空,则返回空串
	 * @param es Elements
	 * @return String
	 */
	public static final String getElementsFirst(Elements es) {
		return getElementsFirst(es, "");
	}

	/**
	 * 从数组中提取第一个text()值，如为空,则返回def
	 * @param es Elements
	 * @param def String
	 * @return String
	 */
	public static final String getElementsFirst(Elements es, String def) {
		if (es == null || es.size() == 0) return def;
		Element e = es.first();
		if (e == null) return def;
		return e.text();
	}

	/**
	 * 从数组中提取第一个text()值，如为空,则返回def
	 * @param doc Element
	 * @param classname String
	 * @param def String
	 * @return String
	 */
	public static final String getElementsFirst(Element doc, String classname, String def) {
		Elements es = doc.getElementsByClass(classname);
		if (es == null || es.size() == 0) return def;
		return getElementsFirst(es, def);
	}

	/**
	 * 按classname提取第一个text()值，如为空,则返回空串
	 * @param doc Element
	 * @param classname String
	 * @return String
	 */
	public static final String getElementsClassFirst(Element doc, String classname) {
		Elements es = doc.getElementsByClass(classname);
		if (es == null || es.size() == 0) return "";
		Element e = es.first();
		if (e == null) return "";
		return e.text();
	}

	/**
	 * 从es数组中提取含有某个classname为keyClassName，并且内容含有 keyword
	 * @param es Elements
	 * @param keyClassName String
	 * @param keyword String
	 * @return Element
	 */
	public static final Element getElementClassKeyword(Elements es, String keyClassName, String keyword) {
		for (int i = 0; i < es.size(); i++) {
			Element e = es.get(i);
			Elements ces = e.getElementsByClass(keyClassName);
			for (Element ee : ces) {
				if (ee.html().indexOf(keyword) > -1) return e;
			}
		}
		return null;
	}

	/**
	 * 通过Indexof判断classname是否含有
	 * @param doc Element
	 * @param keyword String
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getKeywordClassIndexof(Element doc, String keyword) {
		List<Element> list = new ArrayList<>();
		Elements all = doc.getAllElements();
		loop: for (Element e : all) {
			String classname = e.attr("class");
			String[] arr = classname.split(" ");
			for (String name : arr) {
				name = name.trim();
				if (name.indexOf(keyword) > -1) {
					//System.out.println("name:" + name);
					list.add(e);
					continue loop;
				}
			}
		}
		return list;
	}

	/**
	 * 通过正则判断classname是否含有
	 * @param doc Element
	 * @param keyword String
	 * @return List&lt;RegularElement&gt;
	 */
	public static final List<RegularElement> getKeywordClassRegular(Element doc, String keyword) {
		List<RegularElement> list = new ArrayList<>();
		if (doc == null) return list;
		Elements all = doc.getAllElements();

		Pattern p = Pattern.compile(keyword);
		loop: for (Element e : all) {
			String classname = e.attr("class");
			String[] arr = classname.split(" ");
			for (String name : arr) {
				name = name.trim();
				Matcher m = p.matcher(name);
				if (m.find()) {
					RegularElement f = new RegularElement();
					f.classname = name;
					f.e = e;
					list.add(f);
					continue loop;
				}
			}
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * 通过正则得到的对象
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class RegularElement implements Comparable<RegularElement> {
		String classname = "";
		Element e = null;

		public final String getClassname() {
			return classname;
		}

		public final void setClassname(String classname) {
			this.classname = classname;
		}

		public final Element getE() {
			return e;
		}

		public final void setE(Element e) {
			this.e = e;
		}

		@Override
		public String toString() {
			return "RegularElement [" + (classname != null ? "classname=" + classname + ", " : "") + (e != null ? "e=" + e.html() : "") + "]";
		}

		@Override
		public int compareTo(RegularElement arg0) {
			if (arg0.classname.compareTo(this.classname) > 0) {
				return -1;
			} else if (arg0.classname.compareTo(this.classname) < 0) {
				return 1;
			} else {
				return 0;
			}
		}

	}
}
