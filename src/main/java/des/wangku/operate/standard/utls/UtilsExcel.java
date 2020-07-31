package des.wangku.operate.standard.utls;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 针对excel直接操作
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public final class UtilsExcel {
	/**
	 *  sheet单元 用于添加
	 * 
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class SheetClass{		
		String sheetName="";
		List<List<String>> list;
		List<CellRangeAddress> regionsList=new ArrayList<>();
		public SheetClass(String sheetName,List<List<String>> list) {
			this.sheetName=sheetName;
			this.list=list;
		}
		public final String getSheetName() {
			return sheetName;
		}
		public final void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}
		public final List<List<String>> getList() {
			return list;
		}
		public final void setList(List<List<String>> list) {
			this.list = list;
		}
		public final List<CellRangeAddress> getRegionsList() {
			return regionsList;
		}
		public final void setRegionsList(List<CellRangeAddress> regionsList) {
			this.regionsList = regionsList;
		}
		public final CellRangeAddress[] getRegionsArr() {
			CellRangeAddress[] arr= {};
			return this.regionsList.toArray(arr);
		}
		
	}

	public static final void addWorkbookSheet(String filename, List<SheetClass> arrs) {
		try {
		File file = new File(filename);
		Workbook wb = new XSSFWorkbook();
		for(SheetClass ff:arrs) {
			UtilsPOI.addSheet(wb, ff.sheetName, ff.list,ff.getRegionsArr());
		}
		FileOutputStream fileoutputStream = new FileOutputStream(file);
		wb.write(fileoutputStream);
		fileoutputStream.close();
		wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
