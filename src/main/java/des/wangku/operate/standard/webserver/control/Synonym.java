package des.wangku.operate.standard.webserver.control;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.google.common.base.Joiner;

import static des.wangku.operate.standard.Pv.ACC_ENV;
import des.wangku.operate.standard.Pv.Env;
import des.wangku.operate.standard.database.DBSource;

/**
 * 同义词
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@Controller
@RequestMapping(value = "/MQ/Synonym")
public class Synonym {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(Synonym.class);
	@Autowired
	HttpServletRequest request;

	/**
	 * 得到同义词
	 * @param request HttpServletRequest
	 * @param word String
	 * @return Object
	 */
	@ResponseBody
	@RequestMapping(value = "/search/{word}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
	public Object searchWord(HttpServletRequest request, @PathVariable(required = true) String word) {
		if (word == null) return "";
		word = word.trim();
		if (word.length() == 0) return "";
		logger.debug("word:" + word);
		return Utils.getExistWords(word);
	}

	@ResponseBody
	@RequestMapping(value = "/searchs/{words}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
	public Object searchWords(HttpServletRequest request, @PathVariable(required = true) String words) {
		String[] arrs = words.split(",");
		List<String> list = new ArrayList<>(arrs.length);
		for (String e : arrs) {
			if (e == null || e.trim().length() == 0) continue;
			list.add(e);
		}
		return Utils.getExistWords(list);
	}

	/**
	 * 建立lucene
	 * @param request HttpServletRequest
	 * @return boolean
	 */
	@ResponseBody
	@RequestMapping(value = "/makeLucene")
	public boolean makeLucene(HttpServletRequest request) {

		return false;
	}

	public static void main(String[] args) {
		initConn();
		/*
		 * String word="支持";
		 * Set<String> set =Utils.getExistWord(word);
		 * for(String e:set) {
		 * System.out.println("result:"+e);
		 * }
		 * System.out.println("json:"+JSON.toJSONString(set));
		 */
		String content = "不建议在没有服务器身份验证的情况下建立SSL连接。" + "根据MySQL 5.5.45+、5.6.26+和5.7.6+的要求，如果不设置显式选项，" + "则必须建立默认的SSL连接。需要通过设置useSSL=false来显式禁用SSL，" + "或者设置useSSL=true并为服务器证书验证提供信任存储";
		IKSegmenter ik = new IKSegmenter(new StringReader(content), true);
		StringBuilder result = new StringBuilder();
		try {
			Lexeme word = null;
			while ((word = ik.next()) != null) {
				result.append(word.getLexemeText()).append(" ");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		System.out.println(result.toString());
		/*
		String fileName="e:/abc.txt";
		String sql = "select word from synonymword ;";
		try (Statement stmt = wk_WordConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
			ResultSet rs = stmt.executeQuery(sql);

			File file = new File(fileName);
			if (!file.exists()) file.createNewFile();
			CharArrayWriter caw = new CharArrayWriter();
			while (rs.next()) {
				String line=rs.getString(1);
				caw.write(line);
				caw.append(System.getProperty("line.separator"));
			}
			FileWriter out = new FileWriter(file);
			caw.writeTo(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
	}

	static final void initConn() {
		if (wk_WordConn != null) return;
		String ip = "172.16.3.83";
		if (ACC_ENV == Env.DEV) ip = "127.0.0.1";
		wk_WordConn = DBSource.getMYSQL(ip, "3306", "wk_word", "wk_worduser", "wk_worduser");
	}

	static Connection wk_WordConn = null;

	public static final class Utils {

		public static final Set<String> getExistWords(List<String> list) {
			if(list.size()==0)return new TreeSet<>();
			String[] a = {};
			return getExistWords(list.toArray(a));
		}

		public static final Set<String> getExistWords(String... arrs) {
			initConn();
			Set<String> set = new TreeSet<>();
			if (wk_WordConn == null || arrs.length == 0) return set;
			String sql = null;
			if (arrs.length == 1) {
				String word = arrs[0];
				if (word == null || word.trim().length() == 0) return set;
				sql = "select word from synonymword where word like '%," + arrs[0] + ",%';";
			} else {
				String str = Joiner.on("|").skipNulls().join(arrs);
				sql = "select word from synonymword where word regexp '" + str + "';";
			}
			try (Statement stmt = wk_WordConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					List<String> list = decomposeWordLine(rs.getString(1));
					set.addAll(list);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return set;
		}

		/**
		 * 分解word字符串，把以,为间隔的字符串分成list，并过滤空
		 * @param line String
		 * @return List&lt;String&gt;
		 */
		static final List<String> decomposeWordLine(String line) {
			if (line == null || line.trim().length() == 0) return new ArrayList<>();
			String[] arrs = line.split(",");
			List<String> list = new ArrayList<>(arrs.length);
			for (String e : arrs) {
				if (e == null) continue;
				e = e.trim();
				if (e.length() == 0) continue;
				list.add(e);
			}
			return list;
		}
	}
}
