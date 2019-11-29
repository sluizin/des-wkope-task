package des.wangku.operate.standard.subengineering;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import des.wangku.operate.standard.swt.MultiTree.UnitClass;
import des.wangku.operate.standard.utls.UtilsDate;
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsPathFile;

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
	/**
	 * 从config目录中的cyw_industrycategory14.txt文件中提取14个大类的产业网关键字
	 * @return Map&lt;String,List&lt;String&gt;&gt;
	 */
	public static final Map<String,List<String>> getWK_IndustryGroup(){
		Map<String,List<String>> map=new HashMap<>();
		String filename = UtilsPathFile.getJarBasicPathconfig() + "/cyw_industrycategory14.txt";
		String content = UtilsFile.readFile(filename, "\n").toString();
		if(content.length()==0)return map;
		String[] a1=content.split("\n");
		for(String line:a1) {
			if(line.length()==0)continue;
			String[] a2=line.split(":");
			if(a2.length<2)continue;
			String key=a2[0];
			String val=a2[1];
			String[] a3=val.split(",");
			List<String> list=new ArrayList<>(a3.length);
			for(String e:a3) {
				String f=e.trim();
				if(f.length()==0)continue;
				list.add(f);
			}
			map.put(key, list);
		}		
		return map;
	}
	/**
	 * 把14个大类的产业网关键字组成Tree中的UnitClass数组。有等级
	 * @return List&lt;UnitClass&gt;
	 */
	public static final List<UnitClass> getWK_IndustryGroupListUnitClass() {
		List<UnitClass> listt=new ArrayList<>();
		Map<String,List<String>> map=getWK_IndustryGroup();
		String idcatHead="WKcywcat_";
		String idHead="WKcyw_";
		int i=1;
		for(Map.Entry<String, List<String>> entry :map.entrySet()) {
			String key=entry.getKey();
			List<String> list=entry.getValue();
			UnitClass parent=new UnitClass();
			String id=idcatHead+""+i;
			parent.setId(id);
			parent.setName(key);
			i++;
			for(String e:list) {
				UnitClass son=new UnitClass();
				son.setTargetid(id);
				son.setId(idHead+""+(i++));
				son.setName(e);
				parent.getList().add(son);
			}
			listt.add(parent);
		}
		return listt;
	}
}
