package des.wangku.operate.standard.utls;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量池
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsConsts {
	/** 项目平台 原始标题名称 换项目时更改标题名称，模块退出，则显示此字符串 */
	public static final String ACC_ProjectTitleDefault="SWT Application";

	public static final Map<String, String> header = new HashMap<>();

	public static final Map<String, String> header_a = new HashMap<>();

	static {
		//设置请求头
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
		header.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
		header.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		header.put("Accept-Encoding", "gzip, deflate");
		header.put("X-Requested-With", "XMLHttpRequest");
		header.put("Content-Type", "text/*, application/xml");
		header.put("Connection", "keep-alive");

		header_a.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.22 Safari/537.36 SE 2.X MetaSr 1.0");
		header_a.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header_a.put("Accept-Language", "zh-CN,zh;q=0.8");
		header_a.put("Accept-Encoding", "gzip, deflate, sdch");
		header_a.put("Content-Type", "application/octet-stream");
		header_a.put("Connection", "keep-alive");
		header_a.put("Upgrade-Insecure-Requests", "1");
	}
}
