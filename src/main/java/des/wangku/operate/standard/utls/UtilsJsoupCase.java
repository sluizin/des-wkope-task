package des.wangku.operate.standard.utls;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jsoup工具<br>
 * <br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsJsoupCase {

	/**
	 * 通过正则得到的对象
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class RegularElement implements Comparable<RegularElement> {
		String classname = "";
		Element e = null;

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

		public final String getClassname() {
			return classname;
		}

		public final Element getE() {
			return e;
		}

		public final void setClassname(String classname) {
			this.classname = classname;
		}

		public final void setE(Element e) {
			this.e = e;
		}

		@Override
		public String toString() {
			return "RegularElement [" + (classname != null ? "classname=" + classname + ", " : "") + (e != null ? "e=" + e.html() : "") + "]";
		}

	}

	public static final String ACC_LI = "|li";

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsJsoupCase.class);

	/**
	 * 把字符串转成Jsoup对象再得到text()内容，
	 * @param content String
	 * @return String
	 */
	public static final String cleanHtml(String content) {
		return cleanHtml(content, null);
	}

	/**
	 * 把字符串转成Jsoup对象再得到text()内容
	 * @param content String
	 * @param domain String
	 * @return String
	 */
	public static final String cleanHtml(String content, String domain) {
		if (content == null) return null;
		Document e = UtilsJsoupExt.getDocument(content, domain);
		if (e == null) return null;
		return e.text();
	}

	/**
	 * 通用提取，从doc提取指定字符串的节点，并过滤重复节点<br>
	 * "id,class,na|li"<br>
	 * 以|li为标记，则从na中提取li标签<br>
	 * @param doc Document
	 * @param tagli String
	 * @return Elements
	 */
	public static final Elements commonGetList(Document doc, String tagli) {
		if (doc == null || tagli == null || tagli.length() == 0) return new Elements(0);
		Elements es = new Elements();
		String[] arrs = tagli.split(",");
		for (String e : arrs) {
			if (e.indexOf(ACC_LI) <= 0) {
				es.addAll(UtilsJsoup.getElementAll(doc, e));
				continue;
			}
			String key = tagli.substring(0, tagli.indexOf(ACC_LI)).trim();
			if (key.length() == 0) continue;
			Elements els = UtilsJsoup.getElementAll(doc, key);
			for (Element t : els) {
				es.addAll(t.select("li"));
			}
		}
		if (es.size() > 1) es = UtilsList.distinctHtml(es);
		return es;
	}

	/**
	 * 通用提取，从url提取指定字符串的节点，并过滤重复节点<br>
	 * "id,class,na|li"<br>
	 * 以|li为标记，则从na中提取li标签<br>
	 * @param url String
	 * @param tagli String
	 * @return Elements
	 */
	public static final Elements commonGetList(String url, String tagli) {
		if (url == null || url.length() == 0) return new Elements(0);
		Document doc = UtilsJsoupExt.getDoc(url);
		return commonGetList(doc, tagli);
	}

	/**
	 * 页面含有css数量
	 * @param doc Document
	 * @return int
	 */
	public static final int getBaseCssCount(Document doc) {
		return getTagList(doc, "link", "type", "text/css").size();
	}

	/**
	 * 页面含有img标签中含有alt值的数量
	 * @param doc Document
	 * @return int
	 */
	public static final int getBaseImgAltCount(Document doc) {
		return getTagList(doc, "img", "alt", "*").size();
	}

	/**
	 * 页面含有js数量
	 * @param doc Document
	 * @return int
	 */
	public static final int getBaseJsCount(Document doc) {
		return UtilsJsoup.getElementsFinalByTag(doc, "script").size();
	}

	/**
	 * 得到meta中的值
	 * @param doc Document
	 * @param key String
	 * @return String
	 */
	public static final String getBaseMetaContent(Document doc, String key) {
		Elements elements = doc.select("meta[name=" + key + "]");
		if (elements.size() == 0) return "";
		String value = elements.get(0).attr("content");
		return value == null ? "" : value;
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
	 * 通用提取，从doc提取指定字符串的节点，并过滤重复节点<br>
	 * "id,class,na|li"<br>
	 * 以|li为标记，则从na中提取li标签<br>
	 * @param doc Document
	 * @param tagli String
	 * @return List&lt;Element&gt;
	 */
	@Deprecated
	public static final List<Element> getCommonList(Document doc, String tagli) {
		if (doc == null || tagli == null || tagli.length() == 0) return new ArrayList<>(0);
		List<Element> list = new ArrayList<>();
		String[] arrs = tagli.split(",");
		for (String e : arrs) {
			if (e.indexOf(ACC_LI) <= 0) {
				list.addAll(UtilsJsoup.getElementAll(doc, e));
				continue;
			}
			String key = tagli.substring(0, tagli.indexOf(ACC_LI)).trim();
			if (key.length() == 0) continue;
			List<Element> list3 = UtilsJsoup.getElementAll(doc, key);
			for (Element t : list3) {
				list.addAll(t.select("li"));
			}
		}
		if (list.size() > 1) list = UtilsList.distinct(list);
		return list;
	}

	/**
	 * 通用提取，从url提取指定字符串的节点，并过滤重复节点<br>
	 * "id,class,na|li"<br>
	 * 以|li为标记，则从na中提取li标签<br>
	 * @param url String
	 * @param tagli String
	 * @return List&lt;Element&gt;
	 */
	@Deprecated
	public static final List<Element> getCommonList(String url, String tagli) {
		if (url == null || url.length() == 0) return new ArrayList<>(0);
		Document doc = UtilsJsoupExt.getDoc(url);
		return getCommonList(doc, tagli);
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
			List<Element> list = UtilsJsoup.getElementsFinalByClass(e, keyClassName);
			for (Element ee : list)
				if (ee.html().indexOf(keyword) > -1) return e;
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
	 * 得到所有某个标签的属性值。允许使用"*"代表所有数值
	 * @param doc Document
	 * @param tag String
	 * @param alt String
	 * @param value String
	 * @return List&lt;element&gt;
	 */
	public static final List<Element> getTagList(Document doc, String tag, String alt, String value) {
		List<Element> list = new ArrayList<>();
		if (tag == null || tag.length() == 0) return list;
		if (alt == null || alt.length() == 0) return list;
		List<Element> sList = UtilsJsoup.getElementsFinalByTag(doc, tag);
		for (Element e : sList) {
			String v = e.attr(alt);
			if (v == null) continue;
			if ("*".equals(value) || v.equals(value)) list.add(e);
		}
		return list;
	}

	/**
	 * 得到所有某个标签中含有的地址的绝对地址，如a-href img-src等
	 * @param body Element
	 * @param tag String
	 * @param url String
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getTagUrlList(Element body, String tag, String url) {
		List<String> list = new ArrayList<String>();
		Elements as = body.select(tag + "[" + url + "]");
		for (Element t1 : as)
			list.add(t1.attr("abs:" + url));
		return list;
	}

	/**
	 * 从数组中提取第一个text()值，如为空,则返回def
	 * @param es Elements
	 * @return String
	 */
	public static final String getTextFirst(Elements es) {
		return getTextFirst(es, "");
	}

	/**
	 * 从数组中提取第一个text()值，如为空,则返回def
	 * @param es Elements
	 * @param def String
	 * @return String
	 */
	public static final String getTextFirst(Elements es, String def) {
		if (es == null || es.size() == 0) return def;
		Element e = es.first();
		if (e == null) return def;
		return e.text();
	}

	/**
	 * 从数组中提取第一个text()值，如为空,则返回空串
	 * @param es List&lt;Element&gt;
	 * @return String
	 */
	public static final String getTextFirst(List<Element> es) {
		return getTextFirst(es, "");
	}

	/**
	 * 从数组中提取第一个text()值，如为空,则返回def
	 * @param list List&lt;Element&gt;
	 * @param def String
	 * @return String
	 */
	public static final String getTextFirst(List<Element> list, String def) {
		if (list == null || list.size() == 0) return def;
		Element e = list.get(0);
		if (e == null) return def;
		return e.text();
	}

	/**
	 * 按classname提取第一个text()值，如为空,则返回空串
	 * @param doc Element
	 * @param classname String
	 * @return String
	 */
	public static final String getTextFirstByClass(Element doc, String classname) {
		return getTextFirstByClass(doc, classname, "");
	}

	/**
	 * 从数组中提取第一个text()值，如为空,则返回def
	 * @param doc Element
	 * @param classname String
	 * @param def String
	 * @return String
	 */
	public static final String getTextFirstByClass(Element doc, String classname, String def) {
		List<Element> list = UtilsJsoup.getElementsFinalByClass(doc, classname);
		if (list.size() == 0) return def;
		return getTextFirst(list, def);
	}

	/**
	 * 得到url的title值，如果没有找，则返回null
	 * @param url String
	 * @return String
	 */
	public static final String getURLTitle(String url) {
		if (url == null || url.trim().length() == 0) return null;
		if (!UtilsVerification.isConnect(url)) return null;
		Document doc = UtilsJsoupExt.getDoc(url);
		if (doc == null) return null;
		List<Element> list = UtilsJsoup.getElementsFinalByTag(doc, "title");
		return getTextFirst(list, null);

	}

	/**
	 * 从网页中提取内容，并过滤不必要的字符，返回""
	 * @param href String
	 * @param contentkey String
	 * @param filter String
	 * @return String
	 */
	public static final String getWebSearchContent(String href, String contentkey, String filter) {
		if (href == null || contentkey == null || contentkey.length() == 0) return "";
		Elements es = UtilsJsoup.getElementAll(href, contentkey);
		if (es.size() <= 0) return "";
		Element e = es.first();
		UtilsJsoup.remove(e, filter);
		return UtilsHtmlFilter.filter(e.text());
	}

	/**
	 * 从网页中提取div中所含的所有链接
	 * @param source Element
	 * @param div String
	 * @return Elements
	 */
	public static final Elements getWebSearchItems(Element source, String div) {
		if (source == null) return new Elements();
		String key = div.trim();
		Element obj = source;
		if (div != null && key.length() > 0) obj = UtilsJsoup.getElementFirst(source, key);
		if (obj == null) return new Elements();
		return obj.select("a");
	}

	public static void main2(String[] args) {
		String ACC_Path = "G:/下载文档夹/m.99114.com/";
		String url = "http://m.99114.com/category/find";
		List<Element> list = UtilsJsoup.getElementAll(url, "section");
		int bigmin = 0, bigmax = 0;
		try {
			for (int i = bigmin; i <= bigmax; i++) {
				Element main = list.get(i);
				String bigcategory = UtilsJsoupCase.getTextFirstByClass(main, "classifyTitle");
				String bigcategorypath = ACC_Path + bigcategory + "/";
				File filebig = new File(bigcategorypath);
				if (!filebig.exists()) filebig.mkdir();
				Elements es = main.select("li");
				for (int ii = 0; ii < es.size(); ii++) {
					Element e = es.get(ii);
					String smallcategory = e.text();
					System.out.println("[" + i + "]:" + bigcategory + "\t" + smallcategory);
					String smallcategorypath = bigcategorypath + "/" + smallcategory + ".txt";
					File filesmall = new File(smallcategorypath);
					if (filesmall.exists()) continue;
					String encode = URLEncoder.encode(smallcategory + "", "UTF-8");
					int p = 0;
					while (p < 100) {
						url = "http://m.99114.com/gongying/" + encode + "_-_-_" + p + "_-_-.html";
						System.out.println(url);
						Document doc = UtilsJsoupExt.getDoc(url);//,UtilsJsoup.MODE_URL);
						if (doc.text().length() < 100) break;
						System.out.println(smallcategory + ":" + p);
						Elements gs = doc.getElementsByClass("firstLine");
						for (Element gse : gs) {
							String line = "";
							String name = UtilsJsoupLink.getTextFirst(gse);
							String href = UtilsJsoupLink.getHrefFirst(gse);
							line = UtilsConsts.ACC_ENTER + "" + name + "\t" + href;
							UtilsFile.writeFile(smallcategorypath, line);
						}

						p++;
					}

				}

			}
		} catch (Exception e) {
			System.out.println("error:" + url);
			e.printStackTrace();
			System.out.println("error:" + e.toString());
		}

		/*
		 * String[] arr = { "ab|2", "|2", "|", "div|7" };
		 * for (String e : arr) {
		 * int index = e.indexOf('|');
		 * System.out.println(e + ":" + index);
		 * String v = e.substring(0, index);
		 * String val = e.substring(index + 1, e.length());
		 * System.out.println(e + "=" + v + ":\t:" + val);
		 * }
		 */
	}

	@Deprecated
	public static final void removeCommonIdClassTag(Element source, String... arrs) {
		if (source == null || arrs.length == 0) return;
		for (String e : arrs) {
			int index = e.indexOf('|');
			if (index > -1) {/* 含有定位数值 */
				String key = e.substring(0, index).trim();
				if (key.length() == 0) continue;
				String num = e.substring(index + 1, e.length());
				if (UtilsVerification.isNumeric(num)) {
					//int n = Integer.valueOf(num);

				}

			} else {
				List<Element> list = UtilsJsoup.getElementAll(source, e);
				for (Element f : list)
					f.remove();
			}
		}
	}

}
