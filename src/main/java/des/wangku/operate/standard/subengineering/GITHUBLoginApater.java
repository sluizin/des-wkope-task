package des.wangku.operate.standard.subengineering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import des.wangku.operate.standard.utls.UtilsConsts;

public class GITHUBLoginApater {

	//  public static String LOGIN_URL = "https://login.taobao.com/member/login.jhtml?style=b2b&css_style=b2b&from=b2b&newMini2=true&full_redirect=true&redirect_url=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688&reg=http%3A%2F%2Fmember.1688.com%2Fmember%2Fjoin%2Fenterprise_join.htm%3Flead%3Dhttp%253A%252F%252Fmember.1688.com%252Fmember%252Foperations%252Fmember_operations_jump_engine.htm%253Ftracelog%253Dlogin%2526operSceneId%253Dafter_pass_from_taobao_new%2526defaultTarget%253Dhttp%25253A%25252F%25252Fwork.1688.com%25252F%25253Ftracelog%25253Dlogin_target_is_blank_1688%26leadUrl%3Dhttp%253A%252F%252Fmember.1688.com%252Fmember%252Foperations%252Fmember_operations_jump_engine.htm%253Ftracelog%253Dlogin%2526operSceneId%253Dafter_pass_from_taobao_new%2526defaultTarget%253Dhttp%25253A%25252F%25252Fwork.1688.com%25252F%25253Ftracelog%25253Dlogin_target_is_blank_1688%26tracelog%3Dmember_signout_signin_s_reg";
	public static String LOGIN_URL = "https://login.taobao.com/member/login.jhtml";
	//static String logurl="https://login.taobao.com/member/login.jhtml?style=b2b&amp;css_style=b2b&amp;from=b2b&amp;newMini2=true&amp;full_redirect=true&amp;redirect_url=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688&amp;reg=http%3A%2F%2Fmember.1688.com%2Fmember%2Fjoin%2Fenterprise_join.htm%3Flead%3Dhttp%253A%252F%252Fmember.1688.com%252Fmember%252Foperations%252Fmember_operations_jump_engine.htm%253Ftracelog%253Dlogin%2526operSceneId%253Dafter_pass_from_taobao_new%2526defaultTarget%253Dhttp%25253A%25252F%25252Fwork.1688.com%25252F%25253Ftracelog%25253Dlogin_target_is_blank_1688%26leadUrl%3Dhttp%253A%252F%252Fmember.1688.com%252Fmember%252Foperations%252Fmember_operations_jump_engine.htm%253Ftracelog%253Dlogin%2526operSceneId%253Dafter_pass_from_taobao_new%2526defaultTarget%253Dhttp%25253A%25252F%25252Fwork.1688.com%25252F%25253Ftracelog%25253Dlogin_target_is_blank_1688%26tracelog%3Dmember_signout_signin_s_reg";
	static String logurl = "https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688";
	public static String USER_AGENT = "User-Agent";
	public static String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0";

	public static void main(String[] args) throws Exception {

		simulateLogin("sluizin", "Sunjian1978"); // 模拟登陆github的用户名和密码

	}

