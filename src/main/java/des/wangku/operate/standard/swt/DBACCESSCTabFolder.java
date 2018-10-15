package des.wangku.operate.standard.swt;

import java.sql.Connection;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsDBSource;
import des.wangku.operate.standard.utls.UtilsPathFile;
import des.wangku.operate.standard.utls.UtilsProperties;
import des.wangku.operate.standard.utls.UtilsSWTTools;

/**
 * access
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class DBACCESSCTabFolder extends AbstractDBCTabFolder {
	/** 日志 */
	static Logger logger = Logger.getLogger(DBACCESSCTabFolder.class);

	public DBACCESSCTabFolder(Composite parent, int style, String title) {
		super(parent, style | styleStandard, title);
	}

	public DBACCESSCTabFolder(Composite parent, int style, Properties properties, Connection conn) {
		super(parent, style, properties, conn);
	}

	public DBACCESSCTabFolder(Composite parent, int style, String title, Properties properties, Connection conn) {
		super(parent, style, title, properties, conn);
	}

	public DBACCESSCTabFolder(Composite parent, int style, String title, AbstractTask abstractTask) {
		super(parent, style, title, abstractTask.getProProperties(), getConnection(title, abstractTask));
	}

	private static final String ACC_DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";

	/**
	 * 得到 ACCESS Connection
	 * @param title String
	 * @param abstractTask AbstractTask
	 * @return Connection
	 */
	static final Connection getConnection(String title, AbstractTask abstractTask) {
		Properties properties = abstractTask.getProProperties();
		if (UtilsProperties.isExistProperties(properties, "jdbc.url.access" + title)) { return UtilsDBSource.getConnProperties(abstractTask, title); }
		String filepath = getModelAccessPath(abstractTask);
		//String driver="net.ucanaccess.jdbc.UcanaccessDriver";
		String url = "jdbc:ucanaccess://" + filepath + ";showschema=true;shutdown=true;memory=false";
		return UtilsDBSource.getConnProperties(ACC_DRIVER, url, "", "");
	}

	/**
	 * 获取model中的新建项目的accdb文件<br>
	 * D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope/build/libs/model/des-wkope-task-p000.accdb
	 * @param abstractTask AbstractTask
	 * @return String
	 */
	public static final String getModelAccessPath(AbstractTask abstractTask) {
		String filename = abstractTask.getNewModelFile("accdb");
		String filepath = UtilsPathFile.getModelJarBasicPath() + "/" + filename;
		return filepath;
	}

	@Override
	public String[] getAllTableName() {
		if (UtilsProperties.isExistProperties(properties, "jdbc.url.access" + title)) { return UtilsDBSource.getJackcessDatabaseTableAll(getProperties(), this.title); }
		AbstractTask t = UtilsSWTTools.getParentObjSuperclass(base, AbstractTask.class);
		if (t == null) {
			String[] arr = {};
			return arr;
		}
		return UtilsDBSource.getJackcessDatabaseTableAll(getModelAccessPath(t));

		/*
		 * String sql = "SELECT Name FROM MSysObjects WHERE Type=1 AND Name NOT LIKE 'Msys*'";
		 * return getAllTableNameSQL(sql);
		 */
	}

	@Override
	public String getDbtype() {
		return "access";
	}

}
