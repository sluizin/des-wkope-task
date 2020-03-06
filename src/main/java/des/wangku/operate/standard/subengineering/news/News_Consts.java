package des.wangku.operate.standard.subengineering.news;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.desktop.DesktopUtils;
import des.wangku.operate.standard.utls.UtilsFile;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class News_Consts {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(News_Consts.class);
	static final String jsonstr = ",{\"name\": \"%s\",\"urlkey\": [\"%s\"],\"code\":\"%s\",\"blockid\": [\"\"],\"filterblockkey\": []}";

	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	static final int ThreadRunPoolMax = 10;
	/**
	 * 
	 */
	static final String ACC_Nbsp = Jsoup.parse("&nbsp;").text();

	static List<News_Mapping> MappingList = new ArrayList<News_Mapping>();

	/** 调用browser时需要的一些头部字符串 */
	static final String[] ACC_BrowserHeaders = { "Accept: */*", "Accept-Language: zh-cn", "Content-Type: application/x-www-form-urlencoded", // 建议要有
			"Accept-Encoding: gzip, deflate", "Cache-Control: no-cache" };

	/**
	 * 得到映射规则，按最小下标进得提取判断
	 * @param newUrl URL
	 * @return Mapping
	 */
	static final News_Mapping getMappingIndexMin(URL newUrl) {
		int min = -1;
		News_Mapping e = null;
		for (int i = 0, len = News_Consts.MappingList.size(); i < len; i++) {
			News_Mapping ee = News_Consts.MappingList.get(i);
			int index = ee.isExistIndexMin(newUrl);
			if (index > -1 && (min == -1 || min > index)) {
				min = index;
				e = ee;
			}
		}
		return e;
	}

	public static final String[] ACC_SourceItems = { "百度", "搜狗" };

	/**
	 * @param index int
	 * @return AbstractNewsSource
	 */
	public static final AbstractNewsSource getSource(int index) {
		switch (index) {
		case 1:
			return new NewsSource_Sogou();
		default:
			return new NewsSource_Baidu();
		}
	}

	static final String[] DefaultBlockidArrs = { "TRS_Editor", "newsContent", "article-content", "Cnt-Main-Article-QQ", "artibody", "content", "article" };

	/**
	 * 强制初始化
	 */
	public static final void mappingInit() {
		MappingList.clear();
		String filename = DesktopUtils.getJarBasicPathmodel() + "/des-wkope-task-p01-readUrl-htmlmapping.json";
		String content = UtilsFile.readFile(filename, "").toString();
		logger.debug("提取json文件:"+filename);
		MappingList = JSON.parseArray(content, News_Mapping.class);
		for (int i = News_Consts.MappingList.size() - 1; i >= 0; i--) {
			News_Mapping item = MappingList.get(i);
			if (!item.isSafe()) MappingList.remove(item);
		}
	}
	/**
	 * 清空记录
	 */
	public static final void mappingClean() {
		MappingList.clear();
	}
}
