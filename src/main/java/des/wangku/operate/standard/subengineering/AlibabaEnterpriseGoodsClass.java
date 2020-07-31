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
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsJsoup;
import des.wangku.operate.standard.utls.UtilsJsoup.RegularElement;
import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsString;

/**
 * 阿里里的商家供应商品
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class AlibabaEnterpriseGoodsClass {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AlibabaEnterpriseGoodsClass.class);

	public static class GoodsLine {
		String number = "";
		String name = "";
		String url = "";
		String baseuri="";
		String savefilePath = "";
		String categoryclass1 = "";
		String categoryclass2 = "";
		String goodsname = "";
		String goodsurl = "";
		String goodspictureold="";
		String goodspicturenew="";
		String goodsprice = "";
		String goodsattrib = "";
		String goodsdescription = "";
		String goodspriceexplain = "";
		String goodsdeliveryaddr = "";
		String goodsstarlevel="";
		String goodsbargainnumber="";

		public String[] toTableArray() {
			String[] arr = { number, name, url, categoryclass1, categoryclass2, goodsname, goodsurl,goodspictureold,goodspicturenew, goodsprice, goodsattrib, goodsdescription, goodspriceexplain, goodsdeliveryaddr,goodsstarlevel,goodsbargainnumber };
			return arr;
		}

		public void splitGoodsUrl() {
			Goods e = Goods.splitUrl(goodsurl);
			//logger.debug("savefilePath:"+savefilePath);
			goodspictureold=e.pictureold;
			String picturenew=UtilsFile.downloadPicture(goodspictureold, savefilePath);
			goodspicturenew = picturenew.replaceAll(baseuri, "");
			goodsprice = e.price;
			goodsattrib = e.attrib;
			goodsdescription = e.description;
			goodspriceexplain = e.priceexplain;
			goodsdeliveryaddr = e.deliveryaddr;
			goodsstarlevel=e.starlevel;
			goodsbargainnumber=e.bargainnumber;
		}

		public final void setBaseuri(String baseuri) {
			this.baseuri = baseuri;
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

		public final String getSavefilePath() {
			return savefilePath;
		}

		public final void setSavefilePath(String savefilePath) {
			this.savefilePath = savefilePath;
		}

		public final String getCategoryclass1() {
			return categoryclass1;
		}

		public final void setCategoryclass1(String categoryclass1) {
			this.categoryclass1 = categoryclass1;
		}

		public final String getCategoryclass2() {
			return categoryclass2;
		}

		public final void setCategoryclass2(String categoryclass2) {
			this.categoryclass2 = categoryclass2;
		}

		public final String getGoodsname() {
			return goodsname;
		}

		public final void setGoodsname(String goodsname) {
			this.goodsname = goodsname;
		}

		public final String getGoodsurl() {
			return goodsurl;
		}

		public final void setGoodsurl(String goodsurl) {
			this.goodsurl = goodsurl;
		}

		public final String getGoodsprice() {
			return goodsprice;
		}

		public final void setGoodsprice(String goodsprice) {
			this.goodsprice = goodsprice;
		}

		public final String getGoodsattrib() {
			return goodsattrib;
		}

		public final void setGoodsattrib(String goodsattrib) {
			this.goodsattrib = goodsattrib;
		}

	}

	public static final List<GoodsLine> getList(List<GoodsUnit> unitlist) {
		List<GoodsLine> list = new ArrayList<>();
		for (GoodsUnit e : unitlist) {
			List<GoodsCategoryClass> calist = e.list;
			for (GoodsCategoryClass g : calist) {
				List<Goods> goodslist = g.list;
				for (Goods gg : goodslist) {
					GoodsLine f = new GoodsLine();
					f.number = e.number;
					f.name = e.name;
					f.url = e.url;
					f.savefilePath = e.savefilePath;
					f.categoryclass1 = g.categorytitle;
					f.categoryclass2 = g.categorytitle2;
					f.goodsname = gg.title;
					f.goodsurl = gg.url;
					f.goodsprice = gg.price;
					f.goodsattrib = gg.attrib;
					f.baseuri=e.baseuri;
					list.add(f);
				}
			}
		}
		return list;
	}

	/**
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class GoodsUnit {
		String number = "";
		String name = "";
		String url = "";
		String baseuri="";
		String savefilePath = "";
		List<GoodsCategoryClass> list = new ArrayList<>();

		public void initialization() {
			list = getShopGoodsCategoryList(url);
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

		public final String getSavefilePath() {
			return savefilePath;
		}

		public final void setSavefilePath(String savefilePath) {
			this.savefilePath = savefilePath;
		}

		public final List<GoodsCategoryClass> getList() {
			return list;
		}

		public final void setList(List<GoodsCategoryClass> list) {
			this.list = list;
		}

		public final String getBaseuri() {
			return baseuri;
		}

		public final void setBaseuri(String baseuri) {
			this.baseuri = baseuri;
		}

	}

	/**
	 * shop1489855753263.1688.com中店铺-供应页中提取分类总 "店长推荐(38)"
	 * @param shopUrl String
	 * @return List &lt; GoodsCategoryClass &gt; 
	 */
	public static final List<GoodsCategoryClass> getShopGoodsCategoryList(String shopUrl) {
		List<GoodsCategoryClass> list = new ArrayList<>();
		String urlString = shopUrl + "/page/offerlist.htm";
		Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
		try {
			logger.debug("urlString:"+urlString);
			Document document = connect.timeout(20000).maxBodySize(0).get();
			if (document == null) return list;
			Element categoryDiv = document.getElementsByClass("wp-category").first();
			if (categoryDiv == null) return list;
			Elements ees = categoryDiv.getElementsByClass("wp-category-nav");
			Element categoryDiv0 = UtilsJsoup.getElementClassKeyword(ees, "wp-category-nav-title", "分类：");
			if (categoryDiv0 == null) return list;
			Element categorynavlist = categoryDiv0.getElementsByClass("wp-category-nav-list").first();
			if (categorynavlist == null) return list;

			Elements wpcalistItems = categorynavlist.getElementsByClass("wp-category-list-item");
			for (Element eec : wpcalistItems) {
				Element titleElement = eec.getElementsByClass("wp-category-list-item-title").first();
				String titleContent = titleElement.text();
				String title = titleContent.substring(0, titleContent.indexOf('('));
				Elements childcate = eec.getElementsByClass("wp-category-child");
				if (childcate.size() == 0) {/* 只有主类，没有子类 */
					GoodsCategoryClass e = new GoodsCategoryClass();
					e.categorytitle = title;
					e.categorycount = UtilsString.getNumbersInt(titleContent, "\\(", "\\)");
					e.categoryurl = titleElement.select("a[href]").first().attr("abs:href");
					e.splitUrl();
					list.add(e);
				} else {
					Elements childes = childcate.first().getElementsByClass("wp-category-child-item");
					for (Element onchild : childes) {
						String childtitle = onchild.getElementsByClass("wp-category-name").first().text();
						int childCount = UtilsString.getNumbersInt(onchild.getElementsByClass("wp-category-count").first().text(), "\\(", "\\)");//Integer.parseInt(onchild.getElementsByClass("wp-category-count").first().text());
						GoodsCategoryClass e = new GoodsCategoryClass();
						e.categorytitle = title;
						e.categorytitle2 = childtitle;
						e.categorycount = childCount;
						e.categoryurl = onchild.select("a[href]").first().attr("abs:href");
						e.splitUrl();
						list.add(e);
					}
				}

			}
			//Element urlelem = categorynavlist.select("a[href]").first();
			/*
			 * for (int i = 0; i < categorynavlist.size(); i++) {
			 * Element one = categorynavlist.get(i);
			 * String content = one.text();
			 * Element urlelem = one.select("a[href]").first();
			 * if (urlelem == null) return list;
			 * String baiduUrl = urlelem.attr("abs:href");
			 * logger.debug("url:" + baiduUrl);
			 * GoodsClass e = new GoodsClass();
			 * e.setTitleAll(content);
			 * e.url = baiduUrl;
			 * if (baiduUrl.indexOf('?') > -1) e.url = baiduUrl.substring(0, baiduUrl.indexOf('?'));
			 * list.add(e);
			 * }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static void main(String[] args) {
		String url = "https://yunqilvfeng.1688.com";
		List<GoodsCategoryClass> list = getShopGoodsCategoryList(url);
		for (int i = 0; i < list.size(); i++)
			System.out.println("[" + i + "]:" + list.get(i).toString());
	}

	public static class Goods {
		String title = "";
		String url = "";
		String pictureold="";
		String price = "";
		String attrib = "";
		String description = "";
		String priceexplain = "";
		String deliveryaddr = "";
		String starlevel="";
		String bargainnumber="";

		public static final Goods splitUrl(String url) {
			Goods result = new Goods();
			result.url = url;
			//Connection connect = Jsoup.connect(url).headers(UtilsConsts.header_a);
			try {
				//Document doc = connect.timeout(10000).maxBodySize(0).get();
				//String pageXml2=UtilsReadURL.getReadUrlJs(url);
				Document doc =UtilsReadURL.getReadUrlJsDocument(url);// Jsoup.parse(pageXml2,url);
				if (doc == null) return result;
				System.out.println("" + url);
				result.pictureold=getPicutre(doc);
				result.price = getPrice(doc);
				result.attrib = getGoodsAttrib(doc);
				result.description = getGoodsDescription(doc);
				result.priceexplain = getGoodspriceexplain(doc);
				result.deliveryaddr = getDeliveryaddr(doc);
				result.starlevel=getStarlevelStr(getStarlevel(doc));
				result.bargainnumber=getBargainnumber(doc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		/**
		 * 得到图片集
		 * @param doc Document
		 * @return String
		 */
		public static final String getPicutre(Document doc) {
			if (doc == null) return "";
			Element div = doc.getElementsByClass("tab-content-container").first();
			if (div == null) return "";
			Elements imagelist = div.select("li");
			StringBuilder sb=new StringBuilder();
			for (Element imageli : imagelist) {
				String urlpage = imageli.select("img[src]").attr("abs:src");
				if(urlpage.indexOf("lazyload.png")>-1)continue;
				urlpage = urlpage.replaceAll(".60x60", "");
				if(sb.length()>0)sb.append('|');
				sb.append(urlpage);	
			}
			return sb.toString();
		}

		/**
		 * 得到成交数
		 * @param doc Document
		 * @return String
		 */
		public static final String getBargainnumber(Document doc) {
			return UtilsJsoup.getElementsClassFirst(doc,"bargain-number");
		}
		/**
		 * 数值型转成星级
		 * @param level int
		 * @return String
		 */
		public static final String getStarlevelStr(int level) {
			switch (level) {
			case 1:
				return "半星";
			case 2:
				return "1星";
			case 3:
				return "1星半";
			case 4:
				return "2星";
			case 5:
				return "2星半";
			case 6:
				return "3星";
			case 7:
				return "3星半";
			case 8:
				return "4星";
			case 9:
				return "4星半";
			case 10:
				return "5星";
			default:
				return "0星";
			}
		}

		/**
		 * 得到评价 多个星 0-10
		 * @param doc Document
		 * @return int
		 */
		public static final int getStarlevel(Document doc) {
			if (doc == null) return 0;
			Element div = doc.getElementsByClass("star-level").first();
			if (div == null) return 0;
			for (int i = 0; i <= 10; i++) {
				int v = i * 5;
				Element level = div.getElementsByClass("sstar" + v).first();
				if (level != null) return i;
			}
			return 0;
		}

		/**
		 * 得到物流
		 * @param doc Document
		 * @return String
		 */
		public static final String getDeliveryaddr(Document doc) {
			return UtilsJsoup.getElementsClassFirst(doc,"delivery-addr");
		}

		/**
		 * 得到详细内容
		 * @param doc Document
		 * @return String
		 */
		public static final String getGoodsDescription(Document doc) {
			if (doc == null) return "";
			Elements divs = doc.getElementsByClass("desc-lazyload-container");
			if (divs.first() == null) return "";
			return divs.first().html();
		}

		/**
		 * 得到价格说明
		 * @param doc Document
		 * @return String
		 */
		public static final String getGoodspriceexplain(Document doc) {
			if (doc == null) return "";
			Element div = doc.getElementsByClass("price-explain").first();
			if (div == null) return "";
			return div.html();
		}

		/**
		 * 得到属性
		 * @param doc Document
		 * @return String
		 */
		public static final String getGoodsAttrib(Document doc) {
			if (doc == null) return "";
			Element div = doc.getElementsByClass("mod-detail-attributes").first();
			if (div == null) return "";
			Elements es = div.getElementsByClass("de-feature");
			Elements esvalue = div.getElementsByClass("de-value");
			StringBuilder sb = new StringBuilder();
			if (es.size() > 0) {
				sb.append('[');
				for (int i = 0; i < es.size(); i++) {
					String key = es.get(i).text();
					if (key.length() > 0 && i < esvalue.size()) {
						String value = esvalue.get(i).text();
						if (sb.length() > 1) sb.append(',');
						sb.append("{\"de-feature\":\"" + key + "\",");
						sb.append("\"de-value\":\"" + value + "\"}");
					}
				}
				sb.append(']');
				return sb.toString();
			}
			return "";
		}

		/**
		 * 得到价格
		 * @param doc Document
		 * @return String
		 */
		public static final String getPrice(Document doc) {
			try {
				Element pricediv = doc.getElementsByClass("mod-detail-price").first();
				if (pricediv == null) return "";
				Element tab = pricediv.select("table").first();
				if (tab == null) return "";
				Element price0 = tab.getElementsByClass("price").first();
				Element price1 = tab.getElementsByClass("price-extend").first();
				Element price2 = tab.getElementsByClass("original-price").first();
				Element price3 = tab.getElementsByClass("amount").first();
				List<RegularElement> rlist0 = UtilsJsoup.getKeywordClassRegular(price0, "ladder-\\d+-\\d+");
				List<RegularElement> rlist1 = UtilsJsoup.getKeywordClassRegular(price1, "ladder-\\d+-\\d+");
				List<RegularElement> rlist2 = UtilsJsoup.getKeywordClassRegular(price2, "ladder-\\d+-\\d+");
				List<RegularElement> rlist3 = UtilsJsoup.getKeywordClassRegular(price3, "ladder-\\d+-\\d+");

				StringBuilder sb = new StringBuilder();
				if (rlist0.size() > 0) {
					sb.append('[');
					for (int ii = 0; ii < rlist0.size(); ii++) {
						if (sb.length() > 1) sb.append(',');
						sb.append('{');
						String value;
						value = "";
						if (rlist0.get(ii) != null && rlist0.get(ii).getE() != null) value = rlist0.get(ii).getE().text();
						sb.append("\"price\":\"" + value + "\",");
						value = "";
						if (rlist1.size() > ii && rlist1.get(ii) != null && rlist1.get(ii).getE() != null) value = rlist1.get(ii).getE().text();
						sb.append("\"price-extend\":\"" + value + "\",");

						value = "";
						if (rlist2.size() > ii && rlist2.get(ii) != null && rlist2.get(ii).getE() != null) value = rlist2.get(ii).getE().text();
						sb.append("\"original-price\":\"" + value + "\",");

						value = "";
						if (rlist3.size() > ii && rlist3.get(ii) != null && rlist3.get(ii).getE() != null) value = rlist3.get(ii).getE().text();
						sb.append("\"amount\":\"" + value + "\"");

						sb.append('}');
					}
					sb.append(']');

				}
				return sb.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

	}

	public static class GoodsCategoryClass {
		String categorytitle = "";
		String categorytitle2 = "";
		int categorycount = 0;
		String categoryurl = "";
		List<Goods> list = new ArrayList<>();

		/**
		 * 输入 店长推荐(38)后进行分解
		 * @param title String
		 */
		public void setTitleAll(String title) {
			this.categorytitle = title.substring(0, title.indexOf('(')).trim();
			this.categorycount = UtilsString.getNumbersInt(title, "\\(", "\\)");
		}

		@Override
		public String toString() {
			return "GoodsClass [" + (categorytitle != null ? "title=" + categorytitle + ", " : "") + (categorytitle2 != null ? "title2=" + categorytitle2 + ", " : "") + "count=" + categorycount + ", "
					+ (categoryurl != null ? "url=" + categoryurl : "") + "]";
		}

		/**
		 * 按类分解url得到各主类或子类的列表，并提出产品列表
		 */
		public void splitUrl() {
			Connection connect = Jsoup.connect(categoryurl).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
			try {
				Document doc = connect.timeout(10000).maxBodySize(0).get();
				if (doc == null) return;
				Element prolistdiv = doc.getElementsByClass("wp-offerlist-windows").first();
				if (prolistdiv == null) return;
				list.addAll(getlist(categoryurl));

				Element pagediv = doc.getElementsByClass("app-paginator").first();
				if (pagediv == null) return;
				Element pageele = pagediv.getElementsByClass("page-count").first();
				if (pageele == null) return;
				String pagecount = pageele.text();
				if (pagecount == null || pagecount.length() == 0) return;
				//System.out.println("pagecount:"+pagecount);
				int pagesort = Integer.parseInt(pagecount);
				for (int ii = 2; ii <= pagesort; ii++) {
					list.addAll(getlist(categoryurl + "?pageNum=" + ii));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		static final List<Goods> getlist(String categoryurl) {
			List<Goods> list = new ArrayList<>();

			Connection connect = Jsoup.connect(categoryurl).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
			try {
				Document doc = connect.timeout(10000).maxBodySize(0).get();
				if (doc == null) return list;
				Element prolistdiv = doc.getElementsByClass("wp-offerlist-windows").first();
				if (prolistdiv == null) return list;
				Elements prolist = prolistdiv.getElementsByClass("offer-list-row-offer");
				for (Element e : prolist) {
					String protitle = e.getElementsByClass("title-new").first().text();
					//System.out.println("protitle:"+protitle);
					String prourl = e.getElementsByClass("title-link").first().select("a[href]").first().attr("abs:href");

					//System.out.println("prourl:"+prourl);
					Goods ne = new Goods();
					ne.title = protitle;
					ne.url = prourl;

					/*
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					 * 
					*/

					//ne.splitUrl();
					//System.out.println(ne.price);
					//System.out.println(ne.attrib);
					list.add(ne);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return list;
		}

	}
}
