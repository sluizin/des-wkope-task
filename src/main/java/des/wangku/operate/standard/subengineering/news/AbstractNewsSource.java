package des.wangku.operate.standard.subengineering.news;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.nodes.Element;

/**
 * 新闻采集源
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public abstract class AbstractNewsSource {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AbstractNewsSource.class);
	List<News_Infor> newsList = new ArrayList<>(10);
	/** 是否需要html代码 */
	boolean ishtml = false;
	/** 是否需要script */
	boolean isscript = false;
	/** 是否需要style */
	boolean isstyle = false;
	/** 每个url的反应时间，单位毫秒 */
	int timeout = 20000;

	public AbstractNewsSource(boolean ishtml, boolean isscript, boolean isstyle, int timeout) {
		this.ishtml = ishtml;
		this.isscript = isscript;
		this.isstyle = isstyle;
		this.timeout = timeout;
	}
	/**
	 * 得到基础记录数据，没有读取url
	 * @param key String
	 * @param max int
	 * @return List<NewsInfor>
	 */
	public abstract void getNewsInforList(String key, int max);
	/**
	 * 分析某一块的内容，没有读取url<br>
	 * 必须设置News_Infor中的href值
	 * @param e NewsInfor
	 * @param eResult Element
	 */
	protected abstract void analysis(News_Infor e,Element eResult) ;
	/**
	 * 信息来源引擎 百度、搜狗
	 * @return String
	 */
	public abstract String getSourceName();
	/**
	 * 插入新的信息，包括关键字，引擎
	 * @param list List<NewsInfor>
	 * @param key String
	 * @param link Element
	 */
	protected void putNewsInfor(String key,Element link) {
		News_Infor ee = new News_Infor(ishtml,isscript,isstyle,timeout);
		ee.map.put("searchkey", key);
		ee.map.put("engine", getSourceName());
		analysis(ee, link);
		newsList.add(ee);
	}
	public final List<News_Infor> getNewsList() {
		return newsList;
	}
	public final void setNewsList(List<News_Infor> newsList) {
		this.newsList = newsList;
	}
	public final boolean isIshtml() {
		return ishtml;
	}
	public final void setIshtml(boolean ishtml) {
		this.ishtml = ishtml;
	}
	public final boolean isIsscript() {
		return isscript;
	}
	public final void setIsscript(boolean isscript) {
		this.isscript = isscript;
	}
	public final boolean isIsstyle() {
		return isstyle;
	}
	public final void setIsstyle(boolean isstyle) {
		this.isstyle = isstyle;
	}
	public final int getTimeout() {
		return timeout;
	}
	public final void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
}
