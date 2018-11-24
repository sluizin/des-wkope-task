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

	public static final Map<String, String> header_b = new HashMap<>();
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
		

		header_b.put("Accept", "*/*");
		header_b.put("Accept-Encoding", "gzip, deflate");
		header_b.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		header_b.put("Connection", "keep-alive");
		header_b.put("Host", "www.seodo.cn");
		header_b.put("Content-Type", "application/json;charset=UTF-8");
		header_b.put("Referer", "http://www.seodo.cn/Quote");
		header_b.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
		header_b.put("X-Requested-With", "XMLHttpRequest");
		header_b.put("Upgrade-Insecure-Requests", "1");
		header_b.put("Cookie", "yunsuo_session_verify=383809ce78db30d2ad2ff0d3c4686578; Hm_lvt_294353148bfd9c1037daea2fa1c2c537=1542008107; __root_domain_v=.seodo.cn; _qddaz=QD.xz9ou4.cqswqk.joe0qg19; _qdda=3-1.1; _qddab=3-az79v8.joe2tf02; _qddamta_2852061168=3-0; __RequestVerificationToken=xtnqVeX50c5pHpZyf-nVFDWp93sNArwOh7VfA7fJrTg479Do5ZVpKDv8XmqUtWWzdDcm8QhTd6LJk9GjBgWMtKU6d-16hOYeY1TC3Aj5Yk1_a76m8ghy6x_gzwuw4vt-hNHTrRC3vpeOPEusUqG5Sg2; _qddac=3-2-1.1.az79v8.joe2tf02; Hm_lpvt_294353148bfd9c1037daea2fa1c2c537=1542015029");
	}
}
