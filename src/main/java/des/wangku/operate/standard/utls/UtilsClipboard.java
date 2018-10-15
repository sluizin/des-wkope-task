package des.wangku.operate.standard.utls;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

/**
 * 剪贴板操作
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsClipboard {

	/**
	 * 把字符串拷到剪贴板中
	 * @param content String
	 */
	public static final void copy(String content) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection contents = new StringSelection(content); //用拷贝文本框文本实例化StringSelection对象  
		clipboard.setContents(contents, null);
	}

	/**
	 * 从粘贴板中获取字符串
	 * @return String
	 */
	public static final String getString() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (!clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) return "";
		// 如果剪贴板中包含stringFlavor内容
		try {
			// 取出剪贴板中stringFlavor内容
			String content = (String) clipboard.getData(DataFlavor.stringFlavor);
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
}
