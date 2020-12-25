package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * 针对Control的操作工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsComposite {

	/**
	 * 清空所有控件
	 * @param controls Control[]
	 */
	public static final void cleanControls(Control[] controls) {
		if (controls == null || controls.length == 0) return;
		for (Control e : controls) {
			if (e instanceof Text) {
				Text d = (Text) e;
				d.setText("");
				continue;
			}
			if (e instanceof Button) {
				Button d = (Button) e;
				d.setSelection(false);
				continue;
			}
			if (e instanceof Spinner) {
				Spinner d = (Spinner) e;
				d.setSelection(0);
				continue;
			}

		}
	}

	static final Map<Long, List<Boolean>> LockComMap = new HashMap<>();

	/**
	 * 锁定窗口里所有控件<br>
	 * 返回修改控件数量
	 * @param com Composite
	 * @param islock boolean
	 * @return int
	 */
	public static final int lockControls(Composite com, boolean islock) {
		if (com == null) return 0;
		Long comid = com.handle;
		Control[] controls = com.getChildren();
		int len = controls.length;
		if (len == 0) return 0;
		if (islock) {
			List<Boolean> list = new ArrayList<>(len);
			for (Control e : controls) {
				list.add(e.getEnabled());
				e.setEnabled(false);
			}
			LockComMap.put(comid, list);
			return len;
		}
		if (LockComMap.containsKey(comid)) {
			List<Boolean> list = LockComMap.get(comid);
			int sort = 0;
			for (int i = 0, len2 = list.size(); i < len2 && i < len; i++) {
				controls[i].setEnabled(list.get(i));
				sort++;
			}
			return sort;
		}
		for (Control e : controls) {
			e.setEnabled(true);
		}
		return len;
	}
}
