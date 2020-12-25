package des.wangku.operate.standard.swt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.eclipse.swt.widgets.Composite;

import des.wangku.operate.standard.utls.UtilsProperties;
import des.wangku.operate.standard.utls.UtilsSQL;
import des.wangku.operate.standard.utls.UtilsVerification;

/**
 * 自定义mysql转成多页表格 UI组件<br>
 * 不接受联合主键的更新工作<br>
 * 暂不支持视图修改。但可以显示视图
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public abstract class AbstractDBCTabFolder extends AbstractCTabFolder {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AbstractDBCTabFolder.class);
	/** 数据库连接 */
	Connection conn = null;
	/** 最高等级sql语句 */
	String sql = "";
	/** sql配置 */
	List<DBPara> dbparaList = new ArrayList<>();

	protected void checkSubclass() {

	}

	public AbstractDBCTabFolder(Composite parent, int style, String title) {
		super(parent, style | styleStandard, title);
		checkSubclass();
		Init();
	}

	public AbstractDBCTabFolder(Composite parent, int style, Properties properties, Connection conn) {
		super(parent, style, properties);
		checkSubclass();
		this.conn = conn;
		Init();
	}

	public AbstractDBCTabFolder(Composite parent, int style, String title, Properties properties, Connection conn) {
		super(parent, style, title, properties);
		checkSubclass();
		this.conn = conn;
		Init();
	}

	private void Init() {
		makeCTabFolder();
		this.putPopMenu();
	}

	/**
	 * 得到库中所有表数组
	 * @return String[]
	 */
	public abstract String[] getAllTableName();

	/**
	 * 类型如:access mysql
	 * @return String
	 */
	public abstract String getDbtype();

	@Override
	public void makeCTabFolder() {
		if (conn == null) return;
		dbparaList = UtilsProperties.getPropValueList(getProperties(), DBPara.class, "DBCTF_" + title + "_TableArray");
		for (DBPara e : dbparaList) {
			e.abDB = this;
		}
		String[] arrs = getAllTableName();
		for (String tablename : arrs) {
			if (!getPropertiesTableAll() && !isExist(tablename)) continue;
			ResultTable t = this.addResultTable(tablename);
			makeCTabFolderSheet(tablename);
			t.initialization();
		}
	}

	/**
	 * 是否调出所有表，默认或为null返回true
	 * @return boolean
	 */
	boolean getPropertiesTableAll() {
		//logger.debug("key:" + "DBCTF_" + this.title + "_TableAll");
		return UtilsProperties.getProPropBoolean(getProperties(), "DBCTF_" + this.title + "_TableAll", true);
	}

	/**
	 * 读取到CTabFolder中
	 * @param dbname String
	 */
	void makeCTabFolderSheet(String dbname) {
		DBPara f = this.getDBPara(dbname);
		String sql = f.getSelectSQL();//"select "+f.getFieldsOutput()+" from " + dbname + " limit 100";
		/* 如果sql拼出为空，则返回所有行 */
		if (sql == null || sql.trim().length() == 0) sql = "select * from " + dbname;
		addDBResultTable(dbname, sql, null);
	}

	/**
	 * 添加记录，如果没有此页，则新建此页，如果有，则直接插入
	 * @param index int
	 * @param sql String
	 */
	public void addDBResultTable(int index, String sql) {
		String sheetname = this.getCTableTitle(index);
		if (sheetname == null) return;
		addDBResultTable(sheetname, sql, null);
	}

	/**
	 * 添加记录，如果没有此页，则新建此页，如果有，则直接插入
	 * @param sheetname String
	 * @param sql String
	 * @param jsonStr String
	 */
	public void addDBResultTable(String sheetname, String sql, String jsonStr) {
		if (sheetname == null || sql == null) return;
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//logger.debug("sql:" + sql);
			boolean isnew = false;
			ResultTable t = this.getResultTable(sheetname);
			if (t == null) {
				isnew = true;
				t = this.addResultTable(sheetname);
				if (jsonStr != null) t.setEctpara(jsonStr);
			}
			ResultSet rs = stmt.executeQuery(sql);
			mkExcelResultTableHead(t, rs);
			t.add(rs,true);
			rs.close();
			stmt.close();
			//logger.debug("isnew:" + isnew);
			if (isnew) t.initialization();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提取ResultTable，如果没有，则添加表格
	 * @param sheetname String
	 * @return ResultTable
	 */
	public ResultTable getResultTableNew(String sheetname) {
		ResultTable t = this.getResultTable(sheetname);
		if (t == null) t = this.addResultTable(sheetname);
		return t;
	}

	/**
	 * 提取某表的sql结构，如为null，则返回默认对象。直接使用默认值进行操作
	 * @param dbname String
	 * @return DBPara
	 */
	public DBPara getDBPara(String dbname) {
		if (dbname == null || dbname.length() == 0) return new DBPara();
		for (DBPara e : dbparaList)
			if (dbname.equals(e.dbname)) return e;
		return new DBPara();
	}

	void mkExcelResultTableHead(ResultTable t, ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] newArray = new String[count];
			t.additionalObj = rsmd;
			for (int i = 0; i < count; i++) {
				int p = i + 1;
				String name = rsmd.getColumnLabel(p);
				// logger.debug(name+":"+rsmd.getColumnTypeName(p));
				/*
				 * logger.debug(name+":"+rsmd.getColumnDisplaySize(p));
				 * logger.debug(name+":"+rsmd.getColumnType(p));
				 * logger.debug(name+":"+rsmd.getPrecision(p));
				 * logger.debug(name+":"+rsmd.getScale(p));
				 * logger.debug(name+":"+rsmd.getCatalogName(p));
				 * logger.debug(name+":"+rsmd.getSchemaName(p));
				 * logger.debug(name+":"+rsmd.getTableName(p));
				 * logger.debug(name+":"+rsmd.getColumnName(p));
				 * logger.debug(name+":"+rsmd.toString());
				 */
				newArray[i] = name;
			}
			t.mkResultTableHead(newArray);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn == null) return;
		try {
			if (!conn.isClosed()) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	boolean isExist(String dbname) {
		for (DBPara e : dbparaList) {
			if (dbname.equals(e.dbname)) return true;
		}
		return false;
	}

	/**
	 * 表的显示参数
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class DBPara {
		/** 具体数据库类型 */
		AbstractDBCTabFolder abDB = null;
		String dbname = null;
		/** 可直接使用sql语句 */
		String specialSQL = "";
		/** 显示列 */
		String fields = "";
		/** 限制条数 */
		int rows = -1;
		/** 条件 */
		String where = "";
		/** 排序字符串 */
		String orderby = "";
		int[] pkSuffix = { 0 };

		/**
		 * 得到select sql命令
		 * @return String
		 */
		public final String getSelectSQL() {
			if (abDB == null || (specialSQL != null && specialSQL.trim().length() > 0)) return specialSQL;
			StringBuilder sb = new StringBuilder(60);
			sb.append("select ");
			if ((!"mysql".equals(abDB.getDbtype())) && rows > 0) {
				sb.append(" top ");
				sb.append(rows);
				sb.append(" ");
			}
			sb.append(this.getFieldsOutput());
			sb.append(" from " + dbname);
			if (where != null && where.length() > 0) {
				sb.append(" where ");
				sb.append(where + " ");
			}
			if (orderby != null && orderby.length() > 0) {
				sb.append(" order by ");
				sb.append(orderby + " ");
			}
			if ("mysql".equals(abDB.getDbtype()) && rows > 0) {
				sb.append(" limit ");
				sb.append(rows);
			}
			logger.debug("sql:" + sb.toString());
			return sb.toString();
		}

		public final String getDbname() {
			return dbname;
		}

		public final void setDbname(String dbname) {
			this.dbname = dbname;
		}

		public final String getFields() {
			return fields;
		}

		public final String getFieldsOutput() {
			if (fields == null || fields.length() == 0) return "*";
			return fields;
		}

		public final void setFields(String fields) {
			this.fields = fields;
		}

		public final int getRows() {
			return rows;
		}

		public final void setRows(int rows) {
			this.rows = rows;
		}

		public final String getOrderby() {
			return orderby;
		}

		public final void setOrderby(String orderby) {
			this.orderby = orderby;
		}

		public final String getWhere() {
			return where;
		}

		public final void setWhere(String where) {
			this.where = where;
		}

		public final String getSpecialSQL() {
			return specialSQL;
		}

		public final void setSpecialSQL(String specialSQL) {
			this.specialSQL = specialSQL;
		}

		public final int[] getPkSuffix() {
			return pkSuffix;
		}

		public final void setPkSuffix(int[] pkSuffix) {
			this.pkSuffix = pkSuffix;
		}

	}

	@Override
	public boolean resultTableUpdate(ResultTable base, int x, int y, String oldValue, String newValue) {
		//logger.debug("[" + x + "," + y + "]:" + oldValue + "--" + newValue);
		if (base.additionalObj == null) return true;
		ResultSetMetaData rsmd = (ResultSetMetaData) base.additionalObj;
		DBPara f = this.getDBPara(base.title);
		if (f.pkSuffix.length == 0) return true;
		try {
			if (!UtilsVerification.isEffective(0, rsmd.getColumnCount() - 1, f.pkSuffix)) return true;
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			StringBuilder sb = new StringBuilder();
			sb.append("update ");
			//logger.debug("rsmd.getTableName(1):"+rsmd.getTableName(1));
			sb.append(rsmd.getTableName(1));
			sb.append(" set ");
			sb.append(rsmd.getColumnName(y + 1));
			sb.append("=");
			sb.append(UtilsSQL.getFieldString(rsmd, rsmd.getColumnName(y + 1), newValue));
			sb.append(" where ");
			sb.append(UtilsSQL.unknownfieldcombi(base, rsmd, " and ", x, f.pkSuffix));
			/*
			 * sb.append(UtilsSQL.unknownfieldcombi(AddWhere(base,rsmd," and ",x,f.pkSuffix));
			 * sb.append(rsmd.getColumnName(f.pkSuffix + 1));
			 * sb.append("=");
			 * sb.append(UtilsSWTTableSQL.get(base, x, f.pkSuffix));
			 */
			String sql = sb.toString();
			//logger.debug("update sql:" + sql);
			//PreparedStatement  ff=conn.prepareStatement(sql);
			//boolean tt=ff.execute();
			//logger.debug("tt:"+tt);
			int t = stmt.executeUpdate(sql);
			stmt.close();
			return t > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 从sql语句中提出所有表的数组
	 * @param sql String
	 * @return String[]
	 */
	protected String[] getAllTableNameSQL(String sql) {
		List<String> list = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement();
			logger.debug("getAllTableNameSQL sql :" + sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] arr = {};
		return list.toArray(arr);
	}

	/**
	 * 把sql转成list集数据，用于输入
	 * @param sql String
	 * @return List&lt;List&lt;String&gt;&gt;
	 */
	@Deprecated
	public List<List<String>> getDBTableInforList(String sql) {
		List<List<String>> llist = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				List<String> list = UtilsSQL.resultSetToList(rs);
				llist.add(list);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return llist;
	}
}
