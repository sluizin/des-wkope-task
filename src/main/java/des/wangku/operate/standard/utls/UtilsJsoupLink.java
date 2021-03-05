package des.wangku.operate.standard.utls;


import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static des.wangku.operate.standard.utls.UtilsJsoupConst.ACC_JsoupRulePrecisePositioningOutPage;
/**
 * Jsoup工具<br>
 * 提取链接
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsJsoupLink {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsJsoupLink.class);

	/**
	 * 提取source中所有的链接，允许通过id、class等查找
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements getHrefElementsAll(Element source, String... arrs) {
		Elements result = new Elements();
		if (source == null) return result;
		if (!UtilsVerification.isRealVal(arrs)) return source.select("a[href]");
		Elements es = UtilsJsoup.getElementAll(source, arrs);
		for (Element e : es)
			result.addAll(e.select("a[href]"));
		return result;
	}
	/**
	 * 从Elements数组中删除没有链接的节点
	 * @param es Elements
	 * @return Elements
	 */
	public static final Elements removeNoHref(Elements es) {
		Elements nes=new Elements(es.size());
		for(Element e:es) 
			if(e.select("a[href]").size()>0)
				 nes.add(e);
		return  nes;
	}
	/**
	 * 从element中提取指定下标的链接
	 * @param f Element
	 * @param index int
	 * @param arrs String[]
	 * @return String
	 */
	public static final String getHref(Element f, int index, String... arrs) {
		List<String> list = getHrefAll(f, arrs);
		int size = list.size();
		if (size == 0) return null;
		if (index >= 0 && index < size) return list.get(index);
		return null;
	}
	/**
	 * 从element中提取第一个的链接
	 * @param f Element
	 * @return String
	 */
	public static final String getHrefFirst(Element f) {
		if(f==null)return null;
		Elements es = f.select("a[href]");
		if (es.size() == 0) return null;;
		return es.first().attr("abs:href");
	}

	/**
	 * 从element中提取所有的链接，允许通过id、class等查找 ，不去重<br/>
	 * 如果没有关键字，则提出所有链接<br/>
	 * @param f Element
	 * @param arrs String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getHrefAll(Element f, String... arrs) {
		if (f == null) return new ArrayList<>(0);
		if(arrs.length == 0)return getAllHrefListByElement(f);
		List<String> list = new ArrayList<>(1);
		Elements es = getHrefElementsAll(f, arrs);
		for (Element e : es)
			list.add(e.attr("abs:href"));
		return list;
	}

	/**
	 * 从url中提取所有的链接，允许通过id、class等查找 ，不去重
	 * @param url String
	 * @param arrs String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getHrefAll(String url, String... arrs) {
		Document doc = UtilsJsoupExt.getDoc(url);
		if (doc == null) return new ArrayList<>(0);
		return getHrefAll(doc, arrs);
	}

	/**
	 * 从f中提出所有链接地址，把链接地址是否含有数组中的关键字，如果有则输出
	 * @param f Element
	 * @param arrs String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getHrefAllIndexOf(Element f, String... arrs) {
		if (f == null || arrs.length == 0) return new ArrayList<>(0);
		List<String> list = new ArrayList<>(1);
		Elements es = f.select("a[href]");
		loop: for (Element e : es) {
			String val = e.attr("abs:href");
			for (String g : arrs) {
				if (val.indexOf(g) > -1) {
					list.add(val);
					continue loop;
				}
			}
		}
		return list;
	}

	/**
	 * 从url中提出所有链接地址，把链接地址是否含有数组中的关键字，如果有则输出
	 * @param url String
	 * @param arrs String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getHrefAllIndexOf(String url, String... arrs) {
		Document doc = UtilsJsoupExt.getDoc(url);
		return getHrefAllIndexOf(doc, arrs);
	}

	/**
	 * 从element中提取指定下标的链接
	 * @param f Element
	 * @param arrs String[]
	 * @return String
	 */
	public static final String getHrefFirst(Element f, String... arrs) {
		return getHref(f, 0, arrs);
	}

	/**
	 * 从element中提取指定下标的链接
	 * @param url String
	 * @param arrs String[]
	 * @return String
	 */
	public static final String getHrefFirst(String url, String... arrs) {
		Document doc = UtilsJsoupExt.getDoc(url);
		return getHref(doc, 0, arrs);
	}

	/**
	 * 从element中提取指定下标的文本
	 * @param f Element
	 * @param index int
	 * @param arrs String[]
	 * @return String
	 */
	public static final String getText(Element f, int index, String... arrs) {
		List<String> list = getTextAll(f, arrs);
		int size = list.size();
		if (size == 0) return null;
		if (index >= 0 && index < size) return list.get(index);
		return null;
	}

	/**
	 * 从element中提取所有的文本，允许通过id、class等查找 ，不去重
	 * @param f Element
	 * @param arrs String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getTextAll(Element f, String... arrs) {
		if (f == null || arrs.length == 0) return new ArrayList<>(0);
		List<String> list = new ArrayList<>(1);
		Elements es = getHrefElementsAll(f, arrs);
		for (Element e : es)
			list.add(e.text());
		return list;
	}

	/**
	 * 从url中提取所有的链接文本，允许通过id、class等查找 ，不去重
	 * @param url String
	 * @param arrs String[]
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getTextAll(String url, String... arrs) {
		Document doc = UtilsJsoupExt.getDoc(url);
		if (doc == null) return new ArrayList<>(0);
		return getTextAll(doc, arrs);
	}

	/**
	 * 从element中提取指定下标的文本
	 * @param f Element
	 * @param arrs String[]
	 * @return String
	 */
	public static final String getTextFirst(Element f, String... arrs) {
		return getText(f, 0, arrs);
	}

	/**
	 * 得到相联链接的最终单元<br>
	 * 初始地址，按=>进行查找链接，并得到最后关键字Element
	 * @param source Element
	 * @param keyString String
	 * @return String
	 */
	public static final Element getRelationFirst(Element source, String keyString) {
		if (source == null || keyString == null) return null;
		String[] arr = keyString.split(ACC_JsoupRulePrecisePositioningOutPage);
		String newurl = null;
		for (int i = 0, len = arr.length; i < len; i++) {
			String e = arr[i];
			if (e == null || (e = e.trim()).length() == 0) return null;
			if (i == 0) newurl = UtilsJsoupLink.getHrefFirst(source, e);/* 第一个时先提出目标地址 */
			if (i == len - 1) return UtilsJsoup.getElementFirst(source, e);
			newurl = UtilsJsoupLink.getHrefFirst(newurl, e);
		}
		return null;
	}

	/**
	 * 得到相联链接的最终单元<br>
	 * 初始地址，按=>进行查找链接，并得到最后关键字的Elmenet
	 * @param url String
	 * @param keyString String
	 * @return Element
	 */
	public static final Element getRelationFirst(String url, String keyString) {
		if (url == null || keyString == null) return null;//keyString.indexOf(ACC_JsoupRulePrecisePositioning) == -1
		String[] arr = keyString.split(ACC_JsoupRulePrecisePositioningOutPage);
		String newurl = url.trim();
		for (int i = 0, len = arr.length; i < len; i++) {
			String e = arr[i];
			if (e == null || (e = e.trim()).length() == 0) return null;
			if (i == len - 1) return UtilsJsoup.getElementFirst(newurl, e);
			newurl = UtilsJsoupLink.getHrefFirst(newurl, e);
		}
		return null;
	}

	/**
	 * 得到相联链接的最终单元<br>
	 * 初始地址，按=>进行查找链接，并得到最后关键字的链接
	 * @param source Element
	 * @param keyString String
	 * @return String
	 */
	public static final String getRelationHrefFirst(Element source, String keyString) {
		Element e = getRelationFirst(source, keyString);
		if (e == null) return null;
		Elements es = e.select("a[href]");
		if (es.size() == 0) return null;
		return es.first().attr("abs:href");
	}
	/**
	 * 得到相联链接的最终单元<br>
	 * 初始地址，按=>进行查找链接，并得到最后关键字的链接
	 * @param url String
	 * @param keyString String
	 * @return String
	 */
	public static final String getRelationHrefFirst(String url, String keyString) {
		Element e = getRelationFirst(url, keyString);
		if (e == null) return null;
		Elements es = e.select("a[href]");
		if (es.size() == 0) return null;
		return es.first().attr("abs:href");
	}
	/**
	 * 从Element中提取所有链接地址 ，不去重<br/>
	 * @param e Element
	 * @return List&lt;String&gt;p
	 */
	private static final List<String> getAllHrefListByElement(Element e){
		List<String> list=new ArrayList<>();
		String url = e.attr("abs:href");
		if(url!=null && url.length()>0)list.add(url);
		Elements es=e.select("a");
		for(Element f:es) {
			url=f.attr("abs:href");
			if(url==null || url.length()==0)continue;
			//if(list.contains(url))continue;
			list.add(url);
		}
		return list;
	}
	public static void main(String[] args) {
		
		String content = "<div class='a1'>aa<div class='a2'>bb<div class='a3'>cc</div><div id='ccd' href='ccc'>ff</div></div></div>";
		content += "<div class='a1'>aa<a href='/www/a.html' id='cce'>YYY</a><div class='a2'>bb</div></div>";
		content += "<div class='a1'>a<a href='/www/ab.html' id='ccd'>XXX</a>a<div class='a2'>bb<div class='a3'>c<div class='a4'>dd</div>c</div></div></div>";
		content +="<div class='a22'>eee</div>";
		content +="<div class='a22'></div>";
		content +="<div class='a22'><a href='/abc.html'>xxx</a></div>";
		Document doc = UtilsJsoupExt.getDocument(content,"http://www.99114.com/");
		Element obj = doc.body();
		List<String> list = UtilsJsoupLink.getHrefAll(obj, "ccd");
		for (String e : list) {
			System.out.println("e:" + e);
		}
		Elements es=obj.getElementsByClass("a22");
		for(Element e:es) {			
			System.out.println("ee:"+e.html());
		}
		es=UtilsJsoupLink.removeNoHref(es);
		for(Element e:es) {			
			System.out.println("eeee:"+e.html());
		}
		/*
		for(int ii=1;ii<50;ii++) {
			String url="http://www.99114.com/infolist/"+ii+".html";
			List<String> list=getHrefAll(url,"news-item->{T}li");
			for(int i=0;i<list.size();i++) {
				String e=list.get(i);
				UtilsFile.writeFile("e:/abc.txt", e+UtilsConsts.ACC_ENTER);
				System.out.println(i+":"+e);
			}
			
		}*/
	}

}
