package des.wangku.operate.standard.subengineering;

import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsRegular;

/**
 * 网库产品
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class WKProduct {
	/**
	 * 得到网库产品的产品图片地址
	 * @param url String
	 * @return String
	 */
	public static final String getWKProPicture(String url) {
		String pic = null;
		String content = UtilsReadURL.getUrlContent(url, "utf-8", 20000);
		if (content == null || content.length() == 0) return "";
		String regEx = "jqimg=\"([^\"]+)\"";
		pic = UtilsRegular.getRegExContent(content, regEx, 1);
		if (pic == null) return "";
		return pic;
	}
}
