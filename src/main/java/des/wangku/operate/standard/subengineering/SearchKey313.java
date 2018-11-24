package des.wangku.operate.standard.subengineering;

import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSON;
import des.wangku.operate.standard.utls.UtilsReadURL;
import des.wangku.operate.standard.utls.UtilsRnd;
/**
 * 313
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public final class SearchKey313 {
	/** 日志 */
	static Logger logger = Logger.getLogger(SearchKey313.class);
	
	public static final String NoFindNull = "无法预估";
	
	public static  final String getBaiduPosid(String key) {
		if(key==null)return NoFindNull;
		key=key.trim();
		if(key.length()==0)return NoFindNull;
		String content="";
		String url="";
		try {
			//key=URLDecoder.decode(key, "utf-8");
			//Thread.sleep(500);
			url="https://www.313.cn/s/result.asp?the_Type=1&keyword="+key+"&t="+UtilsRnd.getNewFilenameNow(2, 1);
			content=UtilsReadURL.getUrlContent(url, "utf-8", 10000);
			//content=UtilsReadURL.getReadUrlJs(url);
			//URL urln=new URL(url);
			//content=UtilsReadURL.getSocketContent(urln, "utf-8",10000);
			//logger.debug("url:"+url);
			//logger.debug("content:"+content);
			Result313 result=JSON.parseObject(content, Result313.class);
			return result.first_Page;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return NoFindNull;
	}
	/**
	 * 
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 *
	 */
	public static final class Result313{
		String the_type="";
		String type_="";
		String first_Page="";
		String one_month="";
		String baidu_index="";
		String index_counts="";
		String search_counts="";
		String id="";
		String days="";
		public final String getThe_type() {
			return the_type;
		}
		public final void setThe_type(String the_type) {
			this.the_type = the_type;
		}
		public final String getType_() {
			return type_;
		}
		public final void setType_(String type_) {
			this.type_ = type_;
		}
		public final String getFirst_Page() {
			return first_Page;
		}
		public final void setFirst_Page(String first_Page) {
			this.first_Page = first_Page;
		}
		public final String getOne_month() {
			return one_month;
		}
		public final void setOne_month(String one_month) {
			this.one_month = one_month;
		}
		public final String getBaidu_index() {
			return baidu_index;
		}
		public final void setBaidu_index(String baidu_index) {
			this.baidu_index = baidu_index;
		}
		public final String getIndex_counts() {
			return index_counts;
		}
		public final void setIndex_counts(String index_counts) {
			this.index_counts = index_counts;
		}
		public final String getSearch_counts() {
			return search_counts;
		}
		public final void setSearch_counts(String search_counts) {
			this.search_counts = search_counts;
		}
		public final String getId() {
			return id;
		}
		public final void setId(String id) {
			this.id = id;
		}
		public final String getDays() {
			return days;
		}
		public final void setDays(String days) {
			this.days = days;
		}
		@Override
		public String toString() {
			return "Result313 [" + (the_type != null ? "the_type=" + the_type + ", " : "") + (type_ != null ? "type_=" + type_ + ", " : "") + (first_Page != null ? "first_Page=" + first_Page + ", " : "")
					+ (one_month != null ? "one_month=" + one_month + ", " : "") + (baidu_index != null ? "baidu_index=" + baidu_index + ", " : "") + (index_counts != null ? "index_counts=" + index_counts + ", " : "")
					+ (search_counts != null ? "search_counts=" + search_counts + ", " : "") + (id != null ? "id=" + id + ", " : "") + (days != null ? "days=" + days : "") + "]";
		}
		
	}
	public static void main(String[] args) 
	{
		System.out.println("Hello World!");
		String key="燕麦价格";
		System.out.println(key+":"+getBaiduPosid(key));
	}

}
