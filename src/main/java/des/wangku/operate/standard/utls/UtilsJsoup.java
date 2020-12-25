package des.wangku.operate.standard.utls;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * [5]H：从a标签中进行检索后面为地址内容indexOf大于0<br>
 * 例:"{CI}name1"，即从id和classname进行检索合并，找出Element列表<br>
 * <br>
 * a1;a2;a3;
 * <br>
 * a1|To|b2
 * <br>
 * 页内进行精确定位 a1 |->| a2 |->| a3<br>
 * 外页进行精确定位 a1 |=>| a2 |=>| a3<br>
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

	public static int ACC_BaiduDefaultTimeout = 30000;

	/** jsoup关键字规则，间断关键字 */
	public static final String ACC_JsoupRuleCutIntervalSplit = "\\|[Tt]{1}[oO]{1}\\|";
	/** jsoup关键字规则，最高等级间隔 */
	public static final String ACC_JsoupRuleInterval = "|;|";
	public static final String ACC_JsoupRuleIntervalSplit = "\\|\\;\\|";
	/** jsoup关键字规则，用于页内精确定位 主要用于通过与id、class等相似的规则，进行定位 */
	public static final String ACC_JsoupRulePrecisePositioningInPage = "|->|";
	public static final String ACC_JsoupRulePrecisePositioningInPageSplit = "\\|->\\|";
	/** jsoup关键字规则，用于页外精确定位 主要用于通过链接的连锁，定位节点 */
	public static final String ACC_JsoupRulePrecisePositioningOutPage = "|=>|";
	public static final String ACC_JsoupRulePrecisePositioningOutPageSplit = "\\|=>\\|";
	static final List<STC> ACC_STCList = new ArrayList<>();

	/** 顶级分隔符或串 */
	@Deprecated
	static final String[] FixedInterval_0 = { ";;;", ",,,", "|||" };

	/** 顶级分隔符或串 */
	@Deprecated
	static final String[] FixedInterval_1 = { ";;", ",,", "||" };

	/** 最低级分隔符或串 */
	@Deprecated
	static final String[] FixedInterval_2 = { ";" };
	public static boolean isDynamicUrl = false;

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsJsoup.class);

	public static final int MODE_Jsoup = 1;

	public static final int MODE_Socket = 2;

	public static final int MODE_URL = 4;

	static {
		ACC_STCList.add(new STC(-1, 'S'));// style:-1 Sub
		ACC_STCList.add(new STC(0, 'I'));// style:0 Id
		ACC_STCList.add(new STC(1, 'C'));// style:1 Class
		ACC_STCList.add(new STC(2, 'T'));// style:2 Tag
		ACC_STCList.add(new STC(3, 'A'));// style:3 AttribKey
		ACC_STCList.add(new STC(4, 'E'));// style:4 Text
		ACC_STCList.add(new STC(5, 'H'));// style:5 Href
	}

	@Deprecated
	public static final String[] decomposeKey1111(String... arr) {
		List<String> list = new ArrayList<>();
		for (String e : arr) {
			if (e == null || e.trim().length() == 0) continue;
			e = e.trim();
			if (intervalKeywords(list, e, FixedInterval_0)) continue;
			if (intervalKeywords(list, e, FixedInterval_1)) continue;
			if (e.indexOf('|') > -1) {
				list.add(e);
				continue;
			}
			if (intervalKeywords(list, e, FixedInterval_2)) continue;
		}
		String[] a = {};
		return list.toArray(a);
	}

	/**
	 * 多个关键字进行精确定位a1 a2 a3
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements forward(Element source, String... arrs) {
		if (arrs.length == 0) return new Elements();
		List<String> list = Arrays.asList(arrs);
		Elements es = new Elements();
		forwardPrivate(es, source, list, 0);
		return es;
	}

	/**
	 * 多个关键字进行精确定位 a1 |->| a2 |->| a3
	 * @param source Element
	 * @param key String
	 * @return Elements
	 */
	public static final Elements forwardKey(Element source, String key) {
		if (key.indexOf(ACC_JsoupRulePrecisePositioningInPage) == -1) return new Elements();
		String[] arr = key.split(ACC_JsoupRulePrecisePositioningInPageSplit);
		return forward(source, arr);
	}

	/**
	 * 递归循环调取精确定位
	 * @param es Elements
	 * @param source Element
	 * @param list List&lt;String&gt;
	 * @param index int
	 */
	private static final void forwardPrivate(Elements es, Element source, List<String> list, int index) {
		if (index < 0 || index >= list.size()) return;
		String key = list.get(index);
		Elements ess = UtilsJsoup.getElementAll(source, key);
		if (ess.size() == 0) return;
		if (index == list.size() - 1) {
			es.addAll(ess);
			return;
		}
		for (Element ee : ess)
			forwardPrivate(es, ee, list, index + 1);
	}

	/**
	 * 读取url，得到节点列表<br>
	 * key:&lt;!--文章内容--&gt;|to|&lt;!--文章内容end--&gt;则读取url某2个字段之间的节点<br>
	 * key:textDvi，则按照id class tag进行提取<br>
	 * key:id1;id2;id3或id1,id2,id3，以分号或逗号分隔，则按照多个关键字进行提取
	 * @param url String
	 * @param key String
	 * @return Elements
	 */
	public static final Elements getAllElementsByKey(String url, String key) {
		Elements es = new Elements();
		if (url == null || url.length() == 0 || key == null || key.length() == 0) return es;
		if (isCutInterval(key)) {
			String[] arrs = key.split(ACC_JsoupRuleCutIntervalSplit);
			Document doc = UtilsJsoup.getDoc(url, arrs[0], arrs[1]);
			if (doc == null) return es;
			es.add(doc.body());
			return es;
		}
		if (key.indexOf(';') > -1) return UtilsJsoup.getElementAll(url, key.split(";"));
		if (key.indexOf(',') > -1) return UtilsJsoup.getElementAll(url, key.split(","));
		return UtilsJsoup.getElementAll(url, key);
	}

	/**
	 * 提取jsoup &gt; Document socket&gt;URL 全部信息
	 * @param url String
	 * @return Document
	 */
	public static final Document getDoc(String url) {
		if (UtilsConsts.isErrorUrl(url)) return null;
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
		if (UtilsConsts.isErrorUrl(url)) return null;
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
		if (UtilsConsts.isErrorUrl(url)) return null;
		Document doc = getDoc(url);
		String head = UtilsReadURL.getUrlDomain(url);
		return getRebuildDoc(head, doc, start, end);
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
		if (UtilsConsts.isErrorUrl(url)) return null;
		if (isDynamicUrl) return getDocJS(url);
		int sleep = 0;
		if (url.getHost().indexOf("baidu.com") > -1) sleep = ACC_BaiduDefaultTimeout;
		return getDoc(url, null, sleep, false, mode, 30000);
	}

	/**
	 * 提取jsoup [1(MODE_Jsoup)] &gt; Document socket[2(MODE_Socket)] &gt; URL[4(MODE_URL)] 全部信息<br>
	 * sleetp 暂停时间:是否需要线程暂停多少毫秒，小于等于0时，则不暂停<br>
	 * istest 是否读取测试<br>
	 * @param url URL
	 * @param newCode String
	 * @param sleep int
	 * @param istest boolean
	 * @param mode int
	 * @param timeout int
	 * @return Document
	 */
	public static final Document getDoc(URL url, String newCode, int sleep, boolean istest, int mode, int timeout) {
		if (url == null) return null;
		if (UtilsConsts.isErrorUrl(url)) return null;
		if (sleep > 0) UtilsThread.ThreadSleep(sleep);
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
		if (newCode == null || newCode.length() == 0) newCode = UtilsJsoupCase.getCode(url);
		String domain = UtilsReadURL.getUrlDomain(url);
		if (UtilsShiftCompare.isCompare(mode, MODE_Socket)) {
			logger.debug(getMessage(url, "socket", timeout));
			String content = UtilsReadURL.getSocketContent(url, newCode, timeout);
			if (content != null && content.length() > 0) { return UtilsJsoupCase.getDocument(content, domain); }
		}
		if (UtilsShiftCompare.isCompare(mode, MODE_URL)) {
			logger.debug(getMessage(url, "UrlRead", timeout));
			String content = UtilsReadURL.getUrlContent(url, newCode, timeout);
			if (content != null && content.length() > 0) return UtilsJsoupCase.getDocument(content, domain);
		}
		return null;
	}

	/**
	 * 提取全部信息 运行JS
	 * @param url String
	 * @return Document
	 */
	public static final Document getDocJS(String url) {
		if (UtilsConsts.isErrorUrl(url)) return null;
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
		if (UtilsConsts.isErrorUrl(url)) return null;
		String content = UtilsReadURL.getReadUrlJsDefault(url.toString());
		String domain = UtilsReadURL.getUrlDomain(url);
		return UtilsJsoupCase.getDocument(content, domain);
	}

	/**
	 * 以|To|进行区分切块
	 * @param e Document
	 * @param key String
	 * @return Element
	 */
	public static final Document getDocumentCutByKey(Document e, String key) {
		if (key == null || key.length() == 0) return null;
		if (!isCutInterval(key)) return null;
		String[] arrs = key.split(ACC_JsoupRuleCutIntervalSplit);
		Document doc = UtilsJsoup.getRebuildDoc(e.baseUri(), e, arrs[0], arrs[1]);
		if (doc == null) return null;
		return doc;
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param source Element
	 * @param start String
	 * @param end String
	 * @return Element
	 */
	@Deprecated
	public static final Element getElement(Element source, String start, String end) {
		if (source == null) return null;
		String result = source.html();
		String head = UtilsReadURL.getUrlDomain(source);
		String cut = UtilsString.cutString(result, start, end);
		if (cut == null) return null;
		return UtilsJsoupCase.getDocument(cut, head);
	}

	/**
	 * 读取Element得到 name 这个字符串，id class tag attribname,text，所有单元 过滤掉重复项<br>
	 * 以双下划线区分，为最高优先等级,下划线区分切块次级，即先判断下划线，如果有，则切块，后再以,进行多个区分<br>
	 * @param f Element
	 * @param arr String[]
	 * @return Elements
	 */
	public static final Elements getElementAll(Element f, String... arr) {
		if (f == null) return new Elements();
		Elements es = new Elements();
		String[] arrs = UtilsArrays.getJsoupRuleKey(arr);
		for (String e : arrs) {
			Elements ess = getElementPrivate(f, e);
			if (ess != null && ess.size() > 0) es.addAll(ess);
		}
		/*
		 * for (String e : arr) {//*************************************************************************
		 * if (e.indexOf("||") < 0) {
		 * es.addAll(getElementPrivate(f, e));
		 * continue;
		 * }
		 * String[] arrs = e.split("\\|\\|");
		 * for (String g : arrs)
		 * es.addAll(getElementPrivate(f, g));
		 * }
		 */
		if (es.size() > 1) es = UtilsList.distinctHtml(es);
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
	 * 获取第一个节点的text内容<br>
	 * 如果没有找到，则返回null
	 * @param url String
	 * @param arr String[]
	 * @return String
	 */
	public static final String getElementAllFirstText(String url, String... arr) {
		Element e = getElementFirst(url, arr);
		if (e == null) return null;
		return e.text();
	}

	/**
	 * 读取Element得到 name 这个字符串，id class tag attribname,text，第一个节点<br>
	 * 如果没有找到，则返回null
	 * @param f Element
	 * @param arr String[]
	 * @return Element
	 */
	public static final Element getElementFirst(Element f, String... arr) {
		return getElementSingle(f, 0, arr);
	}

	/**
	 * 读取url得到 arrs 这个多字符串，id class tag attribname,text，第一个节点<br>
	 * 如果没有找到，则返回null
	 * @param url String
	 * @param arr String[]
	 * @return Element
	 */
	public static final Element getElementFirst(String url, String... arr) {
		return getElementSingle(url, 0, arr);
	}

	/**
	 * 获取第一个节点的text内容<br>
	 * 如果没有找到，则返回null
	 * @param f Element
	 * @param arr String[]
	 * @return String
	 */
	public static final String getElementFirstText(Element f, String... arr) {
		Element e = getElementFirst(f, arr);
		if (e == null) return null;
		return e.text();
	}

	/**
	 * 读取Element得到 name 这个字符串，id class tag attribname,text，所有单元 过滤掉重复项<br>
	 * 以下划线区分切块，即先判断下划线，如果有，则切块，后再以,进行多个区分，允许精确定位<br>
	 * @param f Element
	 * @param key String
	 * @return Elements
	 */
	private static final Elements getElementPrivate(Element f, String key) {
		if (key == null) return new Elements();
		key = key.trim();
		if (key.length() == 0) return new Elements();
		Elements es = new Elements();
		if (key.indexOf(ACC_JsoupRulePrecisePositioningOutPage) > -1) {
			Elements ees = relationKey(f, key);
			es.addAll(ees);
		}else {
			if (key.indexOf(ACC_JsoupRulePrecisePositioningInPage) > -1) {
				Elements ees = forwardKey(f, key);
				es.addAll(ees);
			} else {
				if (isCutInterval(key)) {
					Elements cut = getElementsByCut(f, key);
					if (cut != null) es.addAll(cut);

				} else {
					es.addAll(getElementsByID(f, key));
					es.addAll(getElementsByClass(f, key));
					es.addAll(getElementsByTag(f, key));
					//es.addAll(getElementsByAttribute(f, key));
					//es.addAll(getElementsByText(f, key));
					es.addAll(getElementsByHref(f, key));
				}
			}
		}
		if (es.size() > 1) es = UtilsList.distinctHtml(es);
		return es;
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param url String
	 * @param start String
	 * @param end String
	 * @return Elements
	 */
	public static final Elements getElements(String url, String start, String end) {
		if (UtilsConsts.isErrorUrl(url)) return null;
		Document doc = getDoc(url);
		String head = UtilsReadURL.getUrlDomain(url);
		return getRebuildElements(head, doc, start, end);
	}

	/**
	 * 通过 不同的关键字 查找节点["","abc","{I}def,{C}abc"]，并去重<br>
	 * style:0 Id<br>
	 * style:1 Class<br>
	 * style:2 Tag<br>
	 * style:3 Attrkey<br>
	 * style:4 tExtkey<br>
	 * @param source Element
	 * @param style int
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements getElementsBy(Element source, int style, String... arrs) {
		if (source == null || arrs.length == 0) return new Elements();
		Elements es = new Elements();
		String[] arr = UtilsArrays.jsoupSingleKeyArray(arrs);
		for (String e : arr) {
			if (!isGenericKeyType(e, style)) continue;
			String key = UtilsJsoup.getGenericKeyVal(e);
			String v = getGenericKeyRange(e);
			int[] surplus = UtilsArrays.getArrayInteger(v);
			Elements els = getElementsPrivate(source, key, style);
			if (els == null || els.size() == 0) continue;
			if (surplus.length == 0) {
				es.addAll(els);
				continue;
			}
			/* 只提取指定的下标记录 */
			//Arrays.sort(surplus);
			int len = els.size();
			for (int vv : surplus) {
				if (vv >= 0 && vv < len) es.add(els.get(vv));
			}
		}
		if (es.size() > 1) es = UtilsList.distinctHtml(es);/* 去重 */
		return es;
	}

	/**
	 * 通过attribute属性名称查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
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
	 * 以|to|进行区分切块
	 * @param e Document
	 * @param key String
	 * @return Elements
	 */
	public static final Elements getElementsByCut(Document e, String key) {
		if (e == null || key == null || key.length() == 0) return new Elements();
		if (!isCutInterval(key)) return new Elements();
		String[] arrs = key.split(ACC_JsoupRuleCutIntervalSplit);
		return UtilsJsoup.getRebuildElements(e.baseUri(), e, arrs[0], arrs[1]);
	}

	/**
	 * 以|To|线进行区分切块
	 * @param e Element
	 * @param key String
	 * @return Elements
	 */
	public static final Elements getElementsByCut(Element e, String key) {
		if (e == null || key == null || key.length() == 0) return new Elements();
		if (!isCutInterval(key)) return new Elements();
		String[] arrs = key.split(ACC_JsoupRuleCutIntervalSplit);
		return UtilsJsoup.getRebuildElements(e.baseUri(), e, arrs[0], arrs[1]);
	}

	/**
	 * 通过href内容查找节点
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements getElementsByHref(Element source, String... arrs) {
		return getElementsBy(source, 5, arrs);
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

	public static final void getElementsByKeyRange(Elements count, Elements els, int... surplus) {
		if (surplus.length == 0) return;

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
	public static final Elements getElementsByText(Element source, String... arrs) {
		return getElementsBy(source, 4, arrs);
	}

	/**
	 * 读取Element得到 name 这个字符串，id class tag attribname,text，第 index下标 节点<br>
	 * 如果没有找到，则返回null
	 * @param f Element
	 * @param index int
	 * @param arr String[]
	 * @return Element
	 */
	public static final Element getElementSingle(Element f, int index, String... arr) {
		if (index < 0) return null;
		Elements es = getElementAll(f, arr);
		if (es == null || es.size() == 0) return null;
		if (index >= es.size()) return null;
		return es.get(index);
	}

	/**
	 * 读取url得到 arrs 这个多字符串，id class tag attribname,text，第 index下标 节点<br>
	 * 如果没有找到，则返回null
	 * @param url String
	 * @param index int
	 * @param arr String[]
	 * @return Element
	 */
	public static final Element getElementSingle(String url, int index, String... arr) {
		if (index < 0) return null;
		Elements es = getElementAll(url, arr);
		if (es.size() == 0) return null;
		if (index >= es.size()) return null;
		return es.get(index);
	}

	/**
	 * 获取第一个节点的text内容<br>
	 * 如果没有找到，则返回null
	 * @param f Element
	 * @param arr String[]
	 * @return String
	 */
	public static final String getElementSingleText(Element f, int index, String... arr) {
		Element e = getElementSingle(f, index, arr);
		if (e == null) return null;
		return e.text();
	}

	/**
	 * 获取节点中的关键字，按不同方式查找，并去重<br>
	 * 此关键字为无修饰关键字。即纯关键字
	 * style:0 id<br>
	 * style:1 class<br>
	 * style:2 tag<br>
	 * style:3 attrkey<br>
	 * style:4 textkey<br>
	 * style:5 hrefkey<br>
	 * @param source Element
	 * @param key String
	 * @param style int
	 * @return Elements
	 */
	private static final Elements getElementsPrivate(Element source, String key, int style) {
		Elements es = new Elements();
		if (source == null || key == null) return es;
		key = key.trim();
		if (key.length() == 0) return es;
		switch (style) {
		case -1:/* S */
			if (!isCutInterval(key)) return new Elements();
			String[] arrs = key.split(ACC_JsoupRuleCutIntervalSplit);
			es= UtilsJsoup.getRebuildElements(source.baseUri(), source, arrs[0], arrs[1]);
			//es = source.getElementsByClass(e);aaa
			break;
		case 1:/* 1 */
			es = source.getElementsByClass(key);
			break;
		case 2:/* 2 */
			es = source.getElementsByTag(key);
			break;
		case 3:/* 3 */
			es = source.getElementsByAttribute(key);
			break;
		case 4:/* 4 */
			es = source.getElementsContainingText(key);
		case 5:
			Elements all = source.select("a[href]");
			for (Element f : all) {
				String absHref = f.attr("abs:href");
				if (absHref != null && absHref.indexOf(key) > -1) es.add(f);
			}
			break;
		default:/* 0 */
			Element r = source.getElementById(key);
			if (r != null) es.add(r);
			es.addAll(Collector.collect(new Evaluator.Id(key), source));
			break;
		}
		if (es.size() > 1) es = UtilsList.distinctHtml(es);/* 去重 */
		return es;
	}

	/**
	 * 返回检索关键的范围判断，如没有限制，则返回null，否则返回类型字符串，即{}之间的字符串<br>
	 * 以{}为开头的字符串，如:"{ICAT}classname1[]","{T}IDname1[1,2,5]"<br>
	 * 返回""或1,2,5
	 * @param key String
	 * @return String
	 */
	public static final String getGenericKeyRange(String key) {
		return UtilsStringPrefix.getMiddle(key, true);//UtilsRegular.geCheckIntervalVal(ACC_SearchRangePattern, key);
	}

	/**
	 * 返回检索关键的类型判断，如没有限制，则返回null，否则返回类型字符串，即{}之间的字符串<br>
	 * 以{}为开头的字符串，如:"{ICAT}classname1","{T}IDname1"<br>
	 * 返回ICAT、T
	 * @param key String
	 * @return String
	 */
	public static final String getGenericKeyType(String key) {
		return UtilsStringPrefix.getBig(key, false);//UtilsRegular.geCheckIntervalVal(ACC_SearchTypePattern, key);
	}

	/**
	 * 返回除去限定词之外的关键词<br>
	 * 以{}为开头的字符串<br>
	 * 例如:"{ICATH}classname1","{T}IDname1[0,1,2]"<br>
	 * 返回classname1,IDname1
	 * @param key String
	 * @return String
	 */
	public static final String getGenericKeyVal(final String key) {
		if (key == null || key.length() == 0) return key;
		return UtilsStringPrefix.getValue(key);
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
	 * 从网址中某个长度的局部返回对象
	 * @param domain String
	 * @param doc Document
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Document getRebuildDoc(String domain, Document doc, String start, String end) {
		if (doc == null) return null;
		String result = doc.html();
		String cut = UtilsString.cutString(result, start, end);
		if (cut == null) return null;
		return UtilsJsoupCase.getDocument(cut, domain);
	}

	/**
	 * 把字符串转成 Document，内容为null里则返回null
	 * @param domain String
	 * @param content String
	 * @return Document
	 */
	public static final Document getRebuildDoc(String domain, String content) {
		return UtilsJsoupCase.getDocument(content, domain);
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param domain String
	 * @param doc Document
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Elements getRebuildElements(String domain, Document doc, String start, String end) {
		if (doc == null) return new Elements();
		return getRebuildElementsByString(domain, doc.html(), start, end);
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param domain String
	 * @param e Element
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Elements getRebuildElements(String domain, Element e, String start, String end) {
		if (e == null) return new Elements();
		return getRebuildElementsByString(domain, e.html(), start, end);
	}

	/**
	 * 从网址中某个长度的局部返回对象
	 * @param domain String
	 * @param result String
	 * @param start String
	 * @param end String
	 * @return Document
	 */
	public static final Elements getRebuildElementsByString(String domain, String result, String start, String end) {
		if (result == null) return new Elements();
		Elements es = new Elements();
		String[] arr = UtilsString.cutStrings(result, start, end);
		for (String f : arr) {
			Element t = UtilsJsoupCase.getDocument(f, domain);
			if (t == null) continue;
			es.add(t);
		}
		return es;
	}

	@Deprecated
	private static final boolean intervalKeywords(List<String> list, String key, String... intervals) {
		boolean isInterval = false;
		for (String f : intervals) {
			if (key.indexOf(f) > -1) {
				if (f.equals("|||")) {
					f = "\\|\\|\\|";
				} else {
					if (f.equals("||")) f = "\\|\\|";
				}
				String[] arrs = key.split(f);
				for (String g : arrs) {
					g = g.trim();
					if (g.length() == 0) continue;
					list.add(g);
					isInterval = true;
				}
			}
		}
		return isInterval;
	}

	/**
	 * 判断是否含有间断关键字
	 * @param key String
	 * @return boolean
	 */
	public static final boolean isCutInterval(String key) {
		if(key==null || key.length()==0)return false;
		String[] arr=key.split(ACC_JsoupRuleCutIntervalSplit);
		return arr.length>1;
	}
	/**
	 * 判断Element是否为空
	 * @param e Element
	 * @return boolean
	 */
	public static final boolean isEmpty(Element e) {
		if (e == null) return false;
		String result = e.html().trim();
		result = UtilsHtmlFilter.filter(result);
		return result.length()!=0;
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
		String[] arr = { "{IH}abe[0,1]", "abcc[a, ,-1,5]", "{ICE}XX", "{IH}XX[0,1,2]", "{abc}aa[1]", "de[1,2]f{123}", "a{XY}z", "{ICAT}classname" };
		boolean a0 = false;
		if (a0) for (String e : arr) {
			String val = getGenericKeyType(e);
			String va = getGenericKeyVal(e);
			String v = getGenericKeyRange(e);
			int[] t = UtilsArrays.getArrayInteger(v);
			System.out.print(":" + e + ": \t :");
			System.out.print(val);
			System.out.print(": \t :");
			System.out.print(va);
			System.out.print(":\t:");
			System.out.print(v);
			System.out.print(":\t:");
			System.out.print(Arrays.toString(t));
			System.out.println();
		}
		System.out.println("=====================================");
		String line = "a,b;c|d e|f,g;h,iii&hh";
		String[] arrs = line.split("[,;\\| &]");
		for (String ff : arrs)
			System.out.print("\t:" + ff);
		System.out.println("=====================================");
		String[] a22 = { "a;c", "a|b", "a||b", "a|||b", "c||e|f", "&gt;d&gt;|&gt;e&gt;" };
		//String[] a22= {"a|||b"};
		for (String ff : a22)
			System.out.print(ff + "\t");
		System.out.println();
		String[] a222 = decomposeKey1111(a22);
		for (String ff : a222)
			System.out.print("::XXX::" + ff);
		System.out.println();
		//String lines="abc|||def";
		//System.out.println("lines:"+lines.indexOf("|||"));
		//System.out.println("lines:"+lines.split("\\|\\|\\|")[0]);
		//System.out.println("lines:"+lines.split("\\|\\|\\|")[1]);
		/*
		 * int[] aa = { -10, 5, 9, 12, 8, 2, 16 };
		 * Arrays.sort(aa);
		 * for (int ff : aa)
		 * System.out.print("\t:" + ff);
		 * String str = "正文\n      </dt>|</dl>||正文</dt>|</dl>";
		 * String[] ar = str.split("\\|\\|");
		 * for (String e : ar)
		 * System.out.println("e:" + e);
		 * String content = "<div class='a1'>aa<div class='a2'>bb<div class='a3'>cc</div></div></div>";
		 * content += "<div class='a1'>aa<div class='a2'>bb</div></div>";
		 * content += "<div class='a1'>aa<div class='a2'>bb<div class='a3'>c<div class='a4'>dd</div>c</div></div></div>";
		 * Document doc = UtilsJsoup.getDoc("http//www.99114.com/", content);
		 * Element obj = doc.body();
		 * String key = "a1=>a2";
		 * Elements es = UtilsJsoup.forwardKey(obj, key);
		 * for (Element e : es)
		 * System.out.println("eeee:" + e.html());
		 */
		/*
		 * //File file = UtilsReadURL.downfile("https://www.book9.info/modules/article/txtarticle.php?id=3473", "G:/Download/");
		 * //BooksReadUtils.movefile(file);
		 * {
		 * String url = "http://www.blpv.cn/?book/84064.html";
		 * Element t = UtilsJsoup.getRelationFirst(url, "btopt=>章节目录</b>|</ul>=>content");
		 * if (t == null) {
		 * System.out.println("t:null");
		 * } else {
		 * System.out.println("==" + t.html());
		 * }
		 * }
		 */
	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点
	 * @param e Element
	 * @param keyarr String[]
	 */
	public static final Elements relation(Element e, String... keyarr) {
		Elements summary = new Elements();
		if (e == null || keyarr.length == 0) return summary;
		relation(summary, e, keyarr, 0);
		return summary;
	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点
	 * @param summary Elements
	 * @param e Element
	 * @param keyarr String[]
	 * @param index int
	 */
	private static final void relation(Elements summary, Element e, String[] keyarr, int index) {
		if (e == null) return;
		int len = keyarr.length;
		if (index >= len) return;
		String key = keyarr[index];
		if (key == null) return;
		key = key.trim();
		if (key.length() == 0) return;
		Elements es = UtilsJsoupLink.getHrefElementsAll(e, key);
		if (index == len - 1) {
			summary.addAll(es);
			return;
		}
		for (Element f : es) {
			List<String> list = UtilsJsoupLink.getHrefAll(f);
			list = UtilsList.distinct(list);
			for (String url : list) {
				Element t = UtilsJsoup.getDoc(url);
				if (t == null) continue;
				relation(summary, t, keyarr, index + 1);
			}
		}

	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点<br>
	 * @param url String
	 * @param keyarr String[]
	 */
	public static final Elements relation(String url, String... keyarr) {
		Elements all = new Elements();
		if (url == null || url.length() == 0) return all;
		if (keyarr.length == 0) return all;
		Elements es = UtilsJsoup.getAllElementsByKey(url, keyarr[0]);
		if (keyarr.length == 1) return es;
		for (Element e : es) {
			Elements ess = new Elements();
			relation(ess, e, keyarr, 1);
			all.addAll(ess);
		}
		return all;
	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点<br>
	 * 以"|=&gt;|"为间隔
	 * @param e Element
	 * @param key String
	 */
	public static final Elements relationKey(Element e, String key) {
		if (e == null || key == null || key.length() == 0) return new Elements();
		if (key.indexOf(ACC_JsoupRulePrecisePositioningOutPage) == -1) return new Elements();
		String[] keyarr = key.split(ACC_JsoupRulePrecisePositioningOutPageSplit);
		return relation(e, keyarr);
	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点<br>
	 * 以"|=&gt;|"为间隔
	 * @param key String
	 */
	public static final Elements relationKey(String url, String key) {
		if (url == null || url.length() == 0 || key == null || key.length() == 0) return new Elements();
		if (key.indexOf(ACC_JsoupRulePrecisePositioningOutPage) == -1) return new Elements();
		String[] keyarr = key.split(ACC_JsoupRulePrecisePositioningOutPageSplit);
		if (keyarr.length == 0) return new Elements();
		return relation(url, keyarr);
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
	 * 移除所有text为空的区块
	 * @param source Element
	 * @return Element
	 */
	public static final Element remove_TextEmptyAll(Element source) {
		return remove_TextEmptyAll(source, false);
	}

	/**
	 * 移除所有text为空的区块<br>
	 * 是否过滤所有&lt;&gt;标记
	 * @param source Element
	 * @param filterTags boolean
	 * @return Element
	 */
	public static final Element remove_TextEmptyAll(Element source, boolean filterTags) {
		Elements childen = source.getAllElements();
		for (Element e : childen) {
			if (e == null) continue;
			String result = UtilsHtmlFilter.filterHtmlSymbol(e.html()).trim();
			if (result.length() == 0) e.remove();
		}
		return source;
	}

	public static final Elements remove_TextEmptyAll(Elements es, boolean filterTags) {

		return es;
	}

	/**
	 * 移除所有text为空的子类区块
	 * @param source Element
	 * @return Element
	 */
	public static final Element remove_TextEmptyChild(Element source) {
		Elements childen = source.children();
		for (Element e : childen) {
			if (e.text().trim().length() == 0) e.remove();
		}
		return source;
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
