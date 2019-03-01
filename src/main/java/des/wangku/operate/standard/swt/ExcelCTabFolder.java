package des.wangku.operate.standard.swt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsList;
import des.wangku.operate.standard.utls.UtilsPathFile;
import des.wangku.operate.standard.utls.UtilsSWTPOI;
import des.wangku.operate.standard.utls.UtilsSWTTableSQL;
import des.wangku.operate.standard.utls.UtilsSWTTools;

/**
 * 自定义excel转成多页表格 UI组件<br>
 * 请使用initialization()进行初始化
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class ExcelCTabFolder extends AbstractCTabFolder {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(ExcelCTabFolder.class);
	/** 父容器 */
	Composite parent;
	/** 样式 */
	int style = 0;
	/** 0:本地model目录(默认) 1:绝对地址 */
	int type = 0;
	/** excel文件 */
	String filename = null;
	/** excel */
	Workbook workbook = null;
	/** 是否多选 */
	boolean isRADIO = false;
	/** 读取时是否过滤空行 */
	boolean isFilterBlankLines = false;

	/**
	 * AbstractTask 项目组中的各项目自定义，配置信息与excel文件名相同 "des-wkope-task-XXXX.xlsx"<br>
	 * 请使用initialization()进行初始化
	 * @param parent Composite
	 * @param style int
	 */
	public ExcelCTabFolder(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
	}

	/**
	 * AbstractTask 项目组中的各项目自定义，配置信息与excel文件名相同 "des-wkope-task-XXXX.xlsx"<br>
	 * 请使用initialization()进行初始化
	 * @param parent Composite
	 * @param style int
	 * @param title String
	 */
	public ExcelCTabFolder(Composite parent, int style, String title) {
		super(parent, style, title);
		this.parent = parent;
	}

	/**
	 * 构造初始化
	 */
	public void initialization() {
		AbstractTask t = UtilsSWTTools.getParentObjSuperclass(parent, AbstractTask.class);
		if (t == null) return;
		this.properties = t.getProProperties();
		this.pc = t.getPc();
		if (filename == null) filename = AbstractTask.ACC_PROHead + t.getMenuNameHead().toLowerCase() + ".xlsx";
		init();
	}

	/**
	 * 直接录入配置信息<br>
	 * 请使用initialization()进行初始化
	 * @param parent Composite
	 * @param style int
	 * @param properties Properties
	 */
	public ExcelCTabFolder(Composite parent, int style, Properties properties) {
		super(parent, style, properties);
		this.parent = parent;
	}

	/**
	 * 初始化 type:0 本地model中的文件
	 */
	private void init() {
		String newString = filename;
		switch (type) {
		case 0:
			newString = UtilsPathFile.getModelJarBasicPath() + "/" + filename;
		default:
		}
		this.filename = newString;
		makeCTabFolder();
		this.putPopMenu();
		this.setSelection(0);
	}

	public void makeCTabFolder() {
		if (filename == null) return;
		logger.debug("提取excel文件:" + filename);
		try {
			File file = new File(filename);
			if(!file.exists()) {
				logger.debug("未发现excel文件:" + filename);
				return;
			}
			workbook = new XSSFWorkbook(file);
			int type = isRADIO ? ResultTable.ACC_ResultTableStateRadio : ResultTable.ACC_ResultTableState;
			for (int sheetnum = 0; sheetnum < workbook.getNumberOfSheets(); sheetnum++) {
				ResultTable t = new ResultTable(this, type, properties, workbook.getSheetName(sheetnum));
				makeCTabFolderFromExcel(this, t, workbook.getSheetAt(sheetnum));
				t.initialization();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.getChildren().length > 0) setSelection(0);
	}

	/**
	 * 把Sheet读取到CTabFolder中
	 * @param sheet Sheet
	 */
	void makeCTabFolderFromExcel(CTabFolder parent, ResultTable t, Sheet sheet) {
		String sheetname = sheet.getSheetName();
		CTabItem tbtmExcel = new CTabItem(parent, SWT.NONE);
		tbtmExcel.setText(sheetname);
		tbtmExcel.setControl(t);

		final int rowslen = sheet.getLastRowNum();/* 行数 */
		mkExcelResultTableHead(t, sheet);
		int cellLen = t.getColumnCount();
		List<String> list = new ArrayList<>(cellLen);
		for (int i = 0; i <= rowslen; i++) {
			if (i == t.getEctpara().headRowSuffix) continue;
			Row r = sheet.getRow(i);
			if (r == null) continue;
			list.clear();
			for (int ii = 0; ii < cellLen; ii++) {
				Cell cell = r.getCell(ii);
				String value = UtilsSWTPOI.getCellValueByString(cell, true);
				if (t.getEctpara().isTrim) value = value.trim();
				list.add(value);
			}
			if (!(isFilterBlankLines && UtilsList.isBlankLines(list))) {
				UtilsSWTTableSQL.add(t, list);
			}
		}
	}

	/**
	 * 设置Excel ResultTable头部信息
	 * @param t ResultTable
	 * @param sheet Sheet
	 */
	void mkExcelResultTableHead(ResultTable t, Sheet sheet) {
		final int rowslen = sheet.getPhysicalNumberOfRows();//sheet.getLastRowNum();/* 行数 */
		if (rowslen == 0) return;
		boolean isBadSuffix = false;
		isBadSuffix = t.getEctpara().headRowSuffix < 0 || t.getEctpara().headRowSuffix >= rowslen;
		Row first = isBadSuffix ? sheet.getRow(0) : sheet.getRow(t.getEctpara().headRowSuffix);
		int cellLen = first.getLastCellNum();/* 列数 */
		String value = null;
		String[] newArray = new String[cellLen];
		for (int i = 0; i < cellLen; i++) {
			if (!isBadSuffix) value = UtilsSWTPOI.getCellValueByString(first.getCell(i), true);
			else value = "" + i;
			newArray[i] = value;
		}
		t.mkResultTableHead(newArray);
	}

	@Override
	public void close() {
		try {
			if (workbook != null) workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean resultTableUpdate(ResultTable base, int x, int y, String oldValue, String newValue) {
		return true;
	}

	public final boolean isRADIO() {
		return isRADIO;
	}

	public final void setRADIO(boolean isRADIO) {
		this.isRADIO = isRADIO;
	}

	public final boolean isFilterBlankLines() {
		return isFilterBlankLines;
	}

	/**
	 * 是否过滤掉空行
	 * @param isFilterBlankLines boolean
	 */
	public final void setFilterBlankLines(boolean isFilterBlankLines) {
		this.isFilterBlankLines = isFilterBlankLines;
	}

	public final int getType() {
		return type;
	}

	/**
	 * 设置excel文件是否在model目录，还是绝对地址。默认为0:model目录
	 * @param type int
	 */
	public final void setType(int type) {
		this.type = type;
	}

	public final String getFilename() {
		return filename;
	}

	public final void setFilename(String filename) {
		this.filename = filename;
	}

}
