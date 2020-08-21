package des.wangku.operate.standard.utls;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
	public static final class STC {
		char c = ' ';
		int style = -1;

		public STC(int style, char c) {
			this.style = style;
			this.c = c;
		}

	}

	public static int ACC_DefaultTimeout = 30000;

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
	 * 读取url，得到节点列表<br>
	 * key:&lt;!--文章内容--&gt;|&lt;!--文章内容end--&gt;则读取url某2个字段之间的节点<br>
	 * key:textDvi，则按照id class tag进行提取<br>
	 * key:id1;id2;id3或id1,id2,id3，以分号或逗号分隔，则按照多个关键字进行提取
	 * @param url String
	 * @param key String
	 * @return Elements
	 */
	public static final Elements getAllElementsByKey(String url, String key) {
		Elements es = new Elements();
		if (url == null || url.length() == 0 || key == null || key.length() == 0) return es;
		if (key.indexOf('|') > -1) {
			String[] arrs = key.split("\\|");
			Document doc = UtilsJsoup.getDoc(url, arrs[0], arrs[1]);
			if (doc == null) return es;
			es.add(doc.body());
			return es;
		}
		if (key.indexOf(';') > -1) return UtilsJsoup.getElementAll(url, key.split(";"));
		if (key.indexOf(',') > -1) return UtilsJsoup.getElementAll(url, key.split(","));
		return UtilsJsoup.getElementAll(url, key);
	}
	public static final Element getElementsCutByKey(Document e, String key) {
		if (key == null || key.length() == 0) return null;
		if (key.indexOf('|') == -1) return null;
		String[] arrs = key.split("\\|");
		Document doc=UtilsJsoup.getDoc(e.baseUri(), e, arrs[0], arrs[1]);
		if(doc==null)return null;
		return doc.body();
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
		String head = UtilsReadURL.getUrlDomain(url);
		return getDoc(head,doc,start,end);
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param head String
	 * @param doc Document
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Document getDoc(String head,Document doc, String start, String end) {
		if (doc == null) return null;
		String result = doc.html();
		String cut = UtilsString.cutString(result, start, end);
		if(cut==null)return null;
		if (head == null) return Jsoup.parse(cut);
		return Jsoup.parse(cut, head);
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
		String newCode = UtilsJsoupCase.getCode(url);
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
		String cut = UtilsString.cutString(result, start, end);
		if(cut==null)return null;
		return Jsoup.parse(cut, "", Parser.xmlParser());
	}

	/**
	 * 读取Element得到 name 这个字符串，id class tag attribname,text，所有单元 过滤掉重复项
	 * @param f Element
	 * @param arr String[]
	 * @return Elements
	 */
	public static final Elements getElementAll(Element f, String... arr) {
		if (f == null) return new Elements();
		Elements es = new Elements();
		for (String name : arr) {
			if (name.indexOf('|') == -1) {
				es.addAll(getElementsByID(f, name));
				es.addAll(getElementsByClass(f, name));
				es.addAll(getElementsByTag(f, name));
				//es.addAll(getElementsByAttribute(f, name));
				//es.addAll(UtilsJsoup.getElementsByText(f, name));
				
			}else {
				Element cut=getElementsCutByKey(f.ownerDocument(),name);
				if(cut!=null) {
					es.add(cut);
					continue;
				}
				
			}
		}
		if (es.size() > 1) UtilsList.distinct(es);
		return es;
	}

	/**
	 * 读取url得到 arrs 这个多字符串，id class tag attribname,text，所有单元 过滤掉重复项
	 * @param url String
	 * @param arr String[]
	 * @return Elements
	 */
	public static final Elements getElementAll(String url, String... arr) {
		Document doc = UtilsJsoup.getDoc(url);
		if (doc == null) return new Elements();
		return getElementAll(doc, arr);
	}
	/**
	 * 读取Element得到 name 这个字符串，id class tag attribname,text，第一个节点<br>
	 * 如果没有找到，则返回null
	 * @param f Element
	 * @param arr String[]
	 * @return Element
	 */
	public static final Element getElementAllFirst(Element f, String... arr) {
		Elements es=getElementAll(f,arr);
		if(es==null || es.size()==0)return null;
		return es.first();
	}

	/**
	 * 读取url得到 arrs 这个多字符串，id class tag attribname,text，第一个节点<br>
	 * 如果没有找到，则返回null
	 * @param url String
	 * @param arr String[]
	 * @return Element
	 */
	public static final Element getElementAllFirst(String url, String... arr) {
		Elements es= getElementAll(url, arr);
		if(es.size()==0)return null;
		return es.first();
	}
	/**
	 * 获取第一个节点的text内容<br>
	 * 如果没有找到，则返回null
	 * @param f Element
	 * @param arr String[]
	 * @return String
	 */
	public static final String getElementAllFirstText(Element f, String... arr) {
		Element e=getElementAllFirst(f,arr);
		if(e==null)return null;
		return e.text();
	}
	/**
	 * 获取第一个节点的text内容<br>
	 * 如果没有找到，则返回null
	 * @param url String
	 * @param arr String[]
	 * @return String
	 */
	public static final String getElementAllFirstText(String url,String...arr) {
		Element e=getElementAllFirst(url,arr);
		if(e==null)return null;
		return e.text();
	}
	/**
	 * 通过 不同的关键字 查找节点["","abc","{I}def,{C}abc"]，并去重<br>
	 * style:0 id<br>
	 * style:1 class<br>
	 * style:2 tag<br>
	 * style:3 attrkey<br>
	 * style:4 textkey<br>
	 * @param source Element
	 * @param style int
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements getElementsBy(Element source, int style, String... arrs) {
		if (source == null || arrs.length == 0) return new Elements();
		Elements es = new Elements();
		for (String e : arrs) {
			if (e == null || e.trim().length() == 0) continue;
			e=e.trim();
			String[] arr=e.split(",");
			for(String f:arr) {
				if(!isGenericKeyType(f,style))continue;
				String g=UtilsJsoup.getGenericKeyVal(f);
				Elements els = getElementsPrivate(source, g, style);
				if (els != null && els.size() > 0) es.addAll(els);				
			}
		}
		if (es.size() > 1) UtilsList.distinct(es);/* 去重 */
		return es;
	}

	/**
	 * 通过attribute属性名称查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	@Deprecated
	public static final Elements getElementsByAttribute(Element source, String... arrs) {
		return getElementsBy(source, 3, arrs);
	}

	/**
	 * 通过class查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements getElementsByClass(Element source, String... arrs) {
		return getElementsBy(source, 1, arrs);
	}

	/**
	 * 得到多个id的节点，允许同名id的节点
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements getElementsByID(Element source, String... arrs) {
		return getElementsBy(source, 0, arrs);
	}

	/**
	 * 通过Tag查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements getElementsByTag(Element source, String... arrs) {
		return getElementsBy(source, 2, arrs);
	}

	/**
	 * 通过text内容查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	@Deprecated
	public static final Elements getElementsByText(Element source, String... arrs) {
		return getElementsBy(source, 4, arrs);
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
		Elements es=new Elements();
		if (source == null || e == null) return es;
		e = e.trim();
		if (e.length() == 0) return es;
		switch (style) {
		case 1:/* 1 */
			es = source.getElementsByClass(e);
			break;
		case 2:/* 2 */
			es = source.getElementsByTag(e);
			break;
		case 3:/* 3 */
			es = source.getElementsByAttribute(e);
			break;
		case 4:/* 4 */
			es = source.getElementsContainingText(e);
			break;
		default:/* 0 */
			Element r = source.getElementById(e);
			if (r != null) es.add(r);
			es.addAll(Collector.collect(new Evaluator.Id(e), source));
			break;
		}
		if (es.size() > 1) UtilsList.distinct(es);/* 去重 */
		return es;
	}

	/**
	 * 返回检索关键的类型判断，如没有限制，则返回null，否则返回类型字符串，即{}之间的字符串<br>
	 * 以[]为开头的字符串，如:"{ICAT}classname1","{T}IDname1"<br>
	 * 返回{ ICAT、T }
	 * @param key String
	 * @return String
	 */
	static final String getGenericKeyMatcherVal(String key) {
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
	public static final String getGenericKeyType(String key) {
		String limitword = getGenericKeyMatcherVal(key);
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
	public static final String getGenericKeyVal(String key) {
		String limitword = getGenericKeyMatcherVal(key);
		if (limitword == null) return key;
		return key.substring(limitword.length(), key.length()).trim();
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
	 * 判断关键字字符串中含有限制词是否允许在style范围内<br>
	 * 如果没有限制词，或限制词为空，或含有限制词，则返回true<br>
	 * @param key String
	 * @param style int
	 * @return boolean
	 */
	public static final boolean isGenericKeyType(String key, int style) {
		String result = getGenericKeyType(key);
		if (result == null || result.length() == 0) return true;
		result = result.toUpperCase();
		for (STC e : ACC_STCList) {
			if (e.style == style) return result.indexOf(e.c) > -1;
		}
		return result.indexOf('I') > -1;
	}

	public static void main(String[] args) {
		String[] arr = { null, "abcc", "{ICE}", "", "{    }   ", "{aa", "eee}", "}dd{", "{}", "a{}def", "{abc}aa", "def{123}", "a{XY}z", "{ICAT}classname" };
		for (String e : arr) {
			String val = getGenericKeyType(e);
			String va = getGenericKeyVal(e);
			System.out.print(":" + e + ": \t :");
			System.out.print(val == null ? "null" : val);
			System.out.print(": \t :");
			System.out.print(va == null ? "null" : va);
			System.out.println(":");
		}
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
	@Deprecated
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
	 * @param keyarrs String[]
	 * @param removearr int[]
	 */
	public static final void remove_Element(Element source, int style, String[] keyarrs, int... removearr) {
		if (source == null || keyarrs.length == 0 || removearr.length == 0) return;
		Elements es = UtilsJsoup.getElementsBy(source, style, keyarrs);
		for (int i = es.size() - 1; i >= 0; i--) {
			if (UtilsString.isExist(i, removearr)) {
				es.get(i).remove();
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
		Elements es = UtilsJsoup.getElementsBy(source, style, arrs);
		int len = es.size();
		if (len == 0) return;
		int index = (len - 1) - size;
		if (index >= 0 && index < es.size()) es.get(index).remove();
	}

	/**
	 * 移除所有指定标签下标的节点
	 * @param source Element
	 * @param p String
	 * @param keyarrs int[]
	 */
	public static final void remove_Tag(Element source, String p, int... keyarrs) {
		if (source == null || p == null || p.trim().length() == 0 || keyarrs.length == 0) return;
		Elements es = UtilsJsoup.getElementsByTag(source, p.trim());
		for (int i = es.size() - 1; i >= 0; i--)
			if (UtilsString.isExist(i, keyarrs)) es.get(i).remove();
	}

	/**
	 * 得到所有标签，并按结果列表从后向前步长进行移除<br>
	 * {0,1}:指从后向前，第1个与第2个进行移除
	 * @param source Element
	 * @param p String
	 * @param keyarrs int[]
	 */
	public static final void remove_TagEnd(Element source, String p, int... keyarrs) {
		if (source == null || p == null || p.trim().length() == 0 || keyarrs.length == 0) return;
		Elements es = UtilsJsoup.getElementsByTag(source, p.trim());
		for (int i = es.size() - 1; i >= 0; i--)
			if (UtilsString.isExist(i, keyarrs)) es.get(i).remove();
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
	 * @param keyarrs String[]
	 */
	public static final void removeClass(Element source, String... keyarrs) {
		if (source == null || keyarrs.length == 0) return;
		Elements list = UtilsJsoup.getElementsByClass(source, keyarrs);
		for (Element e : list)
			e.remove();
	}

	/**
	 * 按照id中的单元移除
	 * @param source Element
	 * @param arrs String[]
	 */
	public static final void removeID(Element source, String... arrs) {
		if (source == null || arrs.length == 0) return;
		Elements es = UtilsJsoup.getElementsByID(source, arrs);
		for (Element e : es)
			e.remove();
	}

	/**
	 * 按照tag中的单元移除所有单元
	 * @param source Element
	 * @param arrs String[]
	 */
	public static final void removeTag(Element source, String... arrs) {
		if (source == null || arrs.length == 0) return;
		Elements es = UtilsJsoup.getElementsByTag(source, arrs);
		for (Element e : es)
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
