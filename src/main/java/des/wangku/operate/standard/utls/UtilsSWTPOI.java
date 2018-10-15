package des.wangku.operate.standard.utls;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import des.wangku.operate.standard.dialog.TableOutputExcelParaDialog.ExcelParaClass;
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
	static Logger logger = Logger.getLogger(UtilsSWTPOI.class);

	/**
	 * 保存workbook到model目录里的随机数文件
	 * @param saveFolder String
	 * @param shell Shell
	 * @param workbook Workbook
	 * @param isAlert boolean
	 * @return File
	 */
	public static File save(String saveFolder, Shell shell, Workbook workbook, boolean isAlert) {
		//AbstractTask e=UtilsSWTTools.getParentObj(shell, AbstractTask.class);
		String proFolder = "";
		if (saveFolder != null) proFolder = saveFolder;
		File file = UtilsFile.mkModelRNDFile(proFolder, "xlsx");
		if (file == null) {
			if (isAlert) UtilsSWTMessageBox.Alert(shell, "文件生成失败");
			return null;
		}
		boolean t = UtilsSWTPOI.save(workbook, file);
		if (t) {
			String filename = UtilsFile.getFileName(file);
			if (isAlert) UtilsSWTMessageBox.Alert(shell, filename + "生成成功");
			return file;
		}
		if (isAlert) UtilsSWTMessageBox.Alert(shell, "文件生成失败");
		return null;
	}

	/**
	 * 保存Workbook到File，但关闭Workbook
	 * @param workbook Workbook
	 * @param file File
	 * @return boolean
	 */
	public static final boolean save(Workbook workbook, File file) {
		try {
			if (workbook == null || file == null) return false;
			FileOutputStream fileoutputStream = new FileOutputStream(file);
			workbook.write(fileoutputStream);
			fileoutputStream.close();
			workbook.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 把table放入 Workbook 中 默认名称 "信息"
	 * @param workbook Workbook
	 * @param table ResultTable
	 */
	public static final void addWorkbookSheet(Workbook workbook, ResultTable table) {
		String sheetName = ExcelCTabFolder.getSheetName(table, "信息");
		UtilsSWTPOI.addWorkbookSheet(workbook, sheetName, table);
	}

	/**
	 * 把table放入 Workbook 中
	 * @param workbook Workbook
	 * @param sheetName String
	 * @param table ResultTable
	 */
	public static final void addWorkbookSheet(Workbook workbook, String sheetName, ResultTable table) {
		InterfaceExcelChange change = UtilsSWTTools.getParentInterfaceObj(table, InterfaceExcelChange.class);
		TableItem[] arr = table.getItems();
		boolean isHead = table.getHeaderVisible();
		List<List<String>> list = UtilsSWTTable.getTableItemList(isHead, table, arr);
		UtilsSWTPOI.addWorkbookSheet(workbook, sheetName, list, change);
	}

	/**
	 * 把list放入 Workbook 中
	 * @param workbook Workbook
	 * @param sheetName String
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param change InterfaceExcelChange
	 */
	public static final void addWorkbookSheet(Workbook workbook, String sheetName, List<List<String>> list, InterfaceExcelChange change) {
		sheetName = (sheetName == null) ? "信息" : sheetName;
		Sheet sheet = workbook.createSheet(sheetName);
		CellStyle cellStyle = workbook.createCellStyle();
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
				String value = li.get(ii);
				if (value == null) value = "";
				if (value.length() > 30000) value = value.substring(0, 30000);
				Cell cell = row.createCell(ii);
				cell.setCellValue(value);
			}
		}
		if (change != null) change.afterWork(sheet);
	}

	/**
	 * @param filename String
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param sheetName String
	 * @param change InterfaceExcelChange
	 * @param epc ExcelParaClass
	 * @return boolean
	 */
	public static final boolean makeExcel(String filename, String sheetName, List<List<String>> list, InterfaceExcelChange change, ExcelParaClass epc) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("0");
		CellStyle cellStyle = workbook.createCellStyle();/* 设置这些样式 */
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
				String value = li.get(ii);
				if (value == null) value = "";
				if (value.length() > 30000) value = value.substring(0, 30000);
				Cell cell = row.createCell(ii);
				cell.setCellValue(value);
			}
		}
		if (change != null) change.afterWork(sheet);
		if (epc != null && (epc.getLockRows() > 0 || epc.getLockCols() > 0)) {/* 锁定某行列 */
			sheet.createFreezePane(epc.getLockCols(), epc.getLockRows());
		}
		workbook.setSheetName(0, sheetName == null ? "信息" : sheetName);
		return UtilsFile.writeWorkbookFile(filename, workbook);
	}

	/**
	 * 获取单元格各类型值，返回字符串类型<br>
	 * 如果为null，则返回 空串 ""
	 * @param cell Cell
	 * @return String
	 */
	public static String getCellValueByCell(Cell cell) {
		//判断是否为null或空串
		if (cell == null || cell.toString().trim().equals("")) return "";
		CellType cellType = cell.getCellTypeEnum();
		if (cellType == null) return "";
		String value = "";
		switch (cellType) {
		case STRING:
			value = cell.getStringCellValue();
			break;
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {  //判断日期类型
				/*
				 * Date ee = cell.getDateCellValue();
				 * return ee.toString();
				 * double dd = cell.getNumericCellValue();
				 * return String.valueOf(dd);
				 * return (new DecimalFormat("#.######").format(cell.getNumericCellValue()));
				 * cellValue = DateUtil.formatDateByFormat(cell.getDateCellValue(), "yyyy-MM-dd");
				 */
				value = convertCellToString(cell);
			} else {
				double dd = cell.getNumericCellValue();
				value = (decimalFormat.format(dd));/* return String.valueOf(dd); */
			}
			break;
		case BOOLEAN:
			boolean bb = cell.getBooleanCellValue();
			value = String.valueOf(bb);
			break;
		default:
			return "";
		}
		if (value == null) return "";
		return value.trim();
	}

	static final DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

	/**
	 * 分多种格式解析单元格的值
	 * @param cell 单元格
	 * @return 单元格的值
	 */
	public static final String convertCellToString(Cell cell) {
		/* 如果为null会抛出异常，应当返回空字符串 */
		if (cell == null) return "";
		/*
		 * POI对单元格日期处理很弱，没有针对的类型，日期类型取出来的也是一个double值，所以同样作为数值类型
		 * 解决日期2006/11/02格式读入后出错的问题，POI读取后变成“02-十一月-2006”格式
		 */
		if (cell.toString().contains("-") && checkDate(cell.toString())) {
			String ans = "";
			try {
				ans = new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
			} catch (Exception e) {
				ans = cell.toString();
			}
			return ans;
		}
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue();
	}

	/**
	 * 判断是否是“02-十一月-2006”格式的日期类型
	 * @param str String
	 * @return boolean
	 */
	public static boolean checkDate(String str) {
		String[] dataArr = str.split("-");
		if (dataArr.length != 3) return false;
		try {
			int x = Integer.parseInt(dataArr[0]);
			String y = dataArr[1];
			int z = Integer.parseInt(dataArr[2]);
			if (x > 0 && x < 32 && z > 0 && z < 10000 && y.endsWith("月")) return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
