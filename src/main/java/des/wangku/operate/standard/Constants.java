package des.wangku.operate.standard;

import java.lang.reflect.Field;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import sun.misc.Unsafe;

/**
 * 常量池
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("restriction")
public final class Constants {

	public static final Unsafe UNSAFE;

	public static int CTabItemBaseOffset = 0;
	public static int CTabItemIndexScale = 0;
	public static long CTabFolderItemsOffset = 0L;
	
	public static int TreeBaseOffset = 0;
	public static int TreeIndexScale = 0;
	public static long TreeItemsOffset = 0L;
	public static long TreeItemsHandleOffset = 0L;
	static {
		try {
			final Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			UNSAFE = (Unsafe) field.get(null);

			CTabItemBaseOffset = UNSAFE.arrayBaseOffset(CTabItem[].class);
			CTabItemIndexScale = UNSAFE.arrayIndexScale(CTabItem[].class);
			CTabFolderItemsOffset = UNSAFE.objectFieldOffset(CTabFolder.class.getDeclaredField("items"));

			TreeBaseOffset = UNSAFE.arrayBaseOffset(TreeItem[].class);
			TreeIndexScale = UNSAFE.arrayIndexScale(TreeItem[].class);
			TreeItemsOffset = UNSAFE.objectFieldOffset(Tree.class.getDeclaredField("items"));
			TreeItemsHandleOffset = UNSAFE.objectFieldOffset(TreeItem.class.getDeclaredField("handle"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
