package des.wangku.operate.standard.utls;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


/**
 * 针对MENU进行操作的工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTMenu {
	/** 日志 */
	static Logger logger = Logger.getLogger(UtilsSWTMenu.class);

	/**
	 * 添加退出模块的菜单
	 * @param parentComposite Composite
	 * @param parent Menu
	 */
	public static void addMenuModelExist(Composite parentComposite, Menu parent) {
		MenuItem mi = new MenuItem(parent, SWT.NONE);
		mi.setText("模块退出");
		mi.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				logger.debug("model Exit!!!");
				parentComposite.getParent().getShell().setText(UtilsConsts.ACC_ProjectTitleDefault);
				parentComposite.dispose();
			}
		});
	}
	/**
	 * 添加退出系统的菜单
	 * @param parent Menu
	 */
	public static void addMenuSystemExist(Menu parent) {
		MenuItem miExist = new MenuItem(parent, SWT.NONE);
		miExist.setText("系统退出");
		miExist.addListener(SWT.Selection, UtilsSWTListener.getListenerExist());			
	}
}
