package des.wangku.operate.standard.desktop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
/**
 * 
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MainDesktopComposite {
	/**
	 * 重置容器
	 * @param shell Shell
	 * @param compositeMain Composite
	 * @param width int
	 * @param height int
	 */
	public static final void resetMainComposite(Shell shell,Composite compositeMain,int width,int height) {
		compositeMain = new Composite(shell, SWT.NONE);
		compositeMain.setLayout(new FillLayout());
		compositeMain.redraw(0, 0, width, height, true);
	}

	/**
	 * 设置完容器后刷新
	 * @param compositeMain Composite
	 * @param width int
	 * @param height int
	 */
	public static final void repaintMainComposite(Composite compositeMain,int width,int height) {
		compositeMain.setLayout(new FillLayout());
		compositeMain.setBounds(0, 0, width, height);
	}
}
