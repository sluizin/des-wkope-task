package des.wangku.operate.standard.dialog;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import des.wangku.operate.standard.utls.UtilsDialogState;

/**
 * 多种不同的值
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class InputMultiValueDialog extends Dialog {
	/** 日志 */
	static Logger logger = Logger.getLogger(InputMultiValueDialog.class);
	protected Shell shell;

	public InputMultiValueDialog(Shell parent, int style) {
		super(parent, style);
		this.shell = new Shell(parent);
		shell.setSize(233, 220);
		UtilsDialogState.changeDialogCenter(parent, this.shell);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	/**
	 * 参数
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class InputParameter {
		String title = "";
		int type = 0;
		String value = "";

		public InputParameter(String title, int type, String value) {
			this.title = title;
			this.type = type;
			this.value = value;
		}
	}
}
