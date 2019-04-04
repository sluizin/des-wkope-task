package des.wangku.operate.standard.subengineering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsString;

/**
 * 某单品关键字在1688、网库、慧聪中企业数量和供应信息数量
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class SingleproductAmount {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(SingleproductAmount.class);

	/**
	 * 得到数量 source[1688,wk,hc360]<br>
	 * dtype[0:企业,1:供应信息]<br>
	 * biztype[1:经销批发,生产加工,]
	 * @param keyword String
	 * @param source String
	 * @param dtype int
	 * @param biztype int
	 * @return String
	 */
	public static final String getAmount(String keyword, String source, int dtype,int biztype) {
		String value = null;
		try {
			if ("1688".equals(source)) {
				String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
				if (dtype == 0) {
					String urlString = "https://s.1688.com/company/company_search.htm?keywords=" + newKeyword + "&biztype="+biztype;
					value = UtilsReadURL.getReadUrlDisJsTextByClass(urlString, "sm-navigatebar-count", 0);					
				}else {
					String urlString = "https://s.1688.com/selloffer/offer_search.htm?keywords=" + newKeyword + "&biztype="+biztype;
					value = UtilsReadURL.getReadUrlDisJsTextByClass(urlString, "sm-widget-offer", 0);
					value = UtilsString.getNumbers(value, "共 \\d+ 件");
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	

		if (value == null || value.length() == 0) return "0";
		return value;
	}
	/**
	 * 得到数量 source[1688,wk,hc360]<br>
	 * dtype[0:企业,1:供应信息]
	 * @param keyword String
	 * @param source String
	 * @param dtype int
	 * @return String
	 */
	public static final String getAmount(String keyword, String source, int dtype) {
		String value = null;
		try {
			if ("1688".equals(source)) {
				String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
				if (dtype == 0) {
					String urlString = "https://s.1688.com/company/company_search.htm?keywords=" + newKeyword + "&earseDirect=false&button_click=top&n=y&pageSize=30&offset=3&beginPage=1";
					value = UtilsReadURL.getReadUrlDisJsTextByClass(urlString, "sm-navigatebar-count", 0);
				}
				if (dtype == 1) {
					String urlString = "https://s.1688.com/selloffer/offer_search.htm?keywords=" + newKeyword + "&button_click=top&earseDirect=false&n=y&netType=1%2C11";
					value = UtilsReadURL.getReadUrlDisJsTextByClass(urlString, "sm-widget-offer", 0);
					value = UtilsString.getNumbers(value, "共 \\d+ 件");
				}
			}
			if ("wk".equals(source)) {
				if (dtype == 0) {
					String urlString = "http://qiye.99114.com/listing/" + keyword;
					value = UtilsReadURL.getReadUrlDisJsTextByClass(urlString, "fr", 2);
					value = UtilsString.getNumbers(value, "到 \\d+ 条");
				}
				if (dtype == 1) {
					String urlString = "http://gongying.99114.com/listing/" + keyword;
					value = UtilsReadURL.getReadUrlDisJsTextByClass(urlString, "total", 0);
					value = UtilsString.getNumbers(value, "共\\d+件");
				}
			}
			if ("hc360".equals(source)) {
				String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
				if (dtype == 0) {
					String urlString = "https://s.hc360.com/?w=" + newKeyword + "&mc=enterprise";
					value = UtilsReadURL.getReadUrlDisJsTextByClass(urlString, "col", 0);
				}
				if (dtype == 1) {
					String urlString = "https://s.hc360.com/?w=" + newKeyword + "&mc=seller";
					value = UtilsReadURL.getReadUrlDisJsTextByClass(urlString, "col", 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (value == null || value.length() == 0) return "0";
		return value;
	}

	@Deprecated
	public static final String getAmount1688Cor(String keyword) {
		try {
			String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
			String urlString = "https://s.1688.com/company/company_search.htm?keywords=" + newKeyword + "&earseDirect=false&button_click=top&n=y&pageSize=30&offset=3&beginPage=1";
			String value = UtilsReadURL.getReadUrlJsTextByClassname(urlString, "sm-navigatebar-count", 0);
			if (value == null || value.length() == 0) return "0";
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Deprecated
	public static final String getAmount1688Pro(String keyword) {
		try {
			String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
			String urlString = "https://s.1688.com/selloffer/offer_search.htm?keywords=" + newKeyword + "&button_click=top&earseDirect=false&n=y&netType=1%2C11";
			String value = UtilsReadURL.getReadUrlJsTextByClassname(urlString, "sm-widget-offer", 0);
			value = UtilsString.getNumbers(value, "共 \\d+ 件");
			if (value == null || value.length() == 0) return "0";
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Deprecated
	public static final String getAmounthuicongCor(String keyword) {
		try {
			String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
			String urlString = "https://s.hc360.com/?w=" + newKeyword + "&mc=enterprise";
			String value = UtilsReadURL.getReadUrlJsTextByClassname(urlString, "col", 0);
			if (value == null || value.length() == 0) return "0";
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Deprecated
	public static final String getAmounthuicongPro(String keyword) {
		try {
			String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
			String urlString = "https://s.hc360.com/?w=" + newKeyword + "&mc=seller";
			String value = UtilsReadURL.getReadUrlJsTextByClassname(urlString, "col", 0);
			if (value == null || value.length() == 0) return "0";
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Deprecated
	public static final String getAmountwkCor(String keyword) {
		try {
			String urlString = "http://qiye.99114.com/listing/" + keyword;
			String value = UtilsReadURL.getReadUrlJsTextByClassname(urlString, "fr", 2);
			value = UtilsString.getNumbers(value, "到 \\d+ 条");
			if (value == null || value.length() == 0) return "0";
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Deprecated
	public static final String getAmounthuiwkPro(String keyword) {
		try {
			String urlString = "http://gongying.99114.com/listing/" + keyword;
			String value = UtilsReadURL.getReadUrlJsTextByClassname(urlString, "total", 0);
			value = UtilsString.getNumbers(value, "共\\d+件");
			if (value == null || value.length() == 0) return "0";
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		String keyword = "锦纶";
		System.out.println(keyword + "--1688公司:" + getAmount(keyword, "1688", 0));
		System.out.println(keyword + "--1688商品:" + getAmount(keyword, "1688", 1));
		System.out.println(keyword + "--huicong公司:" + getAmount(keyword, "hc360", 0));
		System.out.println(keyword + "--huicong商品:" + getAmount(keyword, "hc360", 1));
		System.out.println(keyword + "--网库公司:" + getAmount(keyword, "wk", 0));
		System.out.println(keyword + "--网库商品:" + getAmount(keyword, "wk", 1));
	}
}
