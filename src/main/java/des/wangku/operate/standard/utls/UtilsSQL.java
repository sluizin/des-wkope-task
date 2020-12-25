package des.wangku.operate.standard.utls;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import des.wangku.operate.standard.swt.ResultTable;

/**
 * 针对sql的扩展工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSQL {
	/**
	 * 把不同多个字段组合成 a=b and c='d'形式
	 * @param base ResultTable
	 * @param rsmd ResultSetMetaData
	 * @param middle String
	 * @param x int
	 * @param pksuffix int[]
	 * @return String
	 */
	public static final String unknownfieldcombi(ResultTable base, ResultSetMetaData rsmd, String middle, int x, int... pksuffix) {
		StringBuilder sb = new StringBuilder();
		try {
			for (int p : pksuffix) {
				if (p < 0 || p >= rsmd.getColumnCount()) continue;
				if (sb.length() > 0) sb.append(middle);
				sb.append(rsmd.getColumnName(p + 1));
				sb.append("=");
				String newValue = base.get(x, p);
				sb.append(UtilsSQL.getFieldString(rsmd, rsmd.getColumnName(p + 1), newValue));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();

	}

	/**
	 * 把value以按照列名title查找，如果查到为非数值时，则返回 'XXXX'
	 * @param rsmd ResultSetMetaData
	 * @param title String
	 * @param value String
	 * @return String
	 */
	public static final String getFieldString(ResultSetMetaData rsmd, String title, String value) {
		if (value == null) return "";
		int suffix = UtilsSQL.getFieldSuffix(rsmd, title);
		if (suffix == -1) return "";
		if (!UtilsSQL.isFieldNum(rsmd, suffix)) return "'" + value + "'";
		return value;
	}

	/**
	 * 按照列类查找位置，如非法，或没找到，则返回-1;返回值在1-N之间与ResultSetMetaData相同
	 * @param rsmd ResultSetMetaData
	 * @param title String
	 * @return int
	 */
	public static final int getFieldSuffix(ResultSetMetaData rsmd, String title) {
		if (rsmd == null || title == null || title.length() == 0) return -1;
		try {
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				if (rsmd.getColumnName(i + 1).equals(title)) return i + 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 从ResultSetMetaData中判断某列是否在数值型，用于组建sql时是否加''
	 * @param rsmd ResultSetMetaData
	 * @param suffix int
	 * @return boolean
	 */
	public static final boolean isFieldNum(ResultSetMetaData rsmd, int suffix) {
		try {
			String title = rsmd.getColumnTypeName(suffix);
			return isFieldNum(title);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static final String[] ColumnTypeNameNumArrs = { "INT", "TINYINT", "DECIMAL", "DOUBLE", "FLOAT", "BIT", "MEDIUMINT", "SMALLINT" };

	/**
	 * 判断ResultSetMetaData中的getColumnTypeName是否是数值型数据
	 * @param columnTypeName String
	 * @return boolean
	 */
	public static final boolean isFieldNum(String columnTypeName) {
		if (columnTypeName == null || columnTypeName.length() == 0) return false;
		for (String e : ColumnTypeNameNumArrs) {
			if (e.equals(columnTypeName)) return true;
		}
		return false;
	}

	/**
	 * 把rs转成list
	 * @param rs ResultSet
	 * @return List&lt;String&gt;
	 */
	public static final List<String> resultSetToList(ResultSet rs) {
		List<String> list = new ArrayList<>();
		try {
		int len = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= len; i++) {
			Object obj = rs.getObject(i);
			String value = "";
			if (obj != null) value = obj.toString();
			list.add(value);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 把rs转成多个list
	 * @param rs ResultSet
	 * @return List&lt;List&lt;String&gt;&gt;
	 */
	public static final List<List<String>> resultSetToMulitList(ResultSet rs) {
		List<List<String>> list = new ArrayList<>();
		if(rs==null)return list;
		try {
			while (rs.next()) {
				List<String> line=resultSetToList(rs);
				if(list.size()>0)list.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}
