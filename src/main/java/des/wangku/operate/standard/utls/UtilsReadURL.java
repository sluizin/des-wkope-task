package des.wangku.operate.standard.utls;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import org.apache.log4j.Logger;
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
	static Logger logger = Logger.getLogger(UtilsReadURL.class);

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
		try {
			Socket socket = null;
			if ("http".equals(http)) {
				socket = new Socket(InetAddress.getByName(host), port);
			} else {
				socket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(InetAddress.getByName(host), port);
			}
			//socket.setKeepAlive(true);
			socket.setSoTimeout(timeout);
			//socket.setSendBufferSize(200);
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
				sb.append(line);
				if (line.contains("</html>")) break;
			}
			br.close();
			bw.close();
			if (socket != null && !socket.isClosed()) socket.close();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 从url中判断是否含有关键字数组，在html之间<br>
	 * 只支持http与https协议
	 * @param url String
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
	 * 80端口 socket
	 * @param host String
	 * @param url String
	 * @param code String
	 * @return String
	 */
	public static String getRealContent(String host, String url, String code) {
		StringBuilder sb = new StringBuilder();
		try (Socket socket = new Socket(host, 80);) {
			socket.setSoTimeout(30000);
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
	 * 80端口 socket
	 * @param domain String 域名(除去协议)
	 * @param url String 以/开始的右侧url
	 * @param code String
	 * @return Document
	 */
	public static final Document getRealContentDocument(String domain, String url, String code) {
		String content = UtilsReadURL.getRealContent(domain, url, code);
		Document doc = Jsoup.parse(content);
		return doc;
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

	public static void main(String[] args) {
		String href = "http://www.ebrun.com/20160429/174163.shtml";
		try {
			URL url = new URL(href);
			System.out.println("result:" + getSocketContentState(url, "GBK"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (urlString.indexOf(key) > -1 && (!UtilsString.isfilterArr(urlString, filterArr))) return urlString;
		String baiduUlr = UtilsReadURL.getRealLocation(urlString);
		if (baiduUlr != null && baiduUlr.indexOf(key) > -1 && (!UtilsString.isfilterArr(baiduUlr, filterArr))) return baiduUlr;
		try {
			URL url = new URL(urlString);
			URL newurl = UtilsReadURL.getReadURL(url);
			if (newurl == null) return "";
			String urlStr = newurl.toString();
			if (urlStr.indexOf(key) > -1 && (!UtilsString.isfilterArr(urlStr, filterArr))) return urlStr;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "";
	}

	static final NicelyResynchronizingAjaxController nicelyAjax = new NicelyResynchronizingAjaxController();
	static final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52);
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
	 * 调用url，并进行js运行，得到html
	 * @param url String
	 * @param jsEnable boolean
	 * @param cssEnable boolean
	 * @param timeout int
	 * @param jsTime long
	 * @return String
	 */
	public static final String getReadUrlJs(String url, boolean jsEnable, boolean cssEnable, int timeout, long jsTime) {
		/** HtmlUnit请求web页面 */
		WebClient wc = new WebClient(BrowserVersion.FIREFOX_52);
		wc.getOptions().setJavaScriptEnabled(jsEnable); //启用JS解释器，默认为true  
		wc.getOptions().setCssEnabled(cssEnable); //禁用css支持  
		wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常  
		wc.getOptions().setTimeout(timeout); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待  
		wc.waitForBackgroundJavaScript(jsTime);
		//wc.getOptions().setAppletEnabled(true);
		// wc.setAjaxController(nicelyAjax);
		//wc.setCssErrorHandler(new SilentCssErrorHandler());
		// wc.getOptions().setPopupBlockerEnabled(true);
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
	 * 调用url，并进行js运行，得到html
	 * @param url String
	 * @return String
	 */
	public static final String getReadUrlJs(String url) {
		return getReadUrlJs(url, true, false, 20000, 10000);
	}

	/**
	 * 调用url，并进行js运行，得到Document
	 * @param url String
	 * @return Document
	 */
	public static final Document getReadUrlJsDocument(String url) {
		String pageXml2 = UtilsReadURL.getReadUrlJs(url, true, true, 20000, 20000);
		String baseuri = UtilsString.getUrlDomain(url);
		return Jsoup.parse(pageXml2, baseuri);
	}

	/**
	 * 从网址中得到classname的text字符串 为null时返回""
	 * @param url String
	 * @param classname String
	 * @param index int
	 * @return String
	 */
	public static final String getReadUrlJsTextByClassname(String url, String classname, int index) {
		logger.debug("url:" + url);
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
			request.setProxyHost("120.120.120.x");
			request.setProxyPort(8080);
			request.setAdditionalHeader("Referer", refer);//设置请求报文头里的refer字段  
			////设置请求报文头里的User-Agent字段  
			request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
			/** HtmlUnit请求web页面 */
			WebClient wc = new WebClient(BrowserVersion.FIREFOX_52);
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
			con.headers(UtilsConsts.header_a);// 配置模拟浏览器
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
			con2.headers(UtilsConsts.header_a);
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
	 * 参数 - 会员登陆时需要使用的变量，如是否使用会员登陆、登陆界面的一些信息参数
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class LoginParameter {
		boolean isUser = false;
		String logUrl = "";
		String logcheckUrl = "";
		String formID = "";
		String inputNameUsername = "";
		String username = "";
		String inputNamePassword = "";
		String password = "";

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
			con.headers(UtilsConsts.header_a);// 配置模拟浏览器
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
			con2.headers(UtilsConsts.header_a);
			/* 设置cookie和post上面的map数据 */
			Response login = con2.ignoreContentType(true).followRedirects(true).method(method).data(datas).cookies(rs.cookies()).execute();
			if (login != null) return login.body();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String readUrl(String urlString, String encoding) throws IOException {
		URL url = new URL(urlString);
		InputStream is = null;
		ByteArrayOutputStream os = null;
		try {
			//            is = url.openStream();
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
			if (encoding.equals("auto")) {
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
				if (encoding.equals("auto")) {
					encoding = "gb2312";
				}
				return new String(os.toByteArray(), encoding);
			} else {
				return new String(os.toByteArray(), encoding);
			}
		} catch (Exception e) {
			return "";
		} finally {
			if (os != null) os.close();
			if (is != null) is.close();
		}
	}

	private static URLConnection reload(URLConnection uc) throws Exception {
		HttpURLConnection huc = (HttpURLConnection) uc;
		if (huc.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || huc.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM)// 302, 301
			return reload(new URL(huc.getHeaderField("location")).openConnection());
		return uc;
	}

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
}
