package des.wangku.operate.standard.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsSWTPOI;

public class Testing {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			String filename="G:/文件夹/P07/产业网类目分类.xlsx";
			File file = new File(filename);
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheetAt(0);
			final int rowslen = sheet.getLastRowNum();/* 行数 */
			List<Unit> list=new ArrayList<>();
			for (int i = 1; i <= rowslen; i++) {
				Row row = sheet.getRow(i);
				String name = UtilsSWTPOI.getCellValueByString(row.getCell(1), true);
				String url = UtilsSWTPOI.getCellValueByString(row.getCell(2), true);
				String key = UtilsSWTPOI.getCellValueByString(row.getCell(3), true);
				if(name==null || name.length()==0)continue;
				//System.out.println(name+"\t"+url);
				list.add(new Unit(name,url,key));			
			}
			String filetext="G:/a1.txt";
			String content=UtilsFile.readFile(filetext, "\n").toString();
			//System.out.println("content:"+content);
			String[] arr=content.split("\n");
			loop:for(String e:arr) {
				if(e==null || e.length()==0) {
					System.out.println(e+"\t");
				}
				for(Unit f:list) {
					if(f.key.equals(e)) {
						System.out.println(e+"\t"+f.url);
						continue loop;
					}
				}
				System.out.println(e+"\t");
			}
			
			
			
		}catch (Exception ee) {
			ee.printStackTrace();
		}
	}
	static class Unit{
		String name;
		String url;
		String key;
		public Unit(String name,String url,String key) {
			this.name=name;
			this.url=url;
			this.key=key;
		}
	}
}
