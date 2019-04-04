package des.wangku.operate.standard.subengineering;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import des.wangku.operate.standard.utls.UtilsRegular;

public class WKInfoTestingMain {
	
	public static final Connection getconn() {
		Connection conn = null;
		try {
				String driver = "com.mysql.jdbc.Driver";
				String url = "jdbc:mysql://192.168.6.146:3306/wk_info?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT&autoReconnect=true";
				String dbName = "rostat";
				String password = "rostat-0409";
				Class.forName(driver);
				conn = DriverManager.getConnection(url, dbName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	public static void main(String[] args) {
		Connection conn=getconn();
		if(conn==null)return;
		test1(conn);
	}
	public static final void test1(Connection conn) {
			String sql = "select b.article_detail from article a,article_detail b where a.id=b.article_id and  a.site_id=1532 and a.title like '%猕猴桃%';";
			for(int i=1;i<=7;i++) {
				int sort=0;
				try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(sql);) {
					while (rs.next()) {
						String content=rs.getString(1);
						int ci=UtilsRegular.getPatternNumDisCount(content,"猕猴桃");
						if(ci>=i)sort++;
						//return rs.getInt(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(i+"\tsort:"+sort);
			}
		
		
	}

}
