package des.wangku.operate.standard.subengineering;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.utls.UtilsConsts;
import des.wangku.operate.standard.utls.UtilsJsoupCase;
import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsRnd;

/**
 * 阿里企业信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class AlibabaEnterpriseClass {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AlibabaEnterpriseClass.class);
	public static String businessNoHead="sj";
	int page=0;
	/** 企业编号 */
	String number = "";
	/** 企业名称 */
	String name = "";
	/** 企业网址 */
	String url = "";
	/** 主营产品 */
	String mainProduct = "";
	/** 经营模式 */
	String managementModel = "";
	/** 所在地 */
	String local = "";
	/** 加工方式 */
	String processingmode = "";
	/** 员工人数 */
	String employee = "";
	/** 厂房面积 */
	String workshoparea = "";
	/** 工艺类型 */
	String processType = "";
	/** 累计成交数 */
	String cumulNumTrans = "";
	/** 累计买家数 */
	String cumulNumBuyers = "";
	/** 重复采购率 */
	String repeatPurchRate = "";
	/** 成立时间 */
	String foundingtime = "";
	/** 注册资本 */
	String zczb = "";
	/** 经营范围 */
	String jyfw = "";
	/** 注册地址 */
	String zcdz = "";
	/** 联系人 */
	String linkman = "";
	/** 女士 */
	String linkmantype = "";
	/** 职位 */
	String linkmanjob = "";
	/** 简介 */
	String content = "";
	/** 电话 */
	String tel = "";
	/** 移动电话 */
	String mobile = "";
	/** 传真 */
	String fax = "";
	/** 地址 */
	String address = "";
	/** 邮编 */
	String postcode = "";
	/** 公司主页 */
	String copweb = "";

	public String[] toTableArray() {
		String[] arr = { number, name, url,page+"", mainProduct, managementModel, local, processingmode, employee, workshoparea, processType, cumulNumTrans, cumulNumBuyers, repeatPurchRate, foundingtime, zczb, jyfw, zcdz, linkman, linkmantype,
				linkmanjob, content, tel, mobile, fax, address, postcode, copweb };
		return arr;
	}

	public AlibabaEnterpriseClass() {
		number = businessNoHead + UtilsRnd.getNewFilenameNow(4, 1);
	}

	public void setElements(Element... details) {
		String value;
		for (Element ediv : details) {
			if (ediv == null) continue;
			Elements divs = ediv.getElementsByTag("div");
			for (Element t : divs) {
				if (t.html().indexOf("<div") > -1) continue;
				value = getStringTextValue(t, "主营产品", "mainProduct");
				if (value != null) mainProduct = value;
				if (t.html().indexOf("经营模式") > -1) {
					Element ttt = t.getElementsByTag("b").first();
					if (ttt != null) managementModel = ttt.text();
				}
				value = getStringTextValue(t, "所在地", "local");
				if (value != null) local = value;
				value = getStringTextValue(t, "员工人数", "employee");
				if (value != null) employee = value;
				value = getStringTextValue(t, "加工方式", "detail");
				if (value != null) processingmode = value;
				value = getStringTextValue(t, "厂房面积", "detail");
				if (value != null) workshoparea = value;
				value = getStringTextValue(t, "工艺类型", "detail");
				if (value != null) processType = value;
				value = getStringTextValue(t, "累计成交数", "detail");
				if (value != null) cumulNumTrans = value;
				value = getStringTextValue(t, "累计买家数", "detail");
				if (value != null) cumulNumBuyers = value;
				value = getStringTextValue(t, "重复采购率", "detail");
				if (value != null) repeatPurchRate = value;

			}

		}
	}

	public static String getStringTextValue(Element t, String text, String key) {
		if (t == null) return null;
		if (t.html().indexOf(text) == -1) return null;
		return getStringText(t, key);
	}

	/**
	 * 通用得到offer-stat 的内容
	 * @param e Element
	 * @param key String
	 * @return String
	 */
	public static String getStringText(Element e, String key) {
		if (e == null) return null;
		Element t = e.getElementsByAttributeValue("offer-stat", key).first();
		if (t == null) return null;
		return t.text();
	}

	/**
	 * 档案信息
	 */
	public void getArchives() {
		String urlString = this.url + "/page/creditdetail.htm";
		Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a
		try {
			Document document = connect.timeout(10000).maxBodySize(0).get();
			if (document == null) return;
			this.content = UtilsJsoupCase.getTextFirstByClass(document, "detail-info");
			Elements ess = document.getElementsByClass("info-right");
			Element es = ess.first();
			if (es == null) return;
			Elements trs = es.select("table").select("tr");
			for (Element tr : trs) {
				Element tdkey = tr.getElementsByClass("tb-key").first();
				if (tdkey == null) continue;
				String value;
				value = getTbvaluedataTR(tr, "tb-value", "成立时间");
				if (value != null) foundingtime = value;
				value = getTbvaluedataTR(tr, "tb-value", "注册资本");
				if (value != null) zczb = value;

				value = getTbvaluedataTR(tr, "tb-value-data", "经营范围");
				if (value != null) jyfw = value;
				value = getTbvaluedataTR(tr, "tb-value-data", "注册地址");
				if (value != null) zcdz = value;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 多tr中提取td-key的内容，如果发现关键字，则返回class值为classname的，
	 * @param tr Element
	 * @param classname String
	 * @param key String
	 * @return String
	 */
	static final String getTbvaluedataTR(Element tr, String classname, String key) {
		Element tdkey = tr.getElementsByClass("tb-key").first();
		if (tdkey == null) return null;
		if (tdkey.html().indexOf(key) == -1) return null;
		Element tdvalue = tr.getElementsByClass("tb-value-data").first();
		if (tdvalue == null) return "";
		return tdvalue.text();
	}

	/**
	 * 联系方式
	 */
	public void getContactinformation() {
		String urlString = this.url + "/page/contactinfo.htm";
		Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
		try {
			Document document = connect.timeout(10000).maxBodySize(0).get();
			if (document == null) return;
			Elements ess = document.getElementsByClass("props-part");
			Element es = ess.first();
			if (es == null) return;
			this.linkman = UtilsJsoupCase.getTextFirstByClass(es, "membername");
			Element info = es.getElementsByClass("contact-info").first();
			if (info != null) {
				Element dl = info.select("dl").select("dd").first();
				if (dl != null) {
					Elements dlall = dl.getAllElements();
					for (Element dlallone : dlall) {
						dlallone.remove();
					}
					String[] manString = dl.text().split(ACC_Nbsp);
					if (manString.length >= 1) linkmantype = manString[1];
					if (manString.length >= 3) linkmanjob = manString[2];
				}
			}
			Element contcatinfo = es.getElementsByClass("contcat-desc").first();
			if (contcatinfo != null) {
				Elements dls = contcatinfo.select("dl");
				for (Element dl : dls) {
					if (dl.html().indexOf("电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话") > -1) {
						Element tt = dl.select("dd").first();
						if (tt != null) this.tel = tt.text();
					}
					if (dl.html().indexOf("移动电话") > -1) {
						Element tt = dl.select("dd").first();
						if (tt != null) this.mobile = tt.text();
					}
					if (dl.html().indexOf("传&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;真") > -1) {
						Element tt = dl.select("dd").first();
						if (tt != null) this.fax = tt.text();
					}
					if (dl.html().indexOf("地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址") > -1) {
						Element tt = dl.select("dd").first();
						if (tt != null) this.address = tt.text();
					}
					if (dl.html().indexOf("邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;编") > -1) {
						Element tt = dl.select("dd").first();
						if (tt != null) this.postcode = tt.text();
					}
					if (dl.html().indexOf("公司主页") > -1) {
						Element tt = dl.select("dd").first();
						if (tt != null) this.copweb = tt.text();
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	static final String ACC_Nbsp = Jsoup.parse("&nbsp;").text();

	/**
	 * 商家相册
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class EnterprisePictureClass {
		String number = "";
		String name = "";
		String url = "";
		String savefilePath = "";
		List<EnterprisePictureGroupClass> list = new ArrayList<>();

		/**
		 * 读相册图片，分组
		 */
		public void readpict() {
			list.clear();
			String urlString = this.url + "/page/albumlist.htm";
			logger.debug("urlString:" + urlString);
			String pageXml = UtilsReadURL.getReadUrlJs(urlString);
			//Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.header_a);
			//Document document = connect.timeout(timeout).maxBodySize(0).get();
			Document document = Jsoup.parse(pageXml, this.url);
			try {
				Element liss = document.getElementsByClass("app-albumColumn").first();
				if (liss == null) return;
				Elements lis = liss.select("li");
				for (int i = 0; i < lis.size(); i++) {
					Element li = lis.get(i);
					Element title = li.getElementsByClass("title").first();
					if (title == null) continue;
					EnterprisePictureGroupClass e = new EnterprisePictureGroupClass();
					e.groupname = title.text();
					Element picurlfirst = title.select("a[href]").first();
					if (picurlfirst == null) continue;
					String baiduUrl = picurlfirst.attr("abs:href");
					Element con = li.getElementsByClass("count").first();
					if (con == null) continue;
					//String content = con.text().trim();
					//int count = UtilsString.getNumbersInt(content, "\\d+张图片");
					//logger.debug(e.groupname + "\t" + baiduUrl + "\tcount:" + count);

					String picurlString = baiduUrl;//.substring(0, baiduUrl.indexOf('?'));
					//String pageXml2=UtilsReadURL.getReadUrlJs(picurlString);
					//Document docpic = Jsoup.parse(pageXml2,this.url);
					Connection connect = Jsoup.connect(picurlString).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
					Document docpic = connect.timeout(10000).maxBodySize(0).get();
					Element imagelistall = docpic.getElementById("image-list-container");
					if (imagelistall == null) return;
					Elements imagelist = imagelistall.select("li");
					for (Element lihref : imagelist) {
						String titlepage="";
						titlepage = lihref.select("a[href]").attr("title");
						String urlpage = lihref.select("img[src]").attr("abs:src");
						urlpage = urlpage.replaceAll(".64x64", "");
						e.urlList.add(urlpage);
						e.titleList.add(titlepage);
					}
					list.add(e);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public final String getNumber() {
			return number;
		}

		public final void setNumber(String number) {
			this.number = number;
		}

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final String getUrl() {
			return url;
		}

		public final void setUrl(String url) {
			this.url = url;
		}

		public final List<EnterprisePictureGroupClass> getList() {
			return list;
		}

		public final void setList(List<EnterprisePictureGroupClass> list) {
			this.list = list;
		}

		public final String getSavefilePath() {
			return savefilePath;
		}

		public final void setSavefilePath(String savefilePath) {
			this.savefilePath = savefilePath;
		}

	}

	/**
	 * 公司图片相册组
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class EnterprisePictureGroupClass {
		String groupname = "";
		List<String> titleList = new ArrayList<>();
		List<String> urlList = new ArrayList<>();

		public final String getGroupname() {
			return groupname;
		}

		public final void setGroupname(String groupname) {
			this.groupname = groupname;
		}

		public final List<String> getUrlList() {
			return urlList;
		}

		public final void setUrlList(List<String> urlList) {
			this.urlList = urlList;
		}

		public final List<String> getTitleList() {
			return titleList;
		}

		public final void setTitleList(List<String> titleList) {
			this.titleList = titleList;
		}
		
	}
}
