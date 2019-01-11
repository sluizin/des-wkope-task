package des.wangku.operate.standard.subengineering;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import des.wangku.operate.standard.utls.UtilsDate;

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
	 * @param conn Connection
	 * @param site_id String
	 * @param date1 String
	 * @param date2 String
	 * @return int
	 */
	public static final int getSite_idInforCount(Connection conn, String site_id, String date1, String date2) {
		int sort = -1;
		if (conn == null) return sort;
		String sql = "select count(*) from article where site_id=" + site_id + " and (datediff(add_time,'" + date1 + " 00:00:00')>=0 and datediff('" + date2 + " 23:59:59',add_time)>=0)";
		//System.out.println("" + sql);
		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(sql);) {
			if (rs.next()) return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sort;
	}

	/**
	 * 2018-12 转成 2018-12-01 | 2018-12-31<br>
	 * 2018-12-15 转成 2018-12-15 | 2018-12-15<br>
	 * 2018-12:2018-12 转成 2018-12-01 | 2018-12-31<br>
	 * 2018-12-15:2018-12 转成 2018-12-15 | 2018-12-31<br>
	 * 2018-12:2018-12-15 转成 2018-12-01 | 2018-12-15<br>
	 * @param conn Connection
	 * @param site_id String
	 * @param dateStr String
	 * @return int
	 */
	public static final int getSite_idInforCount(Connection conn, String site_id, String dateStr) {
		String[] paraArray = UtilsDate.getDateArray(dateStr);
		if (paraArray == null) return -1;
		return getSite_idInforCount(conn, site_id, paraArray[0], paraArray[1]);
	}

}
