package des.wangku.operate.standard.swtComposite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
/**
 * SWT检索
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class SWTSearch {
	/**
	 * 从Menu中提取ID号MenuItem，可能会出现相同ID
	 * @param e Menu
	 * @param id int
	 * @return MenuItem[]
	 */
	public static final MenuItem[] menuSearch(Menu e, int id) {
		List<MenuItem> list = menuItemSearch(e);
		List<MenuItem> resultlist=list.stream().filter(x->x.getID()==id).collect(Collectors.toList());
		MenuItem[] arrs = {};
		return resultlist.toArray(arrs);
	}
	/**
	 * 从Menu中提取所有 MenuItem
	 * @param e Menu
	 * @return List&lt;MenuItem&gt;
	 */
	public static final List<MenuItem> menuItemSearch(Menu e) {
		List<MenuItem> list = new ArrayList<>();
		menuPrivate(list, e);
		return list;
	}
	/**
	 * 从Menu中提取所有 MenuItem
	 * @param list List&lt;MenuItem&gt;
	 * @param t Menu
	 */
	private static final void menuPrivate(List<MenuItem> list, Menu t) {
		if (t == null || t.isDisposed()) return;
		MenuItem[] arrs = t.getItems();
		for (MenuItem g : arrs) {
			list.add(g);
			menuPrivate(list, g.getMenu());
		}

	}
}
