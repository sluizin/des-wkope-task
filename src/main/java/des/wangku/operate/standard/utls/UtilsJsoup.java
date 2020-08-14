package des.wangku.operate.standard.utls;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jsoup工具<br>
 * {ICTAE}为关键字限制<br>
 * [0]I：从id号进行检索<br>
 * [1]C：从classname进行检索<br>
 * [2]T：从Tag进行检索<br>
 * [3]A：从AttribKey键名称进行检索<br>
 * [4]E：从text内容是否含有进行检索<br>
 * 例:"{CI}name1"，即从id和classname进行检索合并，找出Element列表<br>
 * <br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsJsoup {

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

	public static final class STC {
		char c = ' ';
		int style = -1;

		public STC(int style, char c) {
			this.style = style;
			this.c = c;
		}

	}

	public static final int ACC_DefaultTimeout = 30000;

	public static final String ACC_LI = "|li";

	private static final String ACC_SearchTypePattern = "^\\{[^}]*\\}";

	static final List<STC> ACC_STCList = new ArrayList<>();

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsJsoup.class);

	public static final int MODE_Jsoup = 1;
	public static final int MODE_Socket = 2;

	public static final int MODE_URL = 4;

	static {
		ACC_STCList.add(new STC(0, 'I'));// style:0 Id
		ACC_STCList.add(new STC(1, 'C'));// style:1 Class
		ACC_STCList.add(new STC(2, 'T'));// style:2 Tag
		ACC_STCList.add(new STC(3, 'A'));// style:3 AttribKey
		ACC_STCList.add(new STC(4, 'E'));// style:4 Text
	}

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
	 * @param urldomain String
	 * @return String
	 */
	public static final String cleanHtml(String content, String urldomain) {
		if (content == null) return null;
		Element e = urldomain == null ? Jsoup.parse(content) : Jsoup.parse(content, urldomain);
		if (e == null) return null;
		return e.text();
	}

	/**
	 * 读取Element得到 name 这个字符串，id class tag，所有单元 过滤掉重复项
	 * @param f Element
	 * @param arr String[]
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getAllElements(Element f, String... arr) {
		if (f == null) return new ArrayList<>(0);
		List<Element> list = new ArrayList<>();
		for (String name : arr) {
			list.addAll(getElementByID(f, name));
			list.addAll(getElementsByClass(f, name));
			list.addAll(getElementsByTag(f, name));
		}
		if (list.size() > 1) UtilsList.distinct(list);
		return list;
	}

	/**
	 * 读取url得到 arrs 这个多字符串，id class tag，所有单元 过滤掉重复项
	 * @param url String
	 * @param arr String[]
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getAllElements(String url, String... arr) {
		Document doc = UtilsJsoup.getDoc(url);
		if (doc == null) return new ArrayList<>();
		return getAllElements(doc, arr);
	}

	/**
	 * 读取url，得到节点列表<br>
	 * key:&lt;!--文章内容--&gt;|&lt;!--文章内容end--&gt;则读取url某2个字段之间的节点<br>
	 * key:textDvi，则按照id class tag进行提取<br>
	 * key:id1;id2;id3或id1,id2,id3，以分号或逗号分隔，则按照多个关键字进行提取
	 * @param url String
	 * @param key String
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getAllElementsByKey(String url, String key) {
		List<Element> list = new ArrayList<>();
		if (url == null || url.length() == 0 || key == null || key.length() == 0) return list;
		if (key.indexOf('|') > -1) {
			String[] arrs = key.split("\\|");
			Document doc = UtilsJsoup.getDoc(url, arrs[0], arrs[1]);
			if (doc == null) return list;
			list.add(doc.body());
			return list;
		}
		if (key.indexOf(';') > -1) return UtilsJsoup.getAllElements(url, key.split(";"));
		if (key.indexOf(',') > -1) return UtilsJsoup.getAllElements(url, key.split(","));
		return UtilsJsoup.getAllElements(url, key);
	}

	/**
	 * 从element中提取指定下标的链接
	 * @param f Element
	 * @param index int
	 * @param arrs String[]
	 * @return String
	 */
	public static final String getAllHref(Element f, int index, String... arrs) {
		List<String> list = getAllHrefs(f, arrs);
		int size = list.size();
		if (size == 0) return null;
		if (index >= 0 && index < size) return list.get(index);
		return null;
	}

	/**
	 * 从element中提取指定下标的链接
	 * @param f Element
	 * @param index int
	 * @param arrs String[]
	 * @return String
	 */
	public static final String getAllHref(Element f, String... arrs) {
		return getAllHref(f, 0, arrs);
	}

	/**
	 * 从element中提取所有的链接
	 * @param f Element
	 * @param arrs String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getAllHrefs(Element f, String... arrs) {
		if (f == null || arrs.length == 0) return new ArrayList<>(0);
		List<String> list = new ArrayList<>(1);
		List<Element> li = getAllElements(f, arrs);
		for (Element e : li) {
			Elements arr = e.select("a");
			for (int i = 0; i < arr.size(); i++) {
				Element tt = arr.get(i);
				list.add(tt.attr("abs:href"));
			}
		}
		UtilsList.distinct(list);
		return list;
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
	public static final List<Element> getCommonList(Document doc, String tagli) {
		if (doc == null || tagli == null || tagli.length() == 0) return new ArrayList<>(0);
		List<Element> list = new ArrayList<>();
		String[] arrs = tagli.split(",");
		for (String e : arrs) {
			if (e.indexOf(ACC_LI) <= 0) {
				list.addAll(UtilsJsoup.getAllElements(doc, e));
				continue;
			}
			String key = tagli.substring(0, tagli.indexOf(ACC_LI)).trim();
			if (key.length() == 0) continue;
			List<Element> list3 = UtilsJsoup.getAllElements(doc, key);
			for (Element t : list3) {
				list.addAll(t.select("li"));
			}
		}
		if (list.size() > 1) UtilsList.distinct(list);
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
	public static final List<Element> getCommonList(String url, String tagli) {
		if (url == null || url.length() == 0) return new ArrayList<>(0);
		Document doc = UtilsJsoup.getDoc(url);
		return getCommonList(doc, tagli);
	}

	/**
	 * 页面含有css数量
	 * @param doc Document
	 * @return int
	 */
	public static final int getCssCount(Document doc) {
		return getTagList(doc, "link", "type", "text/css").size();
	}

	/**
	 * 提取jsoup &gt; Document socket&gt;URL 全部信息
	 * @param url String
	 * @return Document
	 */
	public static final Document getDoc(String url) {
		try {
			URL url1 = new URL(url);
			return getDoc(url1);
		} catch (IOException e) {
			System.out.println("error:" + url);
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息
	 * @param url String
	 * @param mode int
	 * @return Document
	 */
	public static final Document getDoc(String url, int mode) {
		try {
			URL url1 = new URL(url);
			return getDoc(url1, mode);
		} catch (IOException e) {
			System.out.println("error:" + url);
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param url String
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Document getDoc(String url, String start, String end) {
		Document doc = getDoc(url);
		if (doc == null) return null;
		String result = doc.html();

		int index1 = result.indexOf(start);
		if (index1 == -1) return null;
		int index2 = result.indexOf(end, index1);
		if (index2 == -1) return null;
		String cut = result.substring(index1 + start.length(), index2);
		return Jsoup.parse(cut);
	}

	/**
	 * 提取jsoup &gt; Document socket&gt;URL 全部信息
	 * @param url URL
	 * @return Document
	 */
	public static final Document getDoc(URL url) {
		if (url == null) return null;
		return getDoc(url, MODE_Jsoup + MODE_Socket + MODE_URL);
	}

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息
	 * @param url URL
	 * @param mode int
	 * @return Document
	 */
	public static final Document getDoc(URL url, int mode) {
		return getDoc(url, 0, false, mode, ACC_DefaultTimeout);
	}

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息<br>
	 * sleetp 暂停时间:是否需要线程暂停多少毫秒，小于等于0时，则不暂停<br>
	 * istest 是否读取测试<br>
	 * @param url URL
	 * @param sleep int
	 * @param istest boolean
	 * @param mode int
	 * @param timeout int
	 * @return Document
	 */
	public static final Document getDoc(URL url, int sleep, boolean istest, int mode, int timeout) {
		if (url == null) return null;
		if (sleep > 0) {
			try {
				Thread.sleep(sleep);
			} catch (Exception e) {
			}
		}
		if (istest && !UtilsReadURL.isConnection(url)) return null;
		if (UtilsShiftCompare.isCompare(mode, MODE_Jsoup)) {
			logger.debug(getMessage(url, "Jsoup", timeout));
			try {
				Connection connect = Jsoup.connect(url.toString()).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
				Document document = connect.timeout(timeout).maxBodySize(0).get();
				if (document != null) return document;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String newCode = getCode(url);
		String domain = UtilsReadURL.getUrlDomain(url);
		if (UtilsShiftCompare.isCompare(mode, MODE_Socket)) {
			logger.debug(getMessage(url, "socket", timeout));
			String content = UtilsReadURL.getSocketContent(url, newCode, timeout);
			if (content != null && content.length() > 0) return domain == null ? Jsoup.parse(content) : Jsoup.parse(content, domain);
		}
		if (UtilsShiftCompare.isCompare(mode, MODE_URL)) {
			logger.debug(getMessage(url, "UrlRead", timeout));
			String content = UtilsReadURL.getUrlContent(url, newCode, timeout);
			if (content != null && content.length() > 0) return domain == null ? Jsoup.parse(content) : Jsoup.parse(content, domain);
		}
		return null;
	}

	/**
	 * 提取全部信息 运行JS
	 * @param url String
	 * @return Document
	 */
	public static final Document getDocJS(String url) {
		try {
			URL url1 = new URL(url);
			return getDocJS(url1);
		} catch (IOException e) {
			System.out.println("error:" + url);
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * 提取全部信息 运行JS
	 * @param url URL
	 * @return Document
	 */
	public static final Document getDocJS(URL url) {
		String content = UtilsReadURL.getReadUrlJsDefault(url.toString());
		String domain = UtilsReadURL.getUrlDomain(url);
		return domain == null ? Jsoup.parse(content) : Jsoup.parse(content, domain);
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param url String
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Element getElement(Element source, String start, String end) {
		if (source == null) return null;
		String result = source.html();
		int index1 = result.indexOf(start);
		if (index1 == -1) return null;
		int index2 = result.indexOf(end, index1);
		if (index2 == -1) return null;
		String cut = result.substring(index1 + start.length(), index2);
		return Jsoup.parse(cut, "", Parser.xmlParser());
	}

	/**
	 * 通过id查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getElementByID(Element source, String... arrs) {
		if (source == null || arrs.length == 0) return new ArrayList<>();
		List<Element> list = new ArrayList<>();
		for (String e : arrs) {
			if (e == null || e.length() == 0) continue;
			Element r = source.getElementById(e);
			if (r != null) list.add(r);//r.remove();
			Elements elements = Collector.collect(new Evaluator.Id(e), source);/* 提取同名id */
			list.addAll(elements);
		}
		if (list.size() > 1) UtilsList.distinct(list);/* 去重 */
		return list;
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
			List<Element> list = getElementsByClass(e, keyClassName);
			for (Element ee : list)
				if (ee.html().indexOf(keyword) > -1) return e;
		}
		return null;
	}

	/**
	 * 通过 不同的关键字 查找节点，并去重<br>
	 * style:0 id<br>
	 * style:1 class<br>
	 * style:2 tag<br>
	 * style:3 attrkey<br>
	 * style:4 textkey<br>
	 * @param source Element
	 * @param style int
	 * @param arrs String[]
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getElementsBy(Element source, int style, String... arrs) {
		if (source == null || arrs.length == 0) return new ArrayList<>();
		List<Element> list = new ArrayList<>();
		for (String e : arrs) {
			if (e == null || e.trim().length() == 0) continue;
			Elements els = getElementsPrivate(source, e, style);
			if (els != null && els.size() > 0) list.addAll(els);
		}
		if (list.size() > 1) UtilsList.distinct(list);/* 去重 */
		return list;
	}

	/**
	 * 通过attribute属性名称查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getElementsByAttribute(Element source, String... arrs) {
		return getElementsBy(source, 3, arrs);
	}

	/**
	 * 通过class查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getElementsByClass(Element source, String... arrs) {
		return getElementsBy(source, 1, arrs);
	}

	/**
	 * 得到多个id的节点，允许同名id的节点
	 * @param source Element
	 * @param arrs String[]
	 * @return List&lt;Elmenet&gt;
	 */
	public static final List<Element> getElementsByID(Element source, String... arrs) {
		return getElementsBy(source, 0, arrs);
	}

	/**
	 * 通过Tag查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return List&lt;Element&gt;
	 */
	public static final List<Element> getElementsByTag(Element source, String... arrs) {
		return getElementsBy(source, 2, arrs);
	}

	/**
	 * 获取节点中的关键字，按不同方式查找，并去重<br>
	 * style:0 id<br>
	 * style:1 class<br>
	 * style:2 tag<br>
	 * style:3 attrkey<br>
	 * style:4 textkey<br>
	 * @param source Element
	 * @param e String
	 * @param style int
	 * @return Elements
	 */
	private static final Elements getElementsPrivate(Element source, String e, int style) {
		if (source == null || e == null) return new Elements();
		e = e.trim();
		if (e.length() == 0) return new Elements();
		switch (style) {
		case 1:
			return source.getElementsByClass(e);
		case 2:
			return source.getElementsByTag(e);
		case 3:
			return source.getElementsByAttribute(e);
		case 4:
			return source.getElementsContainingText(e);
		default:
			return Collector.collect(new Evaluator.Id(e), source);
		}
	}

	/**
	 * 从doc中提取所有链接，并提取第一个链接地址
	 * @param doc Element
	 * @return String
	 */
	public static final String getHrefFirstHref(Element doc) {
		List<String> list = getHrefTagAll(doc, 0);
		if (list.size() == 0) return null;
		return list.get(0);
	}

	/**
	 * 从doc中提取所有链接，并提取第一个链接文本
	 * @param doc Element doc
	 * @return String
	 */
	public static final String getHrefFirstText(Element doc) {
		List<String> list = getHrefTagAll(doc, 1);
		if (list.size() == 0) return null;
		return list.get(0);
	}

	/**
	 * 从doc中提取链接组或标题组 &lt; a href="" &gt; &lt; /a &gt;<br>
	 * style:0 得到链接 1:得到链接文本
	 * @param doc Element
	 * @param style int
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getHrefTagAll(Element doc, int style) {
		if (doc == null) return new ArrayList<>();
		List<String> list = new ArrayList<>();
		Elements as = doc.select("a[href]");
		for (Element t1 : as) {
			String val = null;
			if (style <= 0) {
				val = t1.attr("abs:href");
			} else {
				val = t1.text();
			}
			if (val != null) list.add(val);
		}
		return list;
	}

	/**
	 * 从es列表中提取链接组或标题组 &lt; a href="" &gt; &lt; /a &gt;<br>
	 * style:0 得到链接 1:得到链接文本
	 * @param es List&lt;String&gt;
	 * @param style int
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getHrefTagAll(List<Element> es, int style) {
		if (es == null || es.size() == 0) return new ArrayList<>();
		List<String> list = new ArrayList<>(es.size());
		for (Element e : es)
			list.addAll(getHrefTagAll(e, style));
		return list;
	}

	/**
	 * 从doc中提取所有 id class tag 节点并从中提取链接组或标题组 &lt; a href="" &gt; &lt; /a &gt;<br>
	 * style:0 得到链接 1:得到链接文本
	 * @param doc Element
	 * @param style int
	 * @param arr String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getHrefTagBy(Element doc, int style, String... arr) {
		if (doc == null) return new ArrayList<>();
		return getHrefTagAll(getAllElements(doc, arr), style);
	}

	/**
	 * 页面含有img标签中含有alt值的数量
	 * @param doc Document
	 * @return int
	 */
	public static final int getImgAltCount(Document doc) {
		return getTagList(doc, "img", "alt", "*").size();
	}

	/**
	 * 页面含有js数量
	 * @param doc Document
	 * @return int
	 */
	public static final int getJsCount(Document doc) {
		return getElementsByTag(doc, "script").size();
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
	 * 重组日志内容
	 * @param url URL
	 * @param type String
	 * @param timeout int
	 * @return String
	 */
	private static final String getMessage(URL url, String type, int timeout) {
		return "[" + timeout + "]读取网址方式: " + type + "!\t" + url.toString();
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
	 * 返回检索关键的类型判断，如没有限制，则返回null，否则返回类型字符串，即{}之间的字符串<br>
	 * 以[]为开头的字符串，如:"{ICAT}classname1","{T}IDname1"<br>
	 * 返回{ ICAT、T }
	 * @param key String
	 * @return String
	 */
	static final String getSearchKeyMatcherVal(String key) {
		if (key == null) return null;
		String newkey = key.trim();
		if (newkey.length() == 0) return null;
		final Pattern r = Pattern.compile(ACC_SearchTypePattern);
		final Matcher m = r.matcher(newkey);
		if (!m.find()) return null;
		return m.group();
	}

	/**
	 * 返回检索关键的类型判断，如没有限制，则返回null，否则返回类型字符串，即{}之间的字符串<br>
	 * 以{}为开头的字符串，如:"{ICAT}classname1","{T}IDname1"<br>
	 * 返回ICAT、T
	 * @param key String
	 * @return String
	 */
	public static final String getSearchKeyType(String key) {
		String limitword = getSearchKeyMatcherVal(key);
		if (limitword == null) return null;
		return limitword.substring(1, limitword.length() - 1).trim();
	}

	/**
	 * 返回除去限定词之外的关键词<br>
	 * 以{}为开头的字符串<br>
	 * 例如:"{ICAT}classname1","{T}IDname1"<br>
	 * 返回classname1,IDname1
	 * @param key String
	 * @return String
	 */
	public static final String getSerachKeyVal(String key) {
		String limitword = getSearchKeyMatcherVal(key);
		if (limitword == null) return key;
		return key.substring(limitword.length(), key.length()).trim();
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
		List<Element> sList = getElementsByTag(doc, tag);
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
		List<Element> list = getElementsByClass(doc, classname);
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
		Document doc = getDoc(url);
		if (doc == null) return null;
		List<Element> list = getElementsByTag(doc, "title");
		return getTextFirst(list, null);

	}

	/**
	 * 判断关键字字符串中含有限制词是否允许在style范围内<br>
	 * 如果没有限制词，或限制词为空，或含有限制词，则返回true<br>
	 * @param key String
	 * @param style int
	 * @return boolean
	 */
	public static final boolean isSearchKeyType(String key, int style) {
		String result = getSearchKeyType(key);
		if (result == null || result.length() == 0) return true;
		result = result.toUpperCase();
		char c = 'I';
		for (STC e : ACC_STCList) {
			if (e.style == style) return result.indexOf(e.c) > -1;
		}
		return result.indexOf(c) > -1;
	}

	public static void main(String[] args) {
		String[] arr = { null, "abcc", "{ICE}", "", "{    }   ", "{aa", "eee}", "}dd{", "{}", "a{}def", "{abc}aa", "def{123}", "a{XY}z", "{ICAT}classname" };
		for (String e : arr) {
			String val = getSearchKeyType(e);
			String va = getSerachKeyVal(e);
			System.out.print(":" + e + ": \t :");
			System.out.print(val == null ? "null" : val);
			System.out.print(": \t :");
			System.out.print(va == null ? "null" : va);
			System.out.println(":");
		}
	}

	public static void main2(String[] args) {
		String ACC_Path = "G:/下载文档夹/m.99114.com/";
		String url = "http://m.99114.com/category/find";
		List<Element> list = UtilsJsoup.getAllElements(url, "section");
		int bigmin = 0, bigmax = 0;
		try {
			for (int i = bigmin; i <= bigmax; i++) {
				Element main = list.get(i);
				String bigcategory = UtilsJsoup.getTextFirstByClass(main, "classifyTitle");
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
						Document doc = UtilsJsoup.getDoc(url);//,UtilsJsoup.MODE_URL);
						if (doc.text().length() < 100) break;
						System.out.println(smallcategory + ":" + p);
						Elements gs = doc.getElementsByClass("firstLine");
						for (Element gse : gs) {
							String line = "";
							String name = UtilsJsoup.getHrefFirstText(gse);
							String href = UtilsJsoup.getHrefFirstHref(gse);
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

	/**
	 * 按照id和class和Tag中的单元
	 * @param source Element
	 * @param arrs String[]
	 */
	public static final void remove(Element source, String... arrs) {
		removeID(source, arrs);
		removeClass(source, arrs);
		removeTag(source, arrs);
	}

	/**
	 * 按照class名称中含有关键字中的单元移除
	 * @param source Element
	 * @param arrs String[]
	 */
	public static final void remove_ClassIndexof(Element source, String... arrs) {
		if (source == null || arrs.length == 0) return;
		Elements es = source.getAllElements();
		loop: for (Element e : es) {
			Set<String> set = e.classNames();
			if (set.size() == 0) continue;
			for (String f : arrs) {
				if (UtilsString.isExistIndexOf(f, set)) {
					e.remove();
					continue loop;
				}
			}
		}
	}

	/**
	 * 按不同类型查出节点 移除指定数组中下标的单元<br>
	 * style:0 id<br>
	 * style:1 class<br>
	 * style:2 tag<br>
	 * style:3 attrkey<br>
	 * style:4 textkey<br>
	 * @param source Element
	 * @param style int
	 * @param arrs String[]
	 * @param arr int[]
	 */
	public static final void remove_Element(Element source, int style, String[] arrs, int... arr) {
		if (source == null || arrs.length == 0 || arr.length == 0) return;
		List<Element> list = getElementsBy(source, style, arrs);
		for (int i = list.size() - 1; i >= 0; i--) {
			if (UtilsString.isExist(i, arr)) {
				list.get(i).remove();
			}
		}
	}

	/**
	 * 按照tag中的单元 移除反向第N个下标的单元<br>
	 * 当size:0，删除最后的单元<br>
	 * 当size:1，删除倒数第二个单元<br>
	 * 以此类推<br>
	 * style:0 id<br>
	 * style:1 class<br>
	 * style:2 tag<br>
	 * style:3 attrkey<br>
	 * style:4 textkey<br>
	 * @param source Element
	 * @param style int
	 * @param size int
	 * @param arrs String[]
	 * @param arr int[]
	 */
	public static final void remove_ElementRev(Element source, int style, int size, String[] arrs, int... arr) {
		if (source == null || size < 0 || arrs.length == 0) return;
		List<Element> list = getElementsBy(source, style, arrs);
		;
		int len = list.size();
		if (len == 0) return;
		int index = (len - 1) - size;
		if (index >= 0 && index < list.size()) list.get(index).remove();

	}

	/**
	 * 移除所有指定标签下标的节点
	 * @param source Element
	 * @param p String
	 * @param arrs int[]
	 */
	public static final void remove_Tag(Element source, String p, int... arrs) {
		if (source == null || p == null || p.trim().length() == 0 || arrs.length == 0) return;
		List<Element> es = getElementsByTag(source, p.trim());
		for (int i = es.size() - 1; i >= 0; i--)
			if (UtilsString.isExist(i, arrs)) es.get(i).remove();
	}

	/**
	 * 得到所有标签，并按结果列表从后向前步长进行移除<br>
	 * {0,1}:指从后向前，第1个与第2个进行移除
	 * @param source Element
	 * @param p String
	 * @param arrs int[]
	 */
	public static final void remove_TagEnd(Element source, String p, int... arrs) {
		if (source == null || p == null || p.trim().length() == 0 || arrs.length == 0) return;
		List<Element> es = getElementsByTag(source, p.trim());
		for (int i = es.size() - 1; i >= 0; i--)
			if (UtilsString.isExist(i, arrs)) es.get(i).remove();
	}

	/**
	 * 移除所有text为空的p区块
	 * @param source Element
	 */
	public static final void remove_TextEmptyAll(Element source) {
		Elements childen = source.getAllElements();
		for (Element e : childen) {
			if (e.text().trim().length() == 0) e.remove();
		}
	}

	/**
	 * 移除所有text为空的子类p区块
	 * @param source Element
	 */
	public static final void remove_TextEmptyChild(Element source) {
		Elements childen = source.children();
		for (Element e : childen) {
			if (e.text().trim().length() == 0) e.remove();
		}
	}

	/**
	 * 按照class中的单元移除
	 * @param source Element
	 * @param arrs String[]
	 */
	public static final void removeClass(Element source, String... arrs) {
		if (source == null || arrs.length == 0) return;
		List<Element> list = getElementsByClass(source, arrs);
		for (Element e : list)
			e.remove();
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
					int n = Integer.valueOf(num);

				}

			} else {
				List<Element> list = getAllElements(source, e);
				for (Element f : list)
					f.remove();
			}
		}
	}

	/**
	 * 按照id中的单元移除
	 * @param source Element
	 * @param arrs String[]
	 */
	public static final void removeID(Element source, String... arrs) {
		if (source == null || arrs.length == 0) return;
		List<Element> list = getElementByID(source, arrs);
		for (Element e : list)
			e.remove();
	}

	/**
	 * 按照tag中的单元移除所有单元
	 * @param source Element
	 * @param arrs String[]
	 */
	public static final void removeTag(Element source, String... arrs) {
		if (source == null || arrs.length == 0) return;
		List<Element> list = getElementsByTag(source, arrs);
		for (Element e : list)
			e.remove();
	}

	/**
	 * 按照tag中的单元 移除指定数组中下标的单元
	 * @param source Element
	 * @param arrs String[]
	 * @param arr int[]
	 */
	public static final void removeTag(Element source, String[] arrs, int... arr) {
		remove_Element(source, 2, arrs, arr);
	}

	/**
	 * 按照tag中的单元 移除反向第N个下标的单元<br>
	 * 当size:0，删除最后的单元<br>
	 * 当size:1，删除倒数第二个单元<br>
	 * 以此类推
	 * @param source Element
	 * @param size int
	 * @param arrs String[]
	 */
	public static final void removeTagReverse(Element source, int size, String... arrs) {
		remove_ElementRev(source, 2, size, arrs);
	}
}
