package des.wangku.operate.standard.subengineering.news;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.utls.UtilsReadURL;

public class NewsSource_Sogou extends AbstractNewsSource {

	public NewsSource_Sogou() {
		super(false, false, false, 20000);
	}
	public NewsSource_Sogou(boolean ishtml, boolean isscript, boolean isstyle, int timeout) {
		super(ishtml, isscript, isstyle, timeout);
	}
	@Override
	public void getNewsInforList(String key, int max) {
		for (int p = 0; p < (max / 10 + 1); p++) {
			String urlHttp = "/news?query=" + key + "&page=" + p;
			String content = UtilsReadURL.getRealContent("news.sogou.com", urlHttp, "GBK");
			Document doc = Jsoup.parse(content);
			Elements links = doc.getElementsByClass("vrwrap");
			int len = links.size();
			if (len >= 10) len = 9;
			loop: for (int i = 0; i < len; i++) {
				Element link = links.get(i);
				putNewsInfor(key, link);
				if (newsList.size() >= max) break loop;
			}
		}
	}

	@Override
	protected void analysis(News_Infor e, Element eResult) {
		if (eResult == null) return;
		String title = "", href = "", source = "", datetime = "", picture = "", summary = "";
		Elements titleElement = eResult.getElementsByClass("vrTitle");
		if (titleElement.size() > 0) title = titleElement.text();
		/* 链接地址 */
		Elements as = titleElement.select("a[href]");
		if (as.size() > 0) href = as.get(0).attr("abs:href");
		e.href = href;
		Elements pics = eResult.getElementsByClass("news-pic");
		if (pics.size() > 0) picture = pics.attr("abs:src");

		/* 内容 */
		Elements summarys = eResult.getElementsByClass("news-info");
		if (summarys.size() > 0) {
			Element esummary = summarys.get(0);
			Elements auths = esummary.getElementsByClass("news-from");
			if (auths.size() > 0) {
				String[] arr = auths.text().split(News_Consts.ACC_Nbsp);
				if (arr.length > 0) source = arr[0];
				if (arr.length > 1) datetime = arr[1].trim();
			}
			Elements es = esummary.getElementsByClass("summary_1");
			if (es.size() > 0) summary = es.get(0).text();

		}
		e.insertAttrib( title, href , source , datetime ,  picture , summary);

	}

	@Override
	public String getSourceName() {
		return "搜狗";
	}
}
