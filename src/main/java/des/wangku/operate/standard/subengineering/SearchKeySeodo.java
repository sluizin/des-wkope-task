package des.wangku.operate.standard.subengineering;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import com.alibaba.fastjson.JSON;
import des.wangku.operate.standard.utls.UtilsConsts;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class SearchKeySeodo {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(SearchKeySeodo.class);

	public static final int NoFindNull = -1;

	/**
	 * 得到单位值
	 * @param key String
	 * @return int
	 */
	private static final int getPosid(String key) {
		if (key == null) return NoFindNull;
		key = key.trim();
		if (key.length() == 0) return NoFindNull;
		try {
			String url = "http://www.seodo.cn/api/quote";
			Connection connect = Jsoup.connect(url).headers(UtilsConsts.header_b).timeout(2000).ignoreContentType(true).data("id", key);
			Response res = connect.execute();
			String body = res.body();
			Result result = JSON.parseObject(body, Result.class);
			if (result == null) return NoFindNull;
			int price = result.Data.Price;
			if (price == 0) return 0;
			return price;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return NoFindNull;
	}

	private static final int getPrice(int price, int months) {
		double p = price * 30 * (1 - 0.05 * (months - 1));
		if (months == 1) p = p * 2;
		if (months == 2) p = getPrice(price, 1) / 2;
		return Integer.parseInt(new java.text.DecimalFormat("0").format(p));
	}
	/**
	 * 得到价格
	 * @param key String
	 * @return int
	 */
	public static final int getPricePosid(String key) {
		int price = getPosid(key);
		if (price == NoFindNull) return NoFindNull;
		return getPrice(price, 1);
	}

	public static void main(String[] args) {
		String key = "大米";
		System.out.println(key + ":" + getPricePosid(key));
	}

	/**
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	static class Result {
		int Status = 0;
		String success = "";
		Data Data = new Data();

		public final int getStatus() {
			return Status;
		}

		public final void setStatus(int status) {
			Status = status;
		}

		public final String getSuccess() {
			return success;
		}

		public final void setSuccess(String success) {
			this.success = success;
		}

		public final Data getData() {
			return Data;
		}

		public final void setData(Data data) {
			Data = data;
		}

	}

	/**
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	static class Data {
		String Word = "";
		String DeWord = "";
		int Price = 0;
		int Days = 0;
		String Token = "";

		public final String getWord() {
			return Word;
		}

		public final void setWord(String word) {
			Word = word;
		}

		public final String getDeWord() {
			return DeWord;
		}

		public final void setDeWord(String deWord) {
			DeWord = deWord;
		}

		public final int getPrice() {
			return Price;
		}

		public final void setPrice(int price) {
			Price = price;
		}

		public final int getDays() {
			return Days;
		}

		public final void setDays(int days) {
			Days = days;
		}

		public final String getToken() {
			return Token;
		}

		public final void setToken(String token) {
			Token = token;
		}

	}
}
