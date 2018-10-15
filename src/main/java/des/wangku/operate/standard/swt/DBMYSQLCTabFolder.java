package des.wangku.operate.standard.swt;

import java.sql.Connection;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

/**
 * MYSQL
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class DBMYSQLCTabFolder extends AbstractDBCTabFolder {
	/** 日志 */
	static Logger logger = Logger.getLogger(DBMYSQLCTabFolder.class);

	public DBMYSQLCTabFolder(Composite parent, int style, String title) {
		super(parent, style | styleStandard, title);
	}

	public DBMYSQLCTabFolder(Composite parent, int style, Properties properties, Connection conn) {
		super(parent, style, properties, conn);
	}

	public DBMYSQLCTabFolder(Composite parent, int style, String title, Properties properties, Connection conn) {
		super(parent, style, title, properties, conn);
	}

	@Override
	public String[] getAllTableName() {
		String sql = "select TABLE_NAME from information_schema.`TABLES` t WHERE t.TABLE_SCHEMA = (select database())";
		return getAllTableNameSQL(sql);
	}

	@Override
	public String getDbtype() {
		return "mysql";
	}
}
