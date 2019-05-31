package des.wangku.operate.standard.swt;

import java.io.File;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.Shell;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsFileMethod;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;
import des.wangku.operate.standard.utls.UtilsSWTPOI;

/**
 * 对本地excel进行二次修改
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class CustomExcel {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(CustomExcel.class);
	Shell shell = null;
	/** excel */
	Workbook workbook = null;
	/** 当前页 */
	Sheet sheet = null;
	/** 保存目录 */
	String saveFolder = "";

	public CustomExcel(AbstractTask parent, boolean isSubSource, String filename, int sheetnum) {
		this.shell = parent.getShell();
		saveFolder = parent.getIdentifierAll();
		String exectfilename = "";
		if (isSubSource) {/* 子资源 */
			exectfilename = parent.getSubSourceFile(filename);
		} else {
			exectfilename = parent.getBaseSourceFile("xlsx");
		}
		initWorkbook(exectfilename);
		if (workbook != null) sheet = workbook.getSheetAt(sheetnum);
	}

	/**
	 * 初始化Workbook
	 * @param filename String
	 */
	private void initWorkbook(String filename) {
		try {
			File file = new File(filename);
			workbook = new XSSFWorkbook(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置页数
	 * @param sheetnum int
	 */
	public void setSheetNum(int sheetnum) {
		if (workbook != null) sheet = workbook.getSheetAt(sheetnum);
	}

	/**
	 * 得到某行某列数据
	 * @param x int
	 * @param y int
	 * @return String
	 */
	public String getValue(int x, int y) {
		Cell cell = getCell(x, y);
		if (cell == null) return null;
		String value = UtilsSWTPOI.getCellValueByString(cell,false);
		return "" + value;
	}

	/**
	 * 设置某单元格内的值
	 * @param x int
	 * @param y int
	 * @param value RichTextString
	 */
	public void setValue(int x, int y, RichTextString value) {
		Cell cell = getCellCreate(x, y);
		cell.setCellValue(value);
	}

	/**
	 * 设置某单元格内的值
	 * @param x int
	 * @param y int
	 * @param value int
	 */
	public void setValue(int x, int y, int value) {
		Cell cell = getCellCreate(x, y);
		cell.setCellValue(value);
	}

	/**
	 * 设置某单元格内的值，如果超出，则截断
	 * @param x int
	 * @param y int
	 * @param value String
	 */
	public void setValue(int x, int y, String value) {
		Cell cell = getCellCreate(x, y);
		if (value.length() >= 32767) value = value.substring(0, 32766);
		cell.setCellValue(value);
	}

	/**
	 * 得到单元格，如果为null，则新加单元格
	 * @param x int
	 * @param y int
	 * @return Cell
	 */
	private Cell getCellCreate(int x, int y) {
		Cell cell = getCell(x, y);
		if (cell != null) return cell;
		return UtilsSWTPOI.createCell(sheet,x, y);
	}

	/**
	 * 得到excel某格
	 * @param x int
	 * @param y int
	 * @return Cell
	 */
	public Cell getCell(int x, int y) {
		if (sheet == null) return null;
		Row r = sheet.getRow(x);
		if (r == null) return null;
		r = sheet.getRow(x);
		return r.getCell(y);
	}
	public int getRowsLenth(int y) {
		if (sheet == null) return 0;
		return sheet.getPhysicalNumberOfRows();
	}

	/**
	 * 保存文件
	 */
	public void save() {
		save(saveFolder);
	}
	/**
	 * 保存文件 随机文件名
	 * @param saveFolder String
	 */
	public void save(String saveFolder) {
		save(saveFolder,null,true);
	}
	/**
	 * 保存文件 随机文件名
	 * @param isAlert boolean
	 */
	public void save(boolean isAlert) {
		save(saveFolder ,null,isAlert);
	}
	/**
	 * 保存文件 指定文件名
	 * @param filename String
	 * @param isAlert boolean
	 */
	public void save(String filename,boolean isAlert) {
		save(saveFolder ,filename,isAlert);
	}
	/**
	 * 关闭资源，注意，关闭这个UI时，excel自动保存
	 */
	public void close() {
		try {
			if(workbook!=null)workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void save(String saveFolder,String filename,boolean isAlert) {
		if (saveFolder == null) saveFolder = "";
		File file = UtilsFile.mkModelFile(saveFolder,filename, "xlsx");
			
		boolean t = UtilsSWTPOI.save(workbook, false, file);
		String filename2 = UtilsFileMethod.getFileName(file);
		if (t) {
			logger.debug("文件生成成功");
			if(isAlert)
			UtilsSWTMessageBox.Alert(shell, filename2 + "生成成功");
		} else {
			logger.debug("文件生成失败");
			if(isAlert)
			UtilsSWTMessageBox.Alert(shell, filename2 + "生成失败");
		}
		
	}

	public final Sheet getSheet() {
		return sheet;
	}

	public final Workbook getWorkbook() {
		return workbook;
	}
	
}
