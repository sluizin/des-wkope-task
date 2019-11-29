package des.wangku.operate.standard.webserver.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import des.wangku.operate.standard.Pv;
import des.wangku.operate.standard.Pv.Env;

/**
 * 发送电子邮件<br>
 * 通过form发送
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@Controller
@RequestMapping(value = "/MQ/sendemail")
public class SendEmail {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(SendEmail.class);
	@Autowired
	HttpServletRequest request;
	
	/**
	 * 以 泛型 的方式接收传递值
	 * @param request HttpServletRequest
	 * @return boolean
	 */
	@ResponseBody
	@RequestMapping(value = "/sendparadigm")
	public boolean sendFormParadigm(HttpServletRequest request) {
		String sendEmailTitle = request.getParameter("sendEmailTitle");
		String emailContent = Utils.getParadigmEmailContent(request);
		List<String> addressList = Utils.getParameterList(request, "sendEmailAddress");
		String[] arr = new String[0];
		String key1 = "sunjian@99114.com";
		if ((Pv.ACC_ENV == Env.DEV)) {
			addressList.clear();
			addressList.add(key1);
		}
		arr = addressList.toArray(arr);
		String a =sendHtmlMail("1", sendEmailTitle, emailContent, arr);
		if (a == null) return false;
		return a.equals("success");
	}	

	/**
	 * 邮件发送接口
	 * @param key String
	 * @param emailTitle String
	 * @param emailContent String
	 * @param address String[]
	 * @return String
	 */
	public String sendHtmlMail(String key, String emailTitle, String emailContent, String... address) {
		for(String addr:address) {
			if(addr==null || addr.trim().length()==0)continue;
			//(emailContent, emailTitle, key, add);
		}
		return "success";
	}

	/**
	 * 针对使用泛型邮件输入的参数进行甄别<br>
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.7
	 */
	public static final class Utils {
		/** 泛型参数名称的尾部关键字 */
		private static final String ACC_PARAPARANAMETAIL = "_title";

		/**
		 * 把所有参数对按参数名顺序输入到emailContent中
		 * @param request HttpServletRequest
		 * @return String
		 */
		static String getParadigmEmailContent(HttpServletRequest request) {
			StringBuilder sb = new StringBuilder(100);
			List<String> parameterList = Utils.getParadigmNameList(request);
			Collections.sort(parameterList);
			String key;
			sb.append("<style>.table-b table{border:1px solid #F00}.table-b table td{border:1px solid #F00}</style> ");
			sb.append("<table class='able-b' border='1' cellspacing='0' cellpadding='0'>");
			for (int i = 0, len = parameterList.size(); i < len; i++) {
				sb.append("<tr height='45'>");
				key = parameterList.get(i);
				sb.append("<td width='120' align='right'>" + request.getParameter(key + Utils.ACC_PARAPARANAMETAIL) + "：</td>");
				sb.append("<td width='700'>" + request.getParameter(key) + "</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			return sb.toString();
		}
		/**
		 * 得到key的多个值，适用于多个input输入
		 * @param request HttpServletRequest
		 * @param key String
		 * @return List&lt;String&gt;
		 */
		public static List<String> getParameterList(HttpServletRequest request, String key) {
			List<String> list = new ArrayList<>();
			if (request == null) return list;
			Enumeration<?> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = (String) paramNames.nextElement();
				if (!paramName.equals(key)) continue;
				String[] paramValues = request.getParameterValues(paramName);
				for(String e:paramValues) {
					list.add(e);
				}
				break;
			}
			return list;
		}

		/**
		 * 得到所有参数对的参数名 abc 如果有abc_title，则保存abc
		 * @param request HttpServletRequest
		 * @return List&lt;String&gt;
		 */
		public static List<String> getParadigmNameList(HttpServletRequest request) {
			Map<String, String> map = getRequestParameterMap(request);
			List<String> list = new ArrayList<String>();
			for (String key : map.keySet())
				if (map.containsKey(key + ACC_PARAPARANAMETAIL)) list.add(key);
			return list;
		}

		/**
		 * 通过 HttpServletRequest 得到参数map 只允许一个值
		 * @param request HttpServletRequest
		 * @return Map&lt;String, String&gt;
		 */
		public static Map<String, String> getRequestParameterMap(HttpServletRequest request) {
			Map<String, String> map = new HashMap<>();
			Map<String, List<String>> allMap = requestParameterToMap(request);
			for (String key : allMap.keySet())
				map.put(key, allMap.get(key).get(0));
			return map;
		}

		/**
		 * 通过 HttpServletRequest 得到参数map 允许多个同名参数值用list保存
		 * @param request HttpServletRequest
		 * @return Map&lt;String, List&lt;String&gt;&gt;
		 */
		public static Map<String, List<String>> requestParameterToMap(HttpServletRequest request) {
			Map<String, List<String>> map = new HashMap<>();
			if (request == null) return map;
			Enumeration<?> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = (String) paramNames.nextElement();
				String[] paramValues = request.getParameterValues(paramName);
				if (paramValues.length == 0) continue;
				List<String> list = new ArrayList<>(paramValues.length);
				for(String e:paramValues) {
					list.add(e);
				}
				map.put(paramName, list);
			}
			return map;
		}
		
	}
}