	/**
	 * @param userName 用户名
	 * @param pwd 密码
	 * @throws Exception 异常
	 */
	@SuppressWarnings("unused")
	public static void simulateLogin(String userName, String pwd) throws Exception {

		/*
		 * 第一次请求
		 * grab login form page first
		 * 获取登陆提交的表单信息，及修改其提交data数据（login，password）
		 */
		// get the response, which we will post to the action URL(rs.cookies())

		//String pageXml = UtilsReadURL.getReadUrlJs(logurl);
		//Connection connect = Jsoup.connect(urlString).headers(UtilsConsts.header_a);
		//Document document = connect.timeout(timeout).maxBodySize(0).get();
		//Document d2 = Jsoup.parse(pageXml, "https://s.1688.com");

		//System.out.print("document:"+pageXml);

		Connection con = Jsoup.connect(logurl);  // 获取connection
		con.headers(UtilsConsts.header_a);
		// con.header(USER_AGENT, USER_AGENT_VALUE);   // 配置模拟浏览器
		Response rs = con.execute();                // 获取响应
		Document d1 = Jsoup.parse(rs.body());       // 转换为Dom树
		// System.out.println("d1:"+d1.html());
		List<Element> eleList = d1.getElementById("J_Form").children();//.select("form");  // 获取提交form表单，可以通过查看页面源码代码得知
		// System.out.println("d1.getElementById(\"J_Form\"):"+d1.getElementById("J_Form").html());
		// 获取cooking和表单属性
		// lets make data map containing all the parameters and its values found in the form
		Map<String, String> datas = new HashMap<>();
		List<Element> eleLists = d1.getElementById("J_Form").select("input");
		for (Element e : eleLists) {
			//System.out.println("e::"+e.attr("name"));
		}
		// eleList.get(0).getAllElements()
		for (Element e : eleLists) {
			// 设置用户名
			if (e.attr("name").equals("TPL_username")) {
				e.attr("value", userName);
			}
			// 设置用户密码
			if (e.attr("name").equals("TPL_password")) {
				e.attr("value", pwd);
			}
			// 排除空值表单属性
			if (e.attr("name").length() > 0) {
				datas.put(e.attr("name"), e.attr("value"));
			}
			//System.out.println(e.attr("name")+"-->"+e.attr("value"));
		}

		/*
		 * 第二次请求，以post方式提交表单数据以及cookie信息
		 */
		String logaddress = "";
		logaddress = "https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688";
		//logaddress="https://login.1688.com/member/signin.htm?tracelog=member_signout_signin";
		//logaddress="https://login.taobao.com/member/login.jhtml?style=b2b&amp;css_style=b2b&amp;from=b2b&amp;newMini2=true&amp;full_redirect=true&amp;redirect_url=https%3A%2F%2Flogin.1688.com%2Fmember%2Fjump.htm%3Ftarget%3Dhttps%253A%252F%252Flogin.1688.com%252Fmember%252FmarketSigninJump.htm%253FDone%253Dhttp%25253A%25252F%25252Fmember.1688.com%25252Fmember%25252Foperations%25252Fmember_operations_jump_engine.htm%25253Ftracelog%25253Dlogin%252526operSceneId%25253Dafter_pass_from_taobao_new%252526defaultTarget%25253Dhttp%2525253A%2525252F%2525252Fwork.1688.com%2525252F%2525253Ftracelog%2525253Dlogin_target_is_blank_1688&amp;reg=http%3A%2F%2Fmember.1688.com%2Fmember%2Fjoin%2Fenterprise_join.htm%3Flead%3Dhttp%253A%252F%252Fmember.1688.com%252Fmember%252Foperations%252Fmember_operations_jump_engine.htm%253Ftracelog%253Dlogin%2526operSceneId%253Dafter_pass_from_taobao_new%2526defaultTarget%253Dhttp%25253A%25252F%25252Fwork.1688.com%25252F%25253Ftracelog%25253Dlogin_target_is_blank_1688%26leadUrl%3Dhttp%253A%252F%252Fmember.1688.com%252Fmember%252Foperations%252Fmember_operations_jump_engine.htm%253Ftracelog%253Dlogin%2526operSceneId%253Dafter_pass_from_taobao_new%2526defaultTarget%253Dhttp%25253A%25252F%25252Fwork.1688.com%25252F%25253Ftracelog%25253Dlogin_target_is_blank_1688%26tracelog%3Dmember_signout_signin_s_reg";
		//logaddress="https://login.1688.com/member/signin.htm";
		//logaddress="https://login.taobao.com//member/login.jhtml";
		//logaddress="https://login.taobao.com/member/login.jhtml";
		Connection con2 = Jsoup.connect(logaddress);
		con2.header(USER_AGENT, USER_AGENT_VALUE);
		// 设置cookie和post上面的map数据
		Response login = con2.ignoreContentType(true).followRedirects(true).method(Method.POST).data(datas).cookies(rs.cookies()).execute();
		// 打印，登陆成功后的信息
		// parse the document from response
		//System.out.println(login.body());

		// 登陆成功后的cookie信息，可以保存到本地，以后登陆时，只需一次登陆即可
		Map<String, String> map = login.cookies();
		for (String s : map.keySet()) {
			System.out.println(s + " : " + map.get(s));
		}

	}

}
