package des.wangku.operate.standard.utls;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * 托盘管理
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTTray {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsSWTTray.class);

	/**
	 * 设置显示隐藏托盘
	 * @param shell Shell
	 * @param display Display
	 */
	public static final void initTrayDisplay(Shell shell, Display display) {
		//shell.setImage(display.getSystemImage(SWT.ICON_INFORMATION));
		Tray tray = display.getSystemTray();
		final TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		Image a = display.getSystemImage(16);
		trayItem.setImage((a == null) ? display.getSystemImage(SWT.ICON_INFORMATION) : a);
		trayItem.setVisible(false);
		trayItem.setToolTipText(shell.getText());
		trayItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UtilsSWTTray.toggleDisplay(shell, tray);
			}
		});
		final Menu trayMenu = new Menu(shell, SWT.POP_UP);
		MenuItem showMenuItem = new MenuItem(trayMenu, SWT.PUSH);
		showMenuItem.setText("显示平台(&s)");
		showMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UtilsSWTTray.toggleDisplay(shell, tray);
			}
		});
		trayMenu.setDefaultItem(showMenuItem);
		new MenuItem(trayMenu, SWT.SEPARATOR);
		MenuItem exitMenuItem = new MenuItem(trayMenu, SWT.PUSH);
		exitMenuItem.setText("退出平台(&x)");
		exitMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
				System.exit(0);
			}
		});
		trayItem.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				trayMenu.setVisible(true);
			}
		});
		shell.addShellListener(new ShellAdapter() {
			public void shellIconified(ShellEvent e) {
				trayItem.setToolTipText(shell.getText());
				UtilsSWTTray.toggleDisplay(shell, tray);
			}

			public void shellClosed(ShellEvent e) {
				e.doit = false;
				UtilsSWTTray.toggleDisplay(shell, tray);
			}
		});

	}

	/**
	 * 设置托盘
	 * @param shell Shell
	 * @param tray Tray
	 */
	public static void toggleDisplay(Shell shell, Tray tray) {
		try {
			shell.setVisible(!shell.isVisible());
			tray.getItem(0).setVisible(!shell.isVisible());
			if (shell.getVisible()) {
				shell.setMinimized(false);
				shell.setActive();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
