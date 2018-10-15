package des.wangku.operate.standard.task;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;


public abstract class AbstractTablesEditDialog extends Dialog{
	/** 日志 */
	static Logger logger = Logger.getLogger(AbstractTablesEditDialog.class);
	protected Shell parent;
	protected Shell shell = null;
	protected TableItem tableItem;

	public AbstractTablesEditDialog(Shell parent, int style, TableItem tableItem) {
		super(parent, style);
		this.parent = parent;
		shell = new Shell(parent);
		this.tableItem=tableItem;
	}
	/**
	 * 关闭
	 */
	public void close() {
		if (shell != null) shell.close();
	}
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public abstract Object open() ;
}
