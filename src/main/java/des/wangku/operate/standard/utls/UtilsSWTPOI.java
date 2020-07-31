package des.wangku.operate.standard.utls;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.TableItem;

import des.wangku.operate.standard.dialog.TableOutputExcelParaDialog.ExcelParaClass;
import des.wangku.operate.standard.swt.AbstractCTabFolder;
import des.wangku.operate.standard.swt.ExcelCTabFolder;
import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.task.InterfaceExcelChange;

/**
 * POI工具方法集
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTPOI {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsSWTPOI.class);


	/**
	 * 把table放入 Workbook 中 默认名称 "信息"
	 * @param wb Workbook
	 * @param table ResultTable
	 */
	public static final void addWorkbookSheet(Workbook wb, ResultTable table) {
		String sheetName = ExcelCTabFolder.getSheetName(table, "信息");
		UtilsSWTPOI.addWorkbookSheet(wb, sheetName, table);
	}

	/**
	 * 把多个table放入 Workbook 中
	 * @param wb Workbook
	 * @param sheetName String
	 * @param arrs ResultTable[]
	 */
	public static final void addWorkbookSheet(Workbook wb, String sheetName, ResultTable... arrs) {
		if (arrs.length == 0) return;
		ResultTable first = arrs[0];
		InterfaceExcelChange change = UtilsSWTTools.getParentInterfaceObj(first, InterfaceExcelChange.class);
		boolean isHead = first.getHeaderVisible();
		List<List<String>> list = new ArrayList<>();
		for (int i = 0; i < arrs.length; i++) {
			ResultTable table = arrs[i];
			String sheetName2 = AbstractCTabFolder.getSheetName(table, "信息" + i);
			TableItem[] arr = table.getItems();
			List<List<String>> list1 = UtilsSWTTable.getTableItemList(i == 0 ? isHead : false, arrs.length > 1 ? sheetName2 : null, table, arr);
			list.addAll(list1);
		}
		UtilsSWTPOI.addWorkbookSheet(wb, sheetName, list, change);
	}

	/**
	 * 把list放入 Workbook 中
	 * @param wb Workbook
	 * @param sheetName String
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param change InterfaceExcelChange
	 */
	public static final void addWorkbookSheet(Workbook wb, String sheetName, List<List<String>> list, InterfaceExcelChange change) {
		String newSheetName = UtilsPOI.getNewSheetNameDefault(wb, sheetName);
		Sheet sheet = wb.createSheet(newSheetName);
		CellStyle cellStyle = wb.createCellStyle();
		// 设置这些样式  
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		int maxCols = 0;
		for (int i = 0, len = list.size(); i < len; i++) {
			List<String> li = list.get(i);
			if (li.size() > maxCols) maxCols = li.size();
		}
		for (int i = 0, len = list.size(); i < len; i++) {
			List<String> li = list.get(i);
			Row row = sheet.createRow(i);
			for (int ii = 0, len2 = li.size(); ii < len2; ii++) {
				row.createCell(ii).setCellStyle(cellStyle);
				String value = UtilsPOI.arrangementString(li.get(ii));
				Cell cell = row.createCell(ii);
				cell.setCellValue(value);
			}
		}
		if (change != null) change.afterWork(sheet);
	}



	/**
	 * 生成excel文件
	 * @param filename String
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param sheetName String
	 * @param change InterfaceExcelChange
	 * @param epc ExcelParaClass
	 * @return Sheet
	 */
	public static final Sheet makeExcel(String filename, String sheetName, List<List<String>> list, InterfaceExcelChange change, ExcelParaClass epc) {
		Workbook wb = new XSSFWorkbook();
		String newSheetName = UtilsPOI.getNewSheetNameDefault(wb, sheetName);
		Sheet sheet = wb.createSheet(newSheetName);
		makeExcel(sheet, list, change, epc);
		boolean t = UtilsFile.writeWorkbookFile(filename, wb, false);
		if (t) return sheet;
		try {
			if (wb != null) wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成excel文件 修改sheet内容
	 * @param sheet Sheet
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param change InterfaceExcelChange
	 * @param epc ExcelParaClass
	 */
	public static final void makeExcel(Sheet sheet, List<List<String>> list, InterfaceExcelChange change, ExcelParaClass epc) {
		if (sheet == null) return;
		CellStyle cellStyle = UtilsPOI.getCellStyleBase(sheet.getWorkbook());
		int maxCols = 0;
		for (int i = 0, len = list.size(); i < len; i++) {
			List<String> li = list.get(i);
			if (li.size() > maxCols) maxCols = li.size();
		}
		for (int i = 0, len = list.size(); i < len; i++) {
			List<String> li = list.get(i);
			Row row = sheet.createRow(i);
			for (int ii = 0, len2 = li.size(); ii < len2; ii++) {
				row.createCell(ii).setCellStyle(cellStyle);
				String value = li.get(ii);
				Cell cell = row.createCell(ii);
				cell.setCellValue(UtilsPOI.arrangementString(value));
				if (epc != null) epc.makeStyle(cell);
			}
		}
		if (change != null) change.afterWork(sheet);
		if (epc != null) epc.makeStyle(sheet);
	}

	/**
	 * ResultSet生成excel
	 * @param filename String
	 * @param sheetName String
	 * @param conn Connection
	 * @param sql String
	 * @param isMetaData boolean
	 * @return boolean
	 */
	public static final boolean makeExcel(String filename, String sheetName, Connection conn, String sql, boolean isMetaData) {
		if (conn == null || sql == null) return false;
		try (Statement statement = conn.createStatement(); ResultSet rs = statement.executeQuery(sql);) {
			return makeExcel(filename, sheetName, rs, isMetaData);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ResultSet生成excel
	 * @param filename String
	 * @param sheetName String
	 * @param rs ResultSet
	 * @param isMetaData boolean
	 * @return boolean
	 */
	public static final boolean makeExcel(String filename, String sheetName, ResultSet rs, boolean isMetaData) {
		if (rs == null) return false;
		try {
			Workbook wb = new XSSFWorkbook();
			String newSheetName = UtilsPOI.getNewSheetNameDefault(wb, sheetName);
			Sheet sheet = wb.createSheet(newSheetName);
			//wb.setSheetName(0, sheetName == null ? "信息" : sheetName);
			CellStyle cellStyle = UtilsPOI.getCellStyleBase(wb);
			int p = 0;
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			if (isMetaData) {
				Row row = sheet.createRow(p++);
				for (int i = 0; i < count; i++) {
					String value = rsmd.getColumnName(i + 1);
					row.createCell(i).setCellStyle(cellStyle);
					Cell cell = row.createCell(i);
					cell.setCellValue(UtilsPOI.arrangementString(value));
				}
			}
			rs.beforeFirst();
			while (rs.next()) {
				Row row = sheet.createRow(p++);
				for (int i = 0; i < count; i++) {
					Object obj = rs.getObject(i + 1);
					String value = (obj == null ? "" : obj.toString());
					//logger.debug("value:"+value);
					row.createCell(i).setCellStyle(cellStyle);
					Cell cell = row.createCell(i);
					cell.setCellValue(UtilsPOI.arrangementString(value));
				}
			}
			logger.debug("filename:" + filename);
			return UtilsFile.writeWorkbookFile(filename, wb);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}










}
