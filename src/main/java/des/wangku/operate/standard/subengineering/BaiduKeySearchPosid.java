package des.wangku.operate.standard.subengineering;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.TableItem;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.task.InterfaceThreadRunUnit;
import des.wangku.operate.standard.utls.UtilsConsts;
import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsSWTTableSQL;
import des.wangku.operate.standard.utls.UtilsString;

/**
 * 百度关键字搜索排名
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class BaiduKeySearchPosid {
	/** 日志 */
	static Logger logger = Logger.getLogger(BaiduKeySearchPosid.class);

	public static final String NoFindNull = "--";

	/**
	 * 得到baidu中的排名 如没检索到，则返回null<br>
	 * 如检索到，则返回第P页第N条
	 * @param maxPage int
	 * @param key String
	 * @param url String
	 * @return String
	 */
	@Deprecated
	public static final String getPosid(int maxPage, String key, String url) {
		int maxbaiduPage = (maxPage - 1) * 10;
		for (int i = 0; i <= maxbaiduPage; i += 10) {
			String urlString = "https://www.baidu.com/s?wd=" + key + "&pn=" + i;
			try {
				//URL u = new URL(urlString);
				Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.header_a);
				Document document = connect.timeout(15000).maxBodySize(0).get();
				if (document == null) return null;
				if (document.html().indexOf(url) == -1) continue;
				Elements es = document.getElementsByClass("result");
				for (int x = 0; x < es.size(); x++) {
					Element e = es.get(x);
					if (e.html().indexOf(url) > -1) {
						int page = (i / 10) + 1;
						return "第" + page + "页第" + (x + 1) + "条";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 得到baidu中的排名 如没检索到，则返回-1<br>
	 * 如检索到，则返回第N条 以0为第1条<br>
	 * 默认timeout:20000
	 * @param type String
	 * @param maxPage int
	 * @param key String
	 * @param url String
	 * @param cutUrl int
	 * @return String
	 */
	public static final int getBaiduPosid(String type, int maxPage, String key, String url, int cutUrl) {
		return getBaiduPosid(type, maxPage, key, url, 20000, cutUrl);
	}

	/**
	 * 得到baidu中的排名 如没检索到，则返回-1<br>
	 * 如检索到，则返回第N条 以0为第1条
	 * @param type String
	 * @param maxPage int
	 * @param key String
	 * @param url String
	 * @param timeout int
	 * @param cutUrl int
	 * @return String
	 */
	public static final int getBaiduPosid(String type, int maxPage, String key, String url, int timeout, int cutUrl) {
		int maxbaiduPage = (maxPage - 1) * 10;
		int p = -1;
		if (key == null || key.length() == 0) return -1;
		if (url == null || url.length() == 0) return -1;
		url = url.toLowerCase();
		String newUrl = url;
		newUrl = newUrl.replaceAll("http://", "");
		newUrl = newUrl.replaceAll("https://", "");
		newUrl = newUrl.toLowerCase();
		if (cutUrl > 0) newUrl = newUrl.substring(0, cutUrl);
		for (int i = 0; i <= maxbaiduPage; i += 10) {
			String urlString = "https://" + type + ".baidu.com/s?wd=" + key + "&pn=" + i;
			try {
				Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.header_a);
				Document document = connect.timeout(timeout).maxBodySize(0).get();
				if (document == null) return -1;
				if (document.html().toLowerCase().indexOf(newUrl) == -1) {
					p += 10;
					continue;
				}
				Elements es = document.getElementsByClass("result");
				for (int x = 0; x < es.size(); x++) {
					Element e = es.get(x);
					p++;
					if (e.html().toLowerCase().indexOf(newUrl) > -1) {
						if (cutUrl > 0) {
							Elements showurl = e.getElementsByClass("c-showurl");
							Elements as = showurl.select("a[href]");
							String searchurl = "";
							for (Element t1 : as) {
								String baiduUrl = t1.attr("abs:href");
								searchurl = UtilsReadURL.getRealLocation(baiduUrl);
								if (searchurl.toLowerCase().indexOf(url) > -1) return p;
							}
							/*
							 * logger.debug("oldurl:"+url);
							 * logger.debug("serurl:"+searchurl);
							 */
							continue;
						}
						return p;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	@SuppressWarnings("unused")
	public static final int getBaiduPosid2(String type, int maxPage, String key, String url, int timeout, int cutUrl) {
		String urlString = "https://" + type + ".baidu.com";

		return -1;
	}

	/**
	 * 把百度排行数量转成 第P页第N条 样式 否则返回null
	 * @param posid int
	 * @return String
	 */
	public static final String getBaiduPosidString(int posid) {
		if (posid < 0) return null;
		int p = posid / 10 + 1;
		int x = posid % 10 + 1;
		return "第" + p + "页第" + x + "条";
	}

	public static void main(String[] args) {
		int pp = getBaiduPosid("www", 10, "朝天椒", "ctj.99114.com", 0);
		System.out.println("pp:" + pp);
		System.out.println("posid:" + getBaiduPosidString(pp));

	}

	/**
	 * 百度关键字搜索排名单元<br>
	 * 大于0位置，如果没空或没找到，则返回"--"
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class BaiduWorkClass implements InterfaceThreadRunUnit {
		String type;
		String keyword;
		String url;
		int cutUrl = 0;
		TableItem tt;
		int y;
		String value;

		public BaiduWorkClass(String type, String keyword, String url, TableItem tt, int y) {
			this.type = type;
			this.keyword = keyword;
			this.url = url;
			this.tt = tt;
			this.y = y;
		}

		public BaiduWorkClass(String type, String keyword, String url, TableItem tt, int y, int cutUrl) {
			this.type = type;
			this.keyword = keyword;
			this.url = url;
			this.tt = tt;
			this.y = y;
			this.cutUrl = cutUrl;
		}

		public void run() {
			int p = BaiduKeySearchPosid.getBaiduPosid(type, 10, keyword, url, cutUrl);
			if (p == -1) value = NoFindNull;
			else value = "" + (p + 1);
			//value = BaiduSearch.getPosid(keyword, url);
		}

		public void show() {
			tt.setText(y, value);

		}

		public String getKeyword() {
			return keyword;
		}

		@Override
		public long getSleepTime() {
			return 1000;
		}
	}
	/**
	 * 得到某列的结果，总数与结果总数(排除没有结果数量)
	 * @param table ResultTable
	 * @param x1 int
	 * @param x2 int
	 * @param y int
	 * @return float[]
	 */
	public static final float[] getReusltTable(ResultTable table, int x1, int x2, int y) {
		float[] result = { 0, 0 };
		int count = 0, well = 0;
		for (int i = x1; i <= x2; i++) {
			String value = UtilsSWTTableSQL.get(table, i, y);
			if (value == null || value.length() == 0) continue;
			if (UtilsString.isNumber(value)) {
				well++;
				count++;
				continue;
			}
			if (value.equals(NoFindNull)) count++;
		}
		result[0] = count;
		result[1] = well;
		return result;
	}
}
