package des.wangku.operate.standard.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 修改poi中某个表格的状态
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceExcelChange {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(InterfaceExcelChange.class);

	/**
	 * 修改Cell内容状态 颜色，样式等内容
	 * @param x int
	 * @param y int
	 * @param cell Cell
	 * @param maxRows int
	 * @param maxCols int
	 */
	public void ChangeCell(int x, int y, Cell cell, int maxRows, int maxCols);

	/**
	 * 修改sheet信息,如设置宽度、高度等信息<br>
	 * 针对于sheet进行操作
	 * @param sheet Sheet
	 */
	public void ChangeSheet(Sheet sheet);

	/**
	 * 设置Sheet，遍历sheet里的所有单元，进行详细的后期设置
	 * @param sheet Sheet
	 */
	public default void afterWork(Sheet sheet) {
		ChangeSheet(sheet);
		int maxRows = sheet.getLastRowNum();
		int maxCols = 0;
		Row firstRow = sheet.getRow(0);
		if (firstRow == null) return;
		maxCols = firstRow.getLastCellNum();
		for (int i = 0; i <= maxRows; i++)
			for (int ii = 0; ii <= maxCols; ii++) {
				Cell cell = sheet.getRow(i).getCell(ii);
				if (cell != null) ChangeCell(i, ii, cell, maxRows, maxCols);
			}
	}


	public static final int ExcelColWdith = 12;
	public static final short ExcelRowHeight = 400;
}
