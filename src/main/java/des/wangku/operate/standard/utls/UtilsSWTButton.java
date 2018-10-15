package des.wangku.operate.standard.utls;

import org.eclipse.swt.widgets.Button;

/**
 * 针对按扭的操作方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public final class UtilsSWTButton {
	/**
	 * 判断数组中是否有选中项，前提是可视Visible+可用Enabled<br>
	 * 如果其中含有选中，则返回true，否则返回false
	 * @param button_s Button[]
	 * @return boolean
	 */
	public static final boolean isCheck(Button... button_s) {
		for(Button e:button_s)
			if(e.getVisible() && e.getEnabled() && e.getSelection())return true;
		return false;
	}
}
