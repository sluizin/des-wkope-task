package des.wangku.operate.standard.subengineering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import des.wangku.operate.standard.utls.UtilsConsts;
import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsString;

/**
 * 阿里一些信息的提取
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class AlibabaSearch {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AlibabaSearch.class);
	//static final String username = "sluizin";
	//static final String password = "Sunjian1978";
	/**
	 * 得到商家总页数
	 * @param para parameter
	 * @param keyword String
	 * @return int
	 */
	public static final int getEnterprisePageCount(parameter para, String keyword) {
		if (keyword == null || keyword.length() == 0) return 0;
		try {
			String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
			Map<String, String>  map=AlibabaSearch.get1688LoginCookies(para);
			String urlString = "https://s.1688.com/company/company_search.htm?keywords=" + newKeyword + "&earseDirect=false&button_click=top&n=y&pageSize=30&offset=3&beginPage=1";
			logger.debug("urlString:" + urlString);
			Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
			Document document = connect.timeout(10000).maxBodySize(0).cookies(map).followRedirects(false).get();//.post();
			if (document == null) return 0;
			//System.out.println(document.toString());
			Element totalpage = document.getElementsByClass("total-page").first();
			if (totalpage == null) return 0;
			String value = totalpage.text();
			logger.debug("value:" + value);
			int val = UtilsString.getNumbersInt(value, "共\\d+页");
			return val;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 得到某页的商家列表
	 * @param para parameter
	 * @param keyword String
	 * @param city String
	 * @param p int
	 * @param timeout int
	 * @param isArchives boolean
	 * @param isContactinformation boolean
	 * @return List &lt; AlibabaEnterpriseClass &gt; 
	 */
	public static final List<AlibabaEnterpriseClass> getEnterprise(parameter para,String keyword,String city, int p, int timeout, boolean isArchives, boolean isContactinformation) {
		List<AlibabaEnterpriseClass> list = new ArrayList<>();
		if (keyword == null || keyword.length() == 0) return list;
		try {
			String newKeyword = java.net.URLEncoder.encode(keyword, "GBK");
			String newCity = java.net.URLEncoder.encode(city, "GBK");
			System.out.println("city："+city);
			System.out.println("newCity："+newCity);
			//String a = "https://s.1688.com/company/company_search.htm?keywords=" ;
			//a+= newKeyword + "&earseDirect=false&button_click=top&n=y&pageSize=30&offset=3&beginPage=" + p;
			StringBuilder sb=new StringBuilder();
			sb.append( "https://s.1688.com/company/company_search.htm?keywords="+newKeyword);
			if(city!=null && city.length()>0) {
				sb.append("&city="+newCity);
			}
			sb.append("&earseDirect=false&button_click=top&n=y&pageSize=30&offset=3&beginPage=" + p);
			
			
			String urlString= sb.toString();
			logger.debug("urlString:" +urlString);
			
			
			//String urlString2 = "https://s.1688.com/company/company_search.htm?keywords=" + newKeyword + "&earseDirect=false&button_click=top&n=y&pageSize=30&offset=3&beginPage=1";
			//logger.debug("urlString2:" + urlString2);
			Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);
			Document document = connect.timeout(10000).maxBodySize(0).cookies(para.map).post();
			
			
			
			
			
			
			//String pageXml = UtilsReadURL.getReadUrlJs(urlString);
			//Map<String, String> map = AlibabaSearch.get1688LoginCookies(para);
			//Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.header_a);
			//Document document = connect.timeout(timeout).cookies(map).maxBodySize(0).get();
			//logger.debug("urlString:"+urlString);
			//logger.debug("pageXml:"+pageXml);
			//Document document = UtilsReadURL.getReadUrlJsDocument(urlString);//Jsoup.parse(pageXml, "https://s.1688.com");
			if (document == null) return list;
			//logger.debug("document:" + document.html());
			Element resultElement = document.getElementsByAttributeValue("data-spm", "result").first();
			if (resultElement == null) return list;
			Elements es = resultElement.getElementsByTag("li");
			//Elements es=document.getElementsByClass("sm-company-list");
			for (int x = 0; x < es.size(); x++) {
				AlibabaEnterpriseClass ec = new AlibabaEnterpriseClass();
				Element e = es.get(x);
				Elements titles = e.getElementsByClass("list-item-title-text");
				if (titles.size() == 0) continue;
				Element title = titles.get(0);
				if (title.text().length() == 0) continue;
				ec.name = title.text();
				Elements hrefs = title.select("a[href]");
				if (hrefs.size() > 0) {
					String baiduUrl = hrefs.get(0).attr("abs:href");
					String newUrl = UtilsReadURL.getRealMultiLocation(baiduUrl, ".1688.com", "/dj.1688.com");
					int index = newUrl.indexOf('/', 9);
					if (index > -1) newUrl = newUrl.substring(0, index);
					//logger.debug(baiduUrl+" --> "+newUrl);
					//logger.debug("=========================");
					ec.url = newUrl;
				}
				ec.page=p;
				Elements details = e.getElementsByClass("list-item-detail");
				Element detail = details.first();
				if (detail != null) {
					Element left = detail.getElementsByClass("detail-left").first();
					Element right = detail.getElementsByClass("detail-right").first();
					ec.setElements(left, right);
				}
				if (ec.name.length() > 0) {
					if (isArchives) ec.getArchives();
					if (isContactinformation) ec.getContactinformation();
					list.add(ec);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}



	//static final String Log1688url = "https://login.taobao.com/member/login.jhtml?style=b2b&amp;css_style=b2b&amp;from=b2b&amp;newMini2=true&amp;full_redirect=true&amp;redirect_url=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688&amp;reg=http%3A%2F%2Fmember.1688.com%2Fmember%2Fjoin%2Fenterprise_join.htm%3Flead%3Dhttp%253A%252F%252Fmember.1688.com%252Fmember%252Foperations%252Fmember_operations_jump_engine.htm%253Ftracelog%253Dlogin%2526operSceneId%253Dafter_pass_from_taobao_new%2526defaultTarget%253Dhttp%25253A%25252F%25252Fwork.1688.com%25252F%25253Ftracelog%25253Dlogin_target_is_blank_1688%26leadUrl%3Dhttp%253A%252F%252Fmember.1688.com%252Fmember%252Foperations%252Fmember_operations_jump_engine.htm%253Ftracelog%253Dlogin%2526operSceneId%253Dafter_pass_from_taobao_new%2526defaultTarget%253Dhttp%25253A%25252F%25252Fwork.1688.com%25252F%25253Ftracelog%25253Dlogin_target_is_blank_1688%26tracelog%3Dmember_signout_signin_s_reg";
	//static final String Log1688url2 = "https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688";
	//static final String Log1688url = "https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688";
	//static final String logaddress = "https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688";
	static final String LogAddress = "https://login.taobao.com/member/login.jhtml?style=b2b&css_style=b2b&from=b2b&newMini2=true&full_redirect=true&redirect_url=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttps%25253A%25252F%25252Fsec.1688.com%25252Fquery.htm%25253Faction%25253DQueryAction%252526event_submit_do_login%25253Dok%252526smApp%25253Dsearchweb2%252526smPolicy%25253Dsearchweb2-company-anti_Spider-html-checklogin%252526smCharset%25253DGBK%252526smTag%25253DMTE3LjEwNy4xMzcuMzAsLDI2ZDMxOGI5YzBjMjRkODQ4ZDg0MDQwNzU1OTcxMjkw%252526smReturn%25253Dhttps%2525253A%2525252F%2525252Fs.1688.com%2525252Fcompany%2525252F-D1E0C2F3.html%2525253F%25252526amp%2525253BearseDirect%2525253Dfalse%25252526amp%2525253BsmToken%2525253D973ced069faa46be8f14375f9553b2c2%25252526amp%2525253Bbutton_click%2525253Dtop%25252526amp%2525253BpageSize%2525253D30%25252526amp%2525253Bn%2525253Dy%25252526amp%2525253Boffset%2525253D3%25252526amp%2525253BbeginPage%2525253D1%252526smSign%25253DmplKr43UiWJF1w30tRv57g%2525253D%2525253D&reg=http%3A%2F%2Fmember.1688.com%2Fmember%2Fjoin%2Fenterprise_join.htm%3Flead%3Dhttps%253A%252F%252Fsec.1688.com%252Fquery.htm%253Faction%253DQueryAction%2526event_submit_do_login%253Dok%2526smApp%253Dsearchweb2%2526smPolicy%253Dsearchweb2-company-anti_Spider-html-checklogin%2526smCharset%253DGBK%2526smTag%253DMTE3LjEwNy4xMzcuMzAsLDI2ZDMxOGI5YzBjMjRkODQ4ZDg0MDQwNzU1OTcxMjkw%2526smReturn%253Dhttps%25253A%25252F%25252Fs.1688.com%25252Fcompany%25252F-D1E0C2F3.html%25253F%252526amp%25253BearseDirect%25253Dfalse%252526amp%25253BsmToken%25253D973ced069faa46be8f14375f9553b2c2%252526amp%25253Bbutton_click%25253Dtop%252526amp%25253BpageSize%25253D30%252526amp%25253Bn%25253Dy%252526amp%25253Boffset%25253D3%252526amp%25253BbeginPage%25253D1%2526smSign%253DmplKr43UiWJF1w30tRv57g%25253D%25253D%26leadUrl%3Dhttps%253A%252F%252Fsec.1688.com%252Fquery.htm%253Faction%253DQueryAction%2526event_submit_do_login%253Dok%2526smApp%253Dsearchweb2%2526smPolicy%253Dsearchweb2-company-anti_Spider-html-checklogin%2526smCharset%253DGBK%2526smTag%253DMTE3LjEwNy4xMzcuMzAsLDI2ZDMxOGI5YzBjMjRkODQ4ZDg0MDQwNzU1OTcxMjkw%2526smReturn%253Dhttps%25253A%25252F%25252Fs.1688.com%25252Fcompany%25252F-D1E0C2F3.html%25253F%252526amp%25253BearseDirect%25253Dfalse%252526amp%25253BsmToken%25253D973ced069faa46be8f14375f9553b2c2%252526amp%25253Bbutton_click%25253Dtop%252526amp%25253BpageSize%25253D30%252526amp%25253Bn%25253Dy%252526amp%25253Boffset%25253D3%252526amp%25253BbeginPage%25253D1%2526smSign%253DmplKr43UiWJF1w30tRv57g%25253D%25253D%26tracelog%3Dnotracelog_s_reg";

	/**
	 * 会员模拟登陆，并得到cookies
	 * @param para parameter
	 * @return Map &lt; String, String &gt; 
	 */
	public static Map<String, String> get1688LoginCookies(parameter para) {
		Map<String, String> map = new HashMap<>();
		if (para == null || (!para.isUser)) return map;
		logger.debug("para.isUser:"+para.isUser);
		try {
			/*
			 * 第一次请求
			 * grab login form page first
			 * 获取登陆提交的表单信息，及修改其提交data数据（login，password）
			 */
			Connection con = Jsoup.connect(LogAddress);  // 获取connection
			con.headers(UtilsConsts.getRndHeadMap());//UtilsConsts.header_a);// 配置模拟浏览器
			Response rs = con.execute();                // 获取响应
			Document d1 = Jsoup.parse(rs.body());       // 转换为Dom树
			/*
			 * 获取cooking和表单属性
			 * lets make data map containing all the parameters and its values found in the form
			 */
			Map<String, String> datas = new HashMap<>();
			List<Element> eleLists = d1.getElementById("J_Form").select("input");
			for (Element e : eleLists) {
				if (e.attr("name").equals("TPL_username")) e.attr("value", para.username);
				if (e.attr("name").equals("TPL_password")) e.attr("value", para.password);
				if (e.attr("name").length() > 0) datas.put(e.attr("name"), e.attr("value"));
			}
			/*
			 * 第二次请求，以post方式提交表单数据以及cookie信息
			 */
			Connection con2 = Jsoup.connect(LogAddress);
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
			map.put("XSRF-TOKEN", "dfb0ff9c-13f7-41c6-8592-a37423ac035a");
			map.put("cad", "2rsWf3OBz95gPoRFasbZaLoIw/jzkmebFI7lrhOiEOQ=0001");
			map.put("cap", "65c3");
			map.put("cn_tmp","Z28mC+GqtZ2Z/IAbzEbDbQpm5KDPgBZ/D2onO5C64OcmUSJ3JoGgmyqW7t6ShONDbLeZaDVCEy/EoAX5jgg11urmzAIOvQDRmtW9n6m2c47tAIdoWtgHiC1JsP/prtDxVD8X2mOO4DmyevwaJdegrjQoZIEyBAjsFvmo66Mez2nSU1nEyPxaMLycS0lsMwOd3hQXRN5DrllfokcEHmTm937NENF4Udb83gocsrnQQd1wYzGDFjOrMzvNoR8QEUy5");
			map.put("cookie1", "W50Pchc77tANngk7j8AQ%2BWruNezL2cFsnuih1AtQUkc%3D");
			map.put("cookie17", "UUtCHrZulW0%3D");
			map.put("cookie2", "1f502ecabdca82199c9c9d537b697789");
			map.put("csg", "2ad9348d");
			map.put("enc", "QgeWsNWfoCUGVraL0iu3AgCjHi1cu4RHWpg11T%2FzyrJHXG%2F1xdW8S%2BobXmpB5%2BmxGS9PeZb6Wj%2BinTIIFpJPyA%3D%3D");
			map.put("tbsnid", "aA%2BQcPBVzgVGxlil895faiVFGWlmPraBnmAQzUyXmd86sOlEpJKl9g%3D%3D");
			map.put("unb", "23927030");
			map.put("userID", "vEP63r7ZCFJGsNUy%2FIfMK%2FvLAAaS1IRMa0Mhc1tKzmc6sOlEpJKl9g%3D%3D");
			map.put("userIDNum", "Ut4MP7TgTLM6sOlEpJKl9g%3D%3D");

			for (String s : map.keySet()) {
				System.out.println(s + "----: " + map.get(s));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 参数 - 会员登陆1688时是否需要用户名与密码
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 *
	 */
	public static final class parameter {
		boolean isUser = false;
		String username = "";
		String password = "";
		Map<String, String>  map=new HashMap<>();

		public parameter(boolean isUser, String username, String password) {
			this.isUser = isUser;
			this.username = username;
			this.password = password;
		}
		public void init() {
			this.map=AlibabaSearch.get1688LoginCookies(this);
		}
		@Override
		public String toString() {
			return "parameter [isUser=" + isUser + ", " + (username != null ? "username=" + username + ", " : "") + (password != null ? "password=" + password : "") + "]";
		}
		
	}
}
