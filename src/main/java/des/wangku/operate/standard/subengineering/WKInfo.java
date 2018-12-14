package des.wangku.operate.standard.subengineering;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 网库新闻数量
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class WKInfo {
	/**
	 * 得到某个产业网，在某段时间段内的资讯数量<br>
	 * 如 date1:2018-12-1 date2:2018-12-10 代表这段时间段<br>
	 * 如果只找当天，则两个日期相同即可
	 * @param conn
	 * @param site_id
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static final int getSite_idInforCount(Connection conn, String site_id, String date1, String date2) {
		int sort = 0;
		if (conn == null) return sort;
		String sql = "select count(*) from article where site_id=" + site_id + " and (datediff(add_time,'" + date1 + " 00:00:00')>=0 and datediff('" + date2 + " 23:59:59',add_time)>=0)";
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				sort = rs.getInt(1);
				break;
			}
			rs.close();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sort;
	}
}
