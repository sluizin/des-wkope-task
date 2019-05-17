package des.wangku.operate.standard.task;

import des.wangku.operate.standard.dialog.AbstractSearch;
//import des.wangku.operate.standard.dialog.SearchDialog;

/**
 * 表格排序
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public interface InterfaceTablesDialog {
	/**
	 * 设置弹出查找框
	 * @param e AbstractSearch
	 */
	public void setSearchDialog(AbstractSearch e);
	/**
	 * 返回table里的面的table查找框
	 * @return AbstractSearch
	 */
	public AbstractSearch getSearchDialog();
	/**
	 * 设置弹出修改框
	 * @param e AbstractTablesEditDialog
	 */
	public void setEditDialog(AbstractTablesEditDialog e);
	/**
	 * 返回table里的面的table修改框
	 * @return AbstractTablesEditDialog
	 */
	public AbstractTablesEditDialog getEditDialog();
	
}
