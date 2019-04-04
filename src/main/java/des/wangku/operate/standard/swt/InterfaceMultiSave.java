package des.wangku.operate.standard.swt;

import java.util.List;

import des.wangku.operate.standard.dialog.TableOutputExcelParaDialog.ExcelParaClass;

/**
 * 弹出菜单 - 导出<br>
 * 保存时是否需要另存其它副本
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public interface InterfaceMultiSave {
	/**
	 * 把ResultTable中选中的记录另存为 Excel
	 * @param table ResultTable
	 * @param epc ExcelParaClass
	 * @param list List&lt;List&lt;String&gt;&gt;
	 */
	public default void multiSaveExcel(ResultTable table,ExcelParaClass epc,List<List<String>> list) {
		
	}
	/**
	 * 把ResultTable中选中的记录另存为 Json
	 * @param table ResultTable
	 * @param list List&lt;List&lt;String&gt;&gt;
	 */
	public default void multiSaveJson(ResultTable table,List<List<String>> list) {
		
	}
	/**
	 * 把ResultTable中选中的记录另存到粘贴板
	 * @param table ResultTable
	 */
	public default void multiSaveClipboard(ResultTable table) {
		
	}
}
