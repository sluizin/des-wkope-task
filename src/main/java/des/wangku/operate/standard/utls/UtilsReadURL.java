package des.wangku.operate.standard.utls;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * 对URL进行读取操作
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsReadURL {
	/**
	 * 参数 - 会员登陆时需要使用的变量，如是否使用会员登陆、登陆界面的一些信息参数
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class LoginParameter {
		String formID = "";
		String inputNamePassword = "";
		String inputNameUsername = "";
		boolean isUser = false;
		String logcheckUrl = "";
		String logUrl = "";
		String password = "";
		String username = "";

		/**
		 * @param isUser boolean
		 * @param logUrl String
		 * @param logcheckUrl String
		 * @param formID String
		 * @param inputNameUsername String
		 * @param username String
		 * @param inputNamePassword String
		 * @param password String
		 */
		public LoginParameter(boolean isUser, String logUrl, String logcheckUrl, String formID, String inputNameUsername, String username, String inputNamePassword, String password) {
			this.isUser = isUser;
			this.logUrl = logUrl;
			this.logcheckUrl = logcheckUrl;
			this.formID = formID;
			this.inputNameUsername = inputNameUsername;
			this.username = username;
			this.inputNamePassword = inputNamePassword;
			this.password = password;
		}
	}

	static final BrowserVersion BrowserVer = BrowserVersion.BEST_SUPPORTED;

	static Map<String, String> datas2 = new HashMap<>();

	static Logger logger = LoggerFactory.getLogger(UtilsReadURL.class);

	static final NicelyResynchronizingAjaxController nicelyAjax = new NicelyResynchronizingAjaxController();

	static final WebClient webClient = new WebClient(BrowserVer);

	static {
		datas2.put("from", "en");
		datas2.put("to", "zh");
		datas2.put("query", "jeep");
		datas2.put("simple_means_flag", "3");
		datas2.put("sign", "14348.318269");
		datas2.put("token", "4edbf8229215f26c6b401aaf693466a3");
	}

	static {
		webClient.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true  
		webClient.getOptions().setCssEnabled(false); //禁用css支持  
		webClient.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常  
		webClient.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待  
		webClient.waitForBackgroundJavaScript(8000);
		webClient.getOptions().setAppletEnabled(true);
		webClient.setAjaxController(nicelyAjax);
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.getOptions().setPopupBlockerEnabled(true);
		webClient.getOptions().setRedirectEnabled(true);
	}

	/**
	 * 下载文件到当地
	 * @param urlstr String
	 * @param savePath String
	 * @return File
	 */
	public static final File downfile(String urlstr, String savePath) {
		if (urlstr == null || savePath == null) return null;
		try {
			URL url = new URL(urlstr);
			String filename = UtilsReadURL.getFileName(url);
			return downfile(url, filename, savePath);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 下载文件到当地
	 * @param urlstr String
	 * @param fileName String
	 * @param savePath String
	 * @return File
	 */
	public static final File downfile(String urlstr, String fileName, String savePath) {
		try {
			URL url = new URL(urlstr);
			return downfile(url, fileName, savePath);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 下载文件到当地
	 * @param urlstr String
	 * @param fileName String
	 * @param savePath String
	 * @return File
	 */
	public static final synchronized File downfile(URL url, String fileName, String savePath) {
		try {
			URLConnection con = url.openConnection();
			if (fileName == null || fileName.length() == 0) fileName = getFileName(con);
			if (fileName == null) return null;
			HttpURLConnection conn = (HttpURLConnection) con;
			conn.setConnectTimeout(50 * 1000);
			conn.setReadTimeout(30*1000);
			conn = UtilsConstsRequestHeader.getRndRequestProperty(conn);//得到输入流
			InputStream inputStream = conn.getInputStream();//获取自己数组
			byte[] getData = UtilsFile.readInputStream(inputStream);//文件保存位置
			File saveDir = new File(savePath);
			if (!saveDir.exists()) saveDir.mkdir();
			File file = new File(saveDir + File.separator + fileName);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(getData);
			if (fos != null) fos.close();
			if (inputStream != null) inputStream.close();
			return file;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 通过URLConnection得到文件名
	 * @param url URL
	 * @return String
	 */
	public static String getFileName(URL url) {
		if (url == null) return null;
		String filename = null;
		try {
			filename = getFileName(url.openConnection());
			if (filename != null && filename.length() > 0) return filename;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过URLConnection得到文件名
	 * @param conn URLConnection
	 * @return String
	 */
	public static String getFileName(URLConnection conn) {
		if (conn == null) return null;
		Map<String, List<String>> hf = conn.getHeaderFields();
		if (hf == null) return null;
		Set<String> key = hf.keySet();
		if (key == null) return null;
		try {
			for (String skey : key) {
				List<String> values = hf.get(skey);
				for (String value : values) {
					String result = new String(value.getBytes("ISO-8859-1"), "GBK");
					int location = result.indexOf("filename");
					if (location >= 0) {
						result = result.substring(location + "filename".length());
						return result.substring(result.indexOf("=") + 1);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		URL url = conn.getURL();
		String urlString = url.toString();
		return urlString.substring(urlString.lastIndexOf("/") + 1);
	}

	public static void getJString(String keyword) {
		try {
			String href = "https://fanyi.baidu.com/v2transapi";
			Connection con = Jsoup.connect(href);
			Map<String, String> header_c = new HashMap<>();
			//header_c.put("Accept", "*/*");
			//header_c.put("Accept-Encoding", "gzip, deflate");
			//header_c.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
			//header_c.put("Content-Type", "text/*,application/x-www-form-urlencoded; charset=UTF-8");
			//header_c.put("Content-Length", "117");
			//header_c.put("Origin", "https://fanyi.baidu.com");
			//header_c.put("Host", "fanyi.baidu.com");
			//header_c.put("Origin", "https://fanyi.baidu.com");
			//header_c.put("Referer", "https://fanyi.baidu.com/");
			//header_c.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3610.2 Safari/537.36");
			//header_c.put("X-Requested-With", "XMLHttpRequest");
			//header_c.put("Upgrade-Insecure-Requests", "1");
			header_c.put("Cookie",
					"BAIDUID=6DC294E63D1D79443EE861C806885497:FG=1; PSTM=1542011390; BIDUPSID=EF309A51D38CDB56B8B7B8F0A9BF85E2; REALTIME_TRANS_SWITCH=1; FANYI_WORD_SWITCH=1; HISTORY_SWITCH=1; SOUND_SPD_SWITCH=1; SOUND_PREFER_SWITCH=1; BDUSS=kdmc2gtVENnZDJkc3Btdko2ZkNveFRsV3Q4MXljMTBnZWttR2xwWkU4YmRXREZjQVFBQUFBJCQAAAAAAAAAAAEAAABtjdJKX7H5t-LHp8TqvP1fAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN3LCVzdywlcY; H_PS_PSSID=1457_21095_28019_22160; Hm_lvt_64ecd82404c51e03dc91cb9e8c025574=1543971572,1544057384,1544145622,1544403922; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; from_lang_often=%5B%7B%22value%22%3A%22zh%22%2C%22text%22%3A%22%u4E2D%u6587%22%7D%2C%7B%22value%22%3A%22en%22%2C%22text%22%3A%22%u82F1%u8BED%22%7D%5D; to_lang_often=%5B%7B%22value%22%3A%22en%22%2C%22text%22%3A%22%u82F1%u8BED%22%7D%2C%7B%22value%22%3A%22zh%22%2C%22text%22%3A%22%u4E2D%u6587%22%7D%5D; delPer=0; PSINO=1; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; ZD_ENTRY=empty; locale=zh; Hm_lpvt_64ecd82404c51e03dc91cb9e8c025574=1544428883");

			con.headers(header_c);
			Response login = con.ignoreContentType(true).followRedirects(true).data(datas2).method(Method.GET).execute();
			System.out.println("login：" + login.body());
			//Document login = con.ignoreContentType(true).followRedirects(true).data(datas2).post();
			//System.out.println("login：" + login.body());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到url的更新时间，直接查head，如果错误则返回null
	 * @param url URL
	 * @return Date
	 */
	public static final Date getLastModify(URL url) {
		try {
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setConnectTimeout(30000);
			http.setReadTimeout(30000);
			http.setRequestMethod("HEAD");
			Date lastModify = new Date(http.getLastModified());
			return lastModify;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 会员模拟登陆，并得到cookies
	 * @param para LoginParameter
	 * @return Map &lt; String, String &gt;
	 */
	public static Map<String, String> getLoginCookies(LoginParameter para) {
		Map<String, String> map = new HashMap<>();
		if (para == null || (!para.isUser)) return map;
		try {
			/*
			 * 第一次请求
			 * grab login form page first
			 * 获取登陆提交的表单信息，及修改其提交data数据（login，password）
			 */
			Connection con = Jsoup.connect(para.logUrl);  // 获取connection
			con.headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);// 配置模拟浏览器
			Response rs = con.execute();                // 获取响应
			Document d1 = Jsoup.parse(rs.body());       // 转换为Dom树
			/*
			 * 获取cooking和表单属性
			 * lets make data map containing all the parameters and its values found in the form
			 */
			Map<String, String> datas = new HashMap<>();
			List<Element> eleLists = d1.getElementById(para.formID).select("input");
			for (Element e : eleLists) {
				if (e.attr("name").equals(para.inputNameUsername)) e.attr("value", para.username);
				if (e.attr("name").equals(para.inputNamePassword)) e.attr("value", para.password);
				if (e.attr("name").length() > 0) datas.put(e.attr("name"), e.attr("value"));
			}
			/*
			 * 第二次请求，以post方式提交表单数据以及cookie信息
			 */
			Connection con2 = Jsoup.connect(para.logcheckUrl);
			con2.headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
			con2.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
			// 设置cookie和post上面的map数据
			Response login = con2.ignoreContentType(true).followRedirects(true).method(Method.POST).data(datas).cookies(rs.cookies()).execute();
			/*
			 * 打印，登陆成功后的信息
			 * parse the document from response
			 * System.out.println(login.body());
			 * 登陆成功后的cookie信息，可以保存到本地，以后登陆时，只需一次登陆即可
			 */
			map = login.cookies();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 得到URL真实的url，跟跳转有关的新地址
	 * @param url URL
	 * @return URL
	 */
	public static final URL getReadURL(URL url) {
		if (url == null) return null;
		try {
			String protocol = url.getProtocol().toLowerCase();
			if (!"http".equals(protocol)) return url;
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setInstanceFollowRedirects(false);
			urlcon.setConnectTimeout(3000);
			urlcon.setReadTimeout(3000);
			String relocation = urlcon.getHeaderField("Location");
			if (relocation == null) return url;
			if (relocation.indexOf("http://") > -1 || relocation.indexOf("https://") > -1) return new URL(relocation);
			return url;
		} catch (IOException e) {
			e.printStackTrace();
			return url;
		}
	}

	/**
	 * 调用url，并不进行js运行，得到html
	 * @param url String
	 * @return String
	 */
	public static final String getReadUrlDisJs(String url) {
		/** HtmlUnit请求web页面 */
		WebClient wc = new WebClient(BrowserVer);
		wc.getOptions().setJavaScriptEnabled(false); //启用JS解释器，默认为true  
		wc.getOptions().setCssEnabled(false); //禁用css支持  
		wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常  
		wc.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待  
		wc.waitForBackgroundJavaScript(10000);
		wc.getOptions().setRedirectEnabled(true);
		try {
			HtmlPage page = wc.getPage(url);
			String pageXml = page.asXml(); //以xml的形式获取响应文本 
			wc.close();
			return pageXml;
		} catch (Exception e) {
			if (wc != null) {
				wc.close();
				wc = null;
			}
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 调用url，并不进行js运行，得到Document
	 * @param url String
	 * @return Document
	 */
	public static final Document getReadUrlDisJsDoc(String url) {
		String pageXml2 = UtilsReadURL.getReadUrlDisJs(url);
		String baseuri = UtilsReadURL.getUrlDomain(url);
		return Jsoup.parse(pageXml2, baseuri);
	}

	/**
	 * 从网址中得到classname的text字符串 为null时返回""
	 * @param url String
	 * @param classname String
	 * @param index int
	 * @return String
	 */
	public static final String getReadUrlDisJsTextByClass(String url, String classname, int index) {
		//logger.debug("url:" + url);
		Document doc = getReadUrlDisJsDoc(url);
		if (doc == null) return "";
		//System.out.println("doc："+doc.html());
		Elements ess = doc.getElementsByClass(classname);
		if (ess == null || index >= ess.size()) return "";
		Element e = ess.get(index);
		if (e == null) return "";
		return e.text();
	}

	/**
	 * 调用url，并进行js运行，得到html
	 * @param url String
	 * @return String
	 */
	public static final String getReadUrlJs(String url) {
		return getReadUrlJs(url, true, false, 20000, 10000);
	}

	/**
	 * 调用url，并进行js运行，得到html
	 * @param url String
	 * @param jsEnable boolean
	 * @param cssEnable boolean
	 * @param timeout int
	 * @param jsTime long
	 * @return String
	 */
	public static final String getReadUrlJs(String url, boolean jsEnable, boolean cssEnable, int timeout, long jsTime) {
		WebClient wc = new WebClient(BrowserVer);/* HtmlUnit请求web页面 */
		wc.getOptions().setJavaScriptEnabled(jsEnable); //启用JS解释器，默认为true  
		wc.getOptions().setCssEnabled(cssEnable); //禁用css支持  
		wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常  
		wc.getOptions().setTimeout(timeout); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待  
		wc.waitForBackgroundJavaScript(jsTime);
		wc.getOptions().setRedirectEnabled(true);
		try {
			HtmlPage page = wc.getPage(url);
			String pageXml = page.asXml(); //以xml的形式获取响应文本 
			wc.close();
			return pageXml;
		} catch (Exception e) {
			if (wc != null) wc.close();
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 调用url，并进行js运行，得到html 后期需要运行js并且验证时再修改此方法
	 * @param urlString String
	 * @param cookies Set &lt; Cookie &gt;
	 * @param code String
	 * @param refer String
	 * @return String
	 */
	@Deprecated
	public static final String getReadUrlJs(String urlString, Set<Cookie> cookies, String code, String refer) {
		try {
			WebRequest request = new WebRequest(new URL(urlString));
			request.setCharset(Charset.forName(code));
			//request.setProxyHost("120.120.120.x");
			//request.setProxyPort(80);
			request.setAdditionalHeader("Referer", refer);//设置请求报文头里的refer字段  
			////设置请求报文头里的User-Agent字段  
			request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
			/** HtmlUnit请求web页面 */
			WebClient wc = new WebClient(BrowserVer);
			wc.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true  
			wc.getOptions().setCssEnabled(false); //禁用css支持  
			wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常  
			wc.getOptions().setTimeout(20000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待 
			wc.getCookieManager().setCookiesEnabled(true);
			wc.waitForBackgroundJavaScript(10000);
			wc.getOptions().setRedirectEnabled(true);
			Iterator<Cookie> iCookies = cookies.iterator();
			while (iCookies.hasNext())
				wc.getCookieManager().addCookie(iCookies.next());
			try {
				HtmlPage page = wc.getPage(request);
				String pageXml = page.asXml(); //以xml的形式获取响应文本 
				wc.close();
				return pageXml;
			} catch (Exception e) {
				if (wc != null) wc.close();
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 调用url，并进行js运行，得到html
	 * @param url String
	 * @return String
	 */
	public static final String getReadUrlJsDefault(String url) {
		try {
			HtmlPage page = webClient.getPage(url);
			String pageXml = page.asXml(); //以xml的形式获取响应文本 
			//wc.close();
			return pageXml;
		} catch (Exception e) {
			//if (wc != null) wc.close();
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 调用url，并进行js运行，得到Document
	 * @param url String
	 * @return Document
	 */
	public static final Document getReadUrlJsDocument(String url) {
		String pageXml2 = UtilsReadURL.getReadUrlJs(url, true, true, 20000, 20000);
		String baseuri = UtilsReadURL.getUrlDomain(url);
		return Jsoup.parse(pageXml2, baseuri);
	}

	/**
	 * 通过url得到classname的数组
	 * @param url String
	 * @param classname String
	 * @return Elements
	 */
	public static final Elements getReadUrlJsTextByClassname(String url, String classname) {
		Document doc = getReadUrlJsDocument(url);
		if (doc == null) return null;
		return doc.getElementsByClass(classname);
	}

	/**
	 * 从网址中得到classname的text字符串 为null时返回""
	 * @param url String
	 * @param classname String
	 * @param index int
	 * @return String
	 */
	public static final String getReadUrlJsTextByClassname(String url, String classname, int index) {
		//logger.debug("url:" + url);
		Document doc = getReadUrlJsDocument(url);
		if (doc == null) return "";
		//System.out.println("doc："+doc.html());
		Elements ess = doc.getElementsByClass(classname);
		if (ess == null || index >= ess.size()) return "";
		Element e = ess.get(index);
		if (e == null) return "";
		return e.text();
	}

	/**
	 * 读取url，以HTTP/1.1格式进行读取，截止到&lt;/html&gt;
	 * @param host String
	 * @param url String
	 * @param port int
	 * @param timeout int
	 * @param code String
	 * @return String
	 */
	public static final String getRealContent(String host, String url, int port, int timeout, String code) {
		StringBuilder sb = new StringBuilder();
		try (Socket socket = new Socket(host, port);) {
			socket.setSoTimeout(timeout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("GET /" + url + " HTTP/1.1\r\n");
			bw.write("Host:" + host + "\r\n");
			bw.write("\r\n");
			bw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), code));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				if (line.contains("</html>")) break;
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 默认：80端口 30000反应时间
	 * @param host
	 * @param url
	 * @param code
	 * @return
	 */
	public static final String getRealContent(String host, String url, String code) {
		return getRealContent(host, url, 80, 30000, code);
	}

	/**
	 * 80端口 socket
	 * @param domain String 域名(除去协议)
	 * @param url String 以/开始的右侧url
	 * @param code String
	 * @return Document
	 */
	public static final Document getRealContentDocument(String domain, String url, String code) {
		String content = UtilsReadURL.getRealContent(domain, url, code);
		Document doc = Jsoup.parse(content,domain);
		return doc;
	}

	/**
	 * 得到实际地址
	 * @param url String
	 * @return String
	 */
	public static String getRealLocation(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpClientContext context = HttpClientContext.create();
		HttpGet get = new HttpGet(url);
		get.addHeader("Accept", "text/html");
		get.addHeader("Accept-Charset", "utf-8");
		get.addHeader("Accept-Encoding", "gzip");
		get.addHeader("Accept-Language", "en-US,en");
		get.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22");
		try {
			@SuppressWarnings("unused")
			HttpResponse response = httpclient.execute(get, context);
			List<URI> redirectLocations = context.getRedirectLocations();
			if (redirectLocations == null || redirectLocations.size() == 0) return "";
			for (URI ii : redirectLocations) {
				return ii.toURL().toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 得到实际地址<br>
	 * 域名中含有关键字，并排队数组中的含有的关键，如过滤数组中含有关键，则返回
	 * @param urlString String
	 * @param key String
	 * @param filterArr String[]
	 * @return String
	 */
	public static final String getRealMultiLocation(final String urlString, String key, String... filterArr) {
		if (urlString.indexOf(key) > -1 && (!UtilsArrays.isfilterArr(urlString, filterArr))) return urlString;
		String baiduUlr = UtilsReadURL.getRealLocation(urlString);
		if (baiduUlr != null && baiduUlr.indexOf(key) > -1 && (!UtilsArrays.isfilterArr(baiduUlr, filterArr))) return baiduUlr;
		try {
			URL url = new URL(urlString);
			URL newurl = UtilsReadURL.getReadURL(url);
			if (newurl == null) return "";
			String urlStr = newurl.toString();
			if (urlStr.indexOf(key) > -1 && (!UtilsArrays.isfilterArr(urlStr, filterArr))) return urlStr;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @param url URL
	 * @param code String
	 * @return String
	 */
	@Deprecated
	public static String getSocketContent(URL url, String code) {
		if (url == null) return "";
		StringBuilder sb = new StringBuilder();
		int port = url.getPort();
		if (port == -1) port = 80;
		String host = url.getHost();
		if (host == null) return "";
		try (Socket socket = new Socket(host, port);) {
			socket.setSoTimeout(30000);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("GET /" + url.getPath() + " HTTP/1.1\r\n");
			bw.write("Host:" + host + "\r\n");
			bw.write("\r\n");
			bw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), code));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				if (line.contains("</html>")) break;
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 只支持http与https协议
	 * @param url URL
	 * @param code String
	 * @param timeout int
	 * @return String
	 */
	public static String getSocketContent(URL url, String code, int timeout) {
		if (url == null) return "";
		StringBuilder sb = new StringBuilder();
		String http = url.getProtocol().toLowerCase();
		/* 只支持http与https协议 */
		if (!UtilsString.isExist(http, "http", "https")) return "";
		int port = url.getPort();
		if ("http".equals(http)) {
			if (port == -1) port = 80;
		} else {
			if (port == -1) port = 443;
		}
		String host = url.getHost();
		if (host == null) return "";
		Socket socket = null;
		try {
			if ("http".equals(http)) {
				socket = new Socket(InetAddress.getByName(host), port);
			} else {
				socket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(InetAddress.getByName(host), port);
			}
			//socket.setKeepAlive(true);
			socket.setSoTimeout(timeout);
			//socket.setSendBufferSize(200);
			if (!socket.isConnected()) {
				logger.debug("Socket连接失败:" + url.toString());
				return null;
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("GET /" + url.getPath() + " HTTP/1.1\r\n");
			bw.write(UtilsConstsRequestHeader.getRndHeadMapString(host));
			/*
			 * bw.write("Host:" + host + "\r\n");
			 * bw.write("Content-Type: text/html\r\n");
			 * bw.write("User-Agent:Mozila/4.0(compatible;MSIE5.01;Window NT5.0)\r\n");
			 * bw.write("Accept:image/gif.image/jpeg.* /* \r\n");
			 * bw.write("Accept-Language:zh-cn\r\n");
			 */
			bw.write("\r\n");
			bw.flush();

			if (UtilsRnd.getRndBoolean()) try (InputStream is = socket.getInputStream(); BufferedInputStream bis = new BufferedInputStream(is);) {
				byte[] buffer = new byte[1024];
				int count = 0;
				while (true) {
					count = bis.read(buffer);
					if (count == -1) break;
					String line = new String(buffer, 0, count, code);
					sb.append(line);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			else try (InputStream is = socket.getInputStream(); InputStreamReader isr = new InputStreamReader(is); BufferedReader br = new BufferedReader(isr);) {
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (bw != null) bw.close();
			if (socket != null && !socket.isClosed()) socket.close();

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	/**
	 * 得到url状态是200还是404
	 * @param url URL
	 * @param code String
	 * @return int
	 */
	public static int getSocketContentState(URL url, String code) {
		int port = url.getPort();
		if (port == -1) port = 80;
		String host = url.getHost();
		try (Socket socket = new Socket(host, port);) {
			socket.setSoTimeout(15000);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("GET /" + url.getPath() + " HTTP/1.1\r\n");
			bw.write("Host:" + host + "\r\n");
			bw.write("\r\n");
			bw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), code));
			br.close();
			bw.close();
		} catch (Exception e) {
			return 404;
		}
		return 200;
	}

	/**
	 * 通过URL得到文件内容
	 * @param url String
	 * @param code String
	 * @param timeout int
	 * @return String
	 */
	public static final String getUrlContent(final String url, final String code, int timeout) {
		if (url == null || url.length() == 0) return "";
		try {
			URL urla = new URL(url);
			return getUrlContent(urla, code, timeout);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 通过html中的form提交相应表单内容，并返回html结果代码
	 * @param url String
	 * @param postUrl String
	 * @param formID String
	 * @param method Method
	 * @param inputMap Map &lt; String,String &gt;
	 * @return String
	 */
	public static final String getUrlContent(String url, String postUrl, String formID, Method method, Map<String, String> inputMap) {
		try {
			/*
			 * 第一次请求
			 * grab login form page first
			 * 获取登陆提交的表单信息，及修改其提交data数据（login，password）
			 */
			Connection con = Jsoup.connect(url);  // 获取connection
			con.headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);// 配置模拟浏览器
			Response rs = con.execute();                // 获取响应
			Document d1 = Jsoup.parse(rs.body());       // 转换为Dom树
			/*
			 * 获取cooking和表单属性
			 * lets make data map containing all the parameters and its values found in the form
			 */
			Map<String, String> datas = new HashMap<>();
			List<Element> eleLists = d1.getElementById(formID).select("input");
			for (Element e : eleLists) {
				String name = e.attr("name");
				if (name == null || name.length() == 0) continue;
				String value = e.attr("value");
				if (inputMap.containsKey(name)) value = inputMap.get(name);
				datas.put(name, value);
			}
			/* 第二次请求，以post方式提交表单数据以及cookie信息 */
			Connection con2 = Jsoup.connect(postUrl);
			con2.headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
			/* 设置cookie和post上面的map数据 */
			Response login = con2.ignoreContentType(true).followRedirects(true).method(method).data(datas).cookies(rs.cookies()).execute();
			if (login != null) return login.body();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 通过URL得到文件内容
	 * @param url URL
	 * @param code String
	 * @param timeout int
	 * @return String
	 */
	public static final String getUrlContent(final URL url, final String code, int timeout) {
		if (url == null) return "";
		StringBuilder sb = new StringBuilder(20);
		try {
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(timeout);
			urlcon.setReadTimeout(timeout);
			urlcon.connect();
			String returnCode = new Integer(urlcon.getResponseCode()).toString();
			if (!returnCode.startsWith("2")) return null;
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, code);
			BufferedReader buffer = new BufferedReader(isr);
			String l = null;
			while ((l = buffer.readLine()) != null) {
				if (l.length() == 0) continue;
				sb.append(l);
				sb.append('\n');
			}
			buffer.close();
			is.close();
		} catch (IOException e) {
		}
		return sb.toString();
	}

	/**
	 * 从地址中提取 http://www.sohu.com
	 * @param url String
	 * @return String
	 */
	public static final String getUrlDomain(String url) {
		if (url == null || url.trim().length() == 0) return null;
		try {
			String newUrl = url.trim().replaceAll("\\\\", "/");
			return getUrlDomain(new URL(newUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到 http://www.99114.com
	 * @param url URL
	 * @return String
	 */
	public static final String getUrlDomain(URL url) {
		if (url == null) return null;
		return url.getProtocol() + "://" + url.getHost();
	}

	/**
	 * 得到基本网址 http://www.99114.com/
	 * @param e Element e
	 * @return String
	 */
	public static final String getUrlDomain(Element e) {
		if (e == null) return null;
		return e.ownerDocument().baseUri();
	}

	/**
	 * 得到字符串中的域名Host
	 * @param host String
	 * @return String
	 */
	public static String getUrlHost(String host) {
		if (host.indexOf("://") > 0) {
			try {
				URL url = new URL(host);
				return getUrlHost(url);
			} catch (MalformedURLException e) {
				return host;
			}
		} else {
			int index = host.indexOf('/');
			if (index > 0) return host.substring(0, index);
		}
		return host;
	}

	/**
	 * 得到字符串中的域名Host
	 * @param url URL
	 * @return String
	 */
	public static String getUrlHost(URL url) {
		if (url == null) return null;
		return url.getHost();// 获取主机名 
	}

	/**
	 * 得到URL的特殊关键主目录，如http://www.xxx.com/abc/DEF/123/ 得到 www.xxxx.com/abc/
	 * @param url URL
	 * @return String
	 */
	public static final String getURLMasterKey(URL url) {
		if (url == null) return "";
		String str = url.toString();
		str = str.substring(str.indexOf("//") + 2, str.length());
		int index = UtilsString.getStringPosition(str, "/", 1);
		if (index == -1) return str;
		str = str.substring(0, index + 1);
		return str;
	}

	/**
	 * 返回url的返回状态值
	 * @param urlStr String
	 * @param timeout int
	 * @return int
	 */
	public static final int getUrlResponseCode(final String urlStr, int timeout) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(timeout);
			urlcon.setReadTimeout(timeout);
			urlcon.connect();
			return urlcon.getResponseCode();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 返回url的返回ContentLength
	 * @param urlStr String
	 * @param timeout int
	 * @return int
	 */
	public static final int getUrlResponseContentLength(final String urlStr, int timeout) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(timeout);
			urlcon.setReadTimeout(timeout);
			urlcon.connect();
			return urlcon.getContentLength();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 判断链接是否是死链
	 * @param urlString String
	 * @return boolean
	 */
	public static boolean isConnection(String urlString) {
		if (urlString.indexOf("http://") != 0 && urlString.indexOf("https://") != 0) return false;
		try {
			URL url = new URL(urlString);
			return isConnection(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断链接是否是死链
	 * @param url URL
	 * @return boolean
	 */
	public static boolean isConnection(URL url) {
		return isConnection(url, 10000);
	}

	/**
	 * 判断链接是否是死链
	 * @param url URL
	 * @param timeout int
	 * @return boolean
	 */
	public static boolean isConnection(URL url, int timeout) {
		try {
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setConnectTimeout(timeout);
			http.setReadTimeout(timeout);
			int result = http.getResponseCode();
			return result >= 200 && result < 300;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 从url中判断是否含有关键字数组，在html之间<br>
	 * 只支持http与https协议
	 * @param href String
	 * @param code String
	 * @param timeout int
	 * @param arrs String[]
	 * @return boolean
	 */
	public static boolean isSocketContainKeywords(String href, String code, int timeout, String... arrs) {
		if (href == null || href.length() == 0) return false;
		try {
			URL url = new URL(href);
			return isSocketContainKeywords(url, code, timeout, arrs);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 从url中判断是否含有关键字数组，在html之间<br>
	 * 只支持http与https协议
	 * @param url URL
	 * @param code String
	 * @param timeout int
	 * @param arrs String[]
	 * @return boolean
	 */
	public static boolean isSocketContainKeywords(URL url, String code, int timeout, String... arrs) {
		if (url == null) return false;
		String http = url.getProtocol().toLowerCase();
		/* 只支持http与https协议 */
		if (!UtilsString.isExist(http, "http", "https")) return false;
		//int port = url.getPort();
		String host = url.getHost();
		if (host == null) return false;
		try {
			Socket socket = null;
			if ("http".equals(http)) {
				socket = new Socket(InetAddress.getByName(host), 80);
			} else {
				socket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(InetAddress.getByName(host), 443);
			}
			socket.setSoTimeout(timeout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("GET /" + url.getPath() + " HTTP/1.1\r\n");
			bw.write("Host:" + host + "\r\n");
			bw.write("Content-Type: text/html\r\n");
			bw.write("User-Agent:Mozila/4.0(compatible;MSIE5.01;Window NT5.0)\r\n");
			bw.write("Accept:image/gif.image/jpeg.*/*\r\n");
			bw.write("Accept-Language:zh-cn\r\n");
			bw.write("\r\n");
			bw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), code));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (UtilsString.isContain(line, arrs)) return true;
				if (line.contains("</html>")) break;
			}
			br.close();
			bw.close();
			if (socket != null && !socket.isClosed()) socket.close();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		String href = "http://www.gazww.com/166/zhangjie124040.shtml";
		try {
			URL url = new URL(href);
			System.out.println("result:" + getSocketContent(url, "utf-8", 5000));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public static void main2(String[] args) {
		String href = "https://fanyi.baidu.com/#en/zh/success";
		try {
			URL url = new URL(href);
			System.out.println("result:" + getSocketContentState(url, "utf-8"));

			/*
			 * 第一次请求
			 * grab login form page first
			 * 获取登陆提交的表单信息，及修改其提交data数据（login，password）
			 */
			Connection con = Jsoup.connect(href);  // 获取connection
			con.headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);// 配置模拟浏览器
			//Response rs = con.execute();                // 获取响应
			//Document d1 = Jsoup.parse(rs.body());       // 转换为Dom树
			/*
			 * 获取cooking和表单属性
			 * lets make data map containing all the parameters and its values found in the form
			 */
			Map<String, String> datas = new HashMap<>();
			Response login = con.ignoreContentType(true).followRedirects(true).method(Method.GET).execute();
			/*
			 * 打印，登陆成功后的信息
			 * parse the document from response
			 * System.out.println(login.body());
			 * 登陆成功后的cookie信息，可以保存到本地，以后登陆时，只需一次登陆即可
			 */
			//System.out.println(login.body());

			String content2 = getUrlContent(new URL("https://fanyi.baidu.com/#en/zh/success"), "utf-8", 20000);

			System.out.println(content2);

			String content = login.body();
			//System.out.println(content);
			String lstr = "gtk = '";
			int p = content.indexOf(lstr);
			if (p > -1) {
				int p2 = content.indexOf("'", p + lstr.length());
				String keyy = content.substring(p + lstr.length(), p2);
				System.out.println(keyy);
			}
			datas = login.cookies();
			for (String key : datas.keySet()) {//keySet获取map集合key的集合  然后在遍历key即可
				String value = datas.get(key).toString();//
				System.out.println("key:" + key + "\tvalue:" + value);
			}

			HtmlPage page = webClient.getPage(href);
			Set<Cookie> set = webClient.getCookies(new URL(href));
			for (Cookie e : set) {
				System.out.println(e.getDomain() + "--" + e.getName() + "--" + e.getValue());
			}
			String pageXml = page.asXml(); //以xml的形式获取响应文本 

			String newurl = "https://fanyi.baidu.com/v2transapi";
			Set<Cookie> setold = new HashSet<>();
			setold.add(new Cookie(".fanyi.baidu.com", "from", "en"));
			setold.add(new Cookie(".fanyi.baidu.com", "to", "zh"));
			setold.add(new Cookie(".fanyi.baidu.com", "query", "soon"));
			setold.add(new Cookie(".fanyi.baidu.com", "transtype", "enter"));
			setold.add(new Cookie(".fanyi.baidu.com", "simple_means_flag", "3"));
			setold.add(new Cookie(".fanyi.baidu.com", "sign", "130710.335271"));
			setold.add(new Cookie(".fanyi.baidu.com", "token", "4edbf8229215f26c6b401aaf693466a3"));

			Map<String, String> datas2 = new HashMap<>();
			datas2.put("from", "en");
			datas2.put("to", "zh");
			datas2.put("query", "success");
			datas2.put("transtype", "enter");
			datas2.put("simple_means_flag", "3");
			datas2.put("sign", "14348.318269");
			datas2.put("token", "4edbf8229215f26c6b401aaf693466a3");

			//String content=getReadUrlJs(newurl, setold, "utf-8", "https://fanyi.baidu.com/");

			Connection con3 = Jsoup.connect(href);  // 获取connection
			con3.headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);// 配置模拟浏览器
			Response rs = con3.execute();                // 获取响应
			Document d1 = Jsoup.parse(rs.body());       // 转换为Dom树

			Connection con2 = Jsoup.connect(newurl);
			con2.headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
			con2.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");

			// 设置cookie和post上面的map数据
			//Map<String, String> cookie=rs.cookies();
			//for(String k:cookie.keySet()) {
			//System.out.println(k+"=="+cookie.get(k));
			//}
			/*
			 * con2.data("from","en");
			 * con2.data("to","zh");
			 * con2.data("query","success");
			 * con2.data("transtype","enter");
			 * con2.data("simple_means_flag","3");
			 * con2.data("sign","883095.629414");
			 * con2.data("token","9b8bb341109338ba7e875bd9a9dd88ba");
			 */
			Response login2 = con2.ignoreContentType(true).followRedirects(false).referrer("https://fanyi.baidu.com/").data(datas2).method(Method.POST).cookies(rs.cookies()).execute();
			System.out.println(login2.body());
			//String count=ge;tReadUrlJsDefault(href);
			//System.out.println(count);
			System.out.println("===============================================");
			getJString("jeep");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param url String
	 * @return Document
	 */
	public static Document manualRedirectHandler(String url) {
		try {
			Response response = Jsoup.connect(url).followRedirects(false).execute();
			int status = response.statusCode();
			System.out.println("Redirect to status:" + status);
			if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
				String redirectUrl = response.header("location");
				System.out.println("Redirect to:" + redirectUrl);
				return manualRedirectHandler(redirectUrl);
			}
			return Jsoup.parse(response.body());
		} catch (Exception e) {
			System.out.println("Redirect to Exception:" + e.toString());
			return null;
		}
	}

	/**
	 * 通过URL得到文件内容<br>
	 * 是否过滤#右侧数据
	 * @param url URL
	 * @param isReadUtf8 boolean
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(final URL url, final boolean isReadUtf8, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(20);
		if (url == null) return sb;
		try {
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(30000);
			urlcon.setReadTimeout(30000);
			urlcon.connect();
			String returnCode = new Integer(urlcon.getResponseCode()).toString();
			if (!returnCode.startsWith("2")) return null;
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = isReadUtf8 ? new InputStreamReader(is, "utf-8") : new InputStreamReader(is);
			BufferedReader buffer = new BufferedReader(isr);
			String l = null;
			while ((l = buffer.readLine()) != null) {
				if (delnotes && l.indexOf('#') >= 0) l = l.substring(0, l.indexOf('#'));
				if (l.length() == 0) continue;
				sb.append(l);
				sb.append(enterStr);
			}
			buffer.close();
			is.close();
			return sb;
		} catch (IOException e) {
			return sb;
		}
	}

	/**
	 * 读取url，并进行解码
	 * @param urlString String
	 * @param encoding String
	 * @return String
	 */
	public static String readUrl(String urlString, String encoding) {
		InputStream is = null;
		ByteArrayOutputStream os = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setConnectTimeout(30000);
			uc.setReadTimeout(30000);
			uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
			uc = (HttpURLConnection) reload(uc);
			is = uc.getInputStream();
			os = new ByteArrayOutputStream(is.available());
			int len;
			byte[] bytes = new byte[1024 * 8];
			while ((len = is.read(bytes)) != -1) {
				os.write(bytes, 0, len);
			}
			if (!encoding.equals("auto")) return new String(os.toByteArray(), encoding);
			Document document = Jsoup.parseBodyFragment(os.toString());//Jsoup.parse(url, 1000 * 60 * 30);
			Elements elements = document.select("meta");
			Iterator<Element> i = elements.iterator();
			while (i.hasNext()) {
				Element element = i.next();
				if (element.attr("http-equiv").equals("Content-Type")) {
					String content = element.attr("content").trim();
					int index = content.indexOf("=");
					encoding = content.substring(index + 1);
					break;
				}
			}
			if (encoding.equals("auto")) encoding = "gb2312";
			return new String(os.toByteArray(), encoding);
		} catch (Exception e) {
			return "";
		} finally {
			if (os != null) try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (is != null) try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param uc URLConnection
	 * @return URLConnection
	 * @throws Exception
	 */
	private static URLConnection reload(URLConnection uc) throws Exception {
		HttpURLConnection huc = (HttpURLConnection) uc;
		if (huc.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || huc.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM)// 302, 301
			return reload(new URL(huc.getHeaderField("location")).openConnection());
		return uc;
	}

	/**
	 * @param url URL
	 * @return InputStream
	 */
	public static InputStream returnBitMap(URL url) {
		InputStream is = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象,我们可以从网络中获取网页数据.  
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream(); //得到网络返回的输入流  
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}
}
