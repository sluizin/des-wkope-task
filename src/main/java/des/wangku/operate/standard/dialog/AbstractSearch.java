package des.wangku.operate.standard.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import des.wangku.operate.standard.utls.UtilsDialogState;

/**
 * 搜索弹出窗抽象类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public abstract class AbstractSearch extends Dialog {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AbstractSearch.class);

	/** 返回对象 */
	protected Object returnObj = null;
	/** 检索关键字 */
	protected String textValue = "";

	/**
	 * 构造函数
	 * @param parent Shell
	 * @param style int
	 */
	public AbstractSearch(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @param parentShell Shell
	 * @param shell Shell
	 * @return Object
	 */
	public Object open(Shell parentShell, Shell shell) {
		createContents(parentShell, shell);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return getReturn();
	}

	/**
	 * 打开
	 * @return Object
	 */
	public abstract Object open();

	/**
	 * 返回对象
	 * @return Object
	 */
	public abstract Object getReturn();

	/**
	 * 设置对象值
	 */
	public abstract void setReturn();

	/**
	 * Create contents of the dialog.
	 * @param parentShell Shell
	 * @param shell Shell
	 */
	static final void createContents(Shell parentShell, Shell shell) {
		UtilsDialogState.changeDialogCenter(parentShell, shell);

	}
}
