package des.wangku.operate.wk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Consts {

	public static class LinkMySQL {

		String sDBDriver = "org.gjt.mm.mysql.Driver";
		String sql_Url = "192.168.6.146";
		String sql_DBName = "wk_business_shard1";
		String sql_Userid = "rostat";
		String sql_Password = "rostat-0409";
		Connection connect = null;

		public Connection dbconn() {
			StringBuilder Url = new StringBuilder();
			try {
				//Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				Class.forName(sDBDriver).newInstance();
				// 连接字符串，格式： "jdbc:数据库驱动名称://数据库服务器ip/数据库名称?user=用户名&password=密码&使用Unicode=布尔值&字符编码=编码"
				Url.append("jdbc:mysql://");
				Url.append(sql_Url);
				Url.append("/");
				Url.append(sql_DBName);
				Url.append("?user=");
				Url.append(sql_Userid);
				Url.append("&password=");
				Url.append(sql_Password);
				Url.append("&useUnicode=true&characterEncoding=utf8");
				connect = DriverManager.getConnection(Url.toString(), sql_Userid, sql_Password);
				return connect;
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			return null;
		}

		public void dbclose() throws Exception {
			try {
				if (connect != null) connect.close();
			} catch (Exception ex) {
				System.out.println("closeConn: " + ex);
			}
		}
	}

	public static class LinkAccess {
		String sDBDriver ="com.hxtt.sql.access.AccessDriver";// "sun.jdbc.odbc.JdbcOdbcDriver";
		String sql_Url = "g:/bf/Summary99114data/Summary99114data.mdb";
		String sql_Userid = "";
		String sql_Password = "";
		Connection connect = null;

		public Statement dbconn() {
			try {
				//Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				Class.forName(sDBDriver).newInstance();//.newInstance();
				Properties prop = new Properties();    //只要添加这几句话就可以  
				prop.put("charSet", "gb2312");
				//String ur1 = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + sql_Url;
				String ur1 = "jdbc:Access:///" + sql_Url;
				connect = DriverManager.getConnection(ur1, sql_Userid,sql_Password);
				Statement stmt = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				return stmt;
			} catch (Exception ex) {

	    	    ex.printStackTrace();
				System.out.println(ex.getMessage());
			}
			return null;
		}

		public void dbclose() throws Exception {
			try {
				if (connect != null) connect.close();
			} catch (Exception ex) {
				System.out.println("closeConn: " + ex);
			}
		}
	}
	
	
	
	

	static final String makeSQL(String[] array,String baseSQL){
		StringBuilder sb=new StringBuilder(100);
		for(int i=0,len=array.length;i<len;i++){
			String key=array[i];
			sb.append(baseSQL.replaceAll("key", key));
			if(i<len-1)sb.append(" or ");
		}
		return sb.toString();
	}
	@SuppressWarnings("unused")
	static final void viewColumnCount(final Connection connectAccess,final Connection connect, final String sql,final String Tablename) {
		try (Statement stmt = connect.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(sql);) {
			List<String> linehead = new ArrayList<String>();
			int maxColumn=rs.getMetaData().getColumnCount();
			for(int iii=1;iii<=maxColumn;iii++)linehead.add("a"+iii);
			{
				ResultSetMetaData data = rs.getMetaData();
				List<String> line = new ArrayList<String>();
				for(int iii=1;iii<=maxColumn;iii++)line.add(data.getColumnLabel(iii));				
				insert(connectAccess,makeSQL(Tablename,linehead,line));
			}
			int i=0,member_id;
			loop:while (rs.next()) {
				i++;
				System.out.println("count:"+i);
				//member_id = rs.getInt(1);
				//String supply_industry_code = rs.getString(6);		
				
				//if(!inPartCodeAll(supply_industry_code)){
					List<String> line = new ArrayList<String>();
					//addtime = rs.getString(3);
					//line.add(""+addtime.substring(0,addtime.indexOf('.') ));
					//line.add(""+member_id);
					for(int iii=1;iii<=maxColumn;iii++)
						{
/*						String email=rs.getObject(1).toString();
						System.out.println("email:"+email);
						if(!Check.isEmail2(email)){
							continue loop;
						}*/
						line.add(""+rs.getObject(iii));
						}
					//line.add(""+rs.getString(2));
					//line.add(""+rs.getString(3));
					//line.add(""+rs.getString(4));
					//line.add(""+rs.getString(5));
					//line.add(""+rs.getInt(10));
					insert(connectAccess,makeSQL(Tablename,linehead,line));
					
				//}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static final boolean insert(final Connection connectAccess, final String sql) {
		if(connectAccess==null)return false;
		if(sql==null)return false;
		try (Statement stmt = connectAccess.createStatement();) {
			stmt.execute(sql);
			return true;
		} catch (SQLException e) {
			System.out.println("sqlerror:"+sql);
			//e.printStackTrace();
		}
		return false;
	}

	static final String makeSQL(final String Tablename, final List<String> linehead, final List<String> line) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into " + Tablename + " (");
		int lenhead = linehead.size();
		int lenline = line.size();
		for (int i = 0; i < lenhead; i++) {
			sb.append(linehead.get(i));
			if (i < lenhead - 1) sb.append(',');
		}
		sb.append(")values(");
		String title="";
		for (int i = 0; i < lenhead; i++) {
			if (i < lenline) {
				title = line.get(i);
				if(title == null || title.length()==0 || title.equals("null")){
					sb.append("''");
					
				}else{
					sb.append("'" + line.get(i).replace("'", "''") + "'");
				}
			} else {
				sb.append("''");
			}
			if (i < lenhead - 1) sb.append(',');
		}
		sb.append(")");
		System.out.println ("sb:"+sb.toString());
		return sb.toString();
	}
	
	
	
	
	
	
	
	
}
