package des.wangku.operate.standard.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.utls.UtilsArrays;
import des.wangku.operate.standard.utls.UtilsSWTTree;
import des.wangku.operate.standard.utls.UtilsVerification;

/**
 * 重构Tree UI组件
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MultiTree extends Tree {
	private static int ACC_style = SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI;
	private boolean isShowID = true;
	private boolean isShowIDLeft = false;

	protected void checkSubclass() {

	}

	public MultiTree(Composite parent, int style) {
		super(parent, style | ACC_style);
		checkSubclass();
		addListener();
	}

	/**
	 * 添加监听器
	 */
	private void addListener() {
		addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					if (item == null) return;
					boolean checked = item.getChecked();
					UtilsSWTTree.changeSelectTreeCheck(item, checked);
				}
			}
		});
	}

	/**
	 * 得到指定级数位置的标签内容
	 * @param arrs int[]
	 * @return String
	 */
	public String getTreeItemString(int... arrs) {
		TreeItem e = getTreeItem(arrs);
		if (e == null) return null;
		return e.getText();
	}

	/**
	 * @param arrs TreeItem[]
	 * @param index int
	 * @return TreeItem
	 */
	public TreeItem getItems(TreeItem[] arrs, int index) {
		int len = arrs.length;
		if (len == 0) return null;
		if (index < 0 || index >= len) return null;
		return arrs[index];
	}

	/**
	 * 按照等级数字串得到对象
	 * @param arrs int[]
	 * @return TreeItem
	 */
	public TreeItem getTreeItem(int... arrs) {
		if (arrs.length == 0) return null;
		if (arrs.length == 1) {
			if (arrs[0] < 0) return null;
			return getItems(getItems(), arrs[0]);
		}
		TreeItem f = this.getItem(arrs[0]);
		TreeItem e = null;
		for (int i = 1; i < arrs.length; i++) {
			e = getItems(f.getItems(), arrs[i]);
			if (e == null) return null;
			f = e;
		}
		return e;
	}

	/**
	 * 通过json字符串导入数据
	 * @param json String
	 */
	public void putJson(String json) {
		List<UnitClass> list = JSON.parseArray(json, UnitClass.class);
		putUnitClass(list);
	}

	/**
	 * 通过UnitClass 数组导入数据
	 * @param list List&lt;UnitClass&gt;
	 */
	public void putUnitClass(List<UnitClass> list) {
		for (UnitClass e : list) {
			TreeItem t1 = mkItem(this, e.getNameAll(this));
			if (t1 == null) continue;
			for (UnitClass f : e.list) {
				putUnitClass(f, t1);
			}
		}
	}

	public final TreeItem mkItem(TreeItem parent, String title) {
		if (isExist(title)) return null;
		TreeItem t1 = new TreeItem(parent, SWT.NONE);
		t1.setText(title);
		return t1;
	}

	public final TreeItem mkItem(Tree parent, String title) {
		if (isExist(title)) return null;
		TreeItem t1 = new TreeItem(parent, SWT.NONE);
		t1.setText(title);
		return t1;
	}

	/**
	 * 从MultiTree查找编号id，是否存在
	 * @param id String
	 * @return boolean
	 */
	public final boolean isExist(String id) {
		TreeItem[] arrs = this.getItems();
		for (TreeItem e : arrs) {
			if (isExist(id, e)) return true;
		}
		return false;
	}

	/**
	 * 在TreeItem中查找编号ID，是否相同
	 * @param id String
	 * @param f TreeItem
	 * @return boolean
	 */
	private static final boolean isExist(String id, TreeItem f) {
		if (id == null || f == null) return false;
		if (id.equals(UtilsVerification.getStringID(f.getText()))) return true;
		TreeItem[] arrs = f.getItems();
		for (TreeItem e : arrs) {
			if (isExist(id, e)) return true;
		}
		return false;
	}

	/**
	 * 按等级导入标签单元
	 * @param e UnitClass
	 * @param t TreeItem
	 */
	public void putUnitClass(UnitClass e, TreeItem t) {
		if (e == null || t == null) return;
		TreeItem t1 = mkItem(t, e.getNameAll(this));
		if (t1 == null) return;
		for (UnitClass f : e.list) {
			putUnitClass(f, t1);
		}
	}

	/**
	 * 添加字符串到指定位置
	 * @param text String
	 * @param arrs int[]
	 */
	public void add(String text, int... arrs) {
		if (text == null) return;
		TreeItem e = getTreeItem(arrs);
		if (e == null) {
			TreeItem t1 = new TreeItem(this, SWT.NONE);
			t1.setText(text);
			return;
		}
		TreeItem t1 = new TreeItem(e, SWT.NONE);
		t1.setText(text);
	}

	/**
	 * 添加多个字符串
	 * @param arrs int[]
	 * @param textarrs String[]
	 */
	public void add(int[] arrs, String... textarrs) {
		for (String e : textarrs) {
			add(e, arrs);
		}
	}

	/**
	 * 添加多个字符串
	 * @param textarrs String[]
	 * @param arrs int[]
	 */
	public void add(String[] textarrs, int... arrs) {
		add(arrs, textarrs);
	}

	/**
	 * Tree的最深度
	 * @return int
	 */
	public int getDeep() {
		TreeItem[] arss = getItems();
		if (arss.length == 0) return -1;
		int deepCount = 0;
		for (TreeItem e : arss) {
			int deep = getDeep(e);
			if (deep > deepCount) deepCount = deep;
		}
		return deepCount;
	}

	/**
	 * 得到某个栏目的深度 -1即没有深度
	 * @param e TreeItem
	 * @return int
	 */
	public int getDeep(TreeItem e) {
		if (e == null) return 0;
		DeepClass dc = new DeepClass(1);
		getPrivateDeeps(dc, 0, e);
		return dc.deep;
	}

	/**
	 * 得到选中的词条数组
	 * @param filterArr int[]
	 * @return List&lt;TreeItem&gt;
	 */
	public List<TreeItem> getCheckedList(int... filterArr) {
		TreeItem[] arrs = getItems();
		DeepClass dc = new DeepClass(0);
		for (TreeItem e : arrs) {
			if (UtilsArrays.isfilterArr(0, filterArr) && e.getChecked()) {
				dc.list.add(e);
			}
			getTreeItemList(dc, 0, e, filterArr);
		}
		return dc.list;
	}

	/**
	 * @param dc DeepClass
	 * @param deep int
	 * @param e TreeItem
	 * @param arr int[]
	 */
	private static void getTreeItemList(DeepClass dc, int deep, TreeItem e, int... arr) {
		if (e == null) return;
		deep++;
		TreeItem[] arrs = e.getItems();
		for (TreeItem f : arrs) {
			if (UtilsArrays.isfilterArr(deep, arr) && f.getChecked()) {
				dc.list.add(f);
			}
			getTreeItemList(dc, deep, f, arr);
		}
	}

	/**
	 * 另类对象
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	private static final class DeepClass {
		int deep = 0;
		List<TreeItem> list = new ArrayList<>();

		public DeepClass(int deep) {
			this.deep = deep;
		}
	}

	/**
	 * 得到深度的静态方法
	 * @param dc DeepClass
	 * @param deep int
	 * @param e TreeItem
	 */
	private static final void getPrivateDeeps(DeepClass dc, int deep, TreeItem e) {
		if (e == null) return;
		TreeItem[] arrs = e.getItems();
		if (arrs.length > 0) deep++;
		if (dc.deep < deep) dc.deep = deep;
		for (TreeItem f : arrs) {
			getPrivateDeeps(dc, deep, f);
		}
	}

	/**
	 * 得取树型的对象集
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> getList() {
		List<UnitClass> list = new ArrayList<>();
		TreeItem[] arrs = getItems();
		for (TreeItem e : arrs) {
			list.add(new UnitClass(e));
		}
		return list;
	}

	/**
	 * 树形结构单元
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class UnitClass {
		/** 编号 唯一 */
		String id = null;
		/** 名称 */
		String name = null;
		/** 下级子列 */
		List<UnitClass> list = new ArrayList<>();

		public UnitClass() {

		}

		public UnitClass(String name) {
			this.name = name;
		}

		public UnitClass(TreeItem e) {
			if (e == null) return;
			this.name = e.getText();
			TreeItem[] arrs = e.getItems();
			for (TreeItem f : arrs) {
				list.add(new UnitClass(f));
			}
		}

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final List<UnitClass> getList() {
			return list;
		}

		public final void setList(List<UnitClass> list) {
			this.list = list;
		}

		public final String getId() {
			return id;
		}

		public final void setId(String id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "UnitClass [" + (id != null ? "id=" + id + ", " : "") + (name != null ? "name=" + name + ", " : "") + (list != null ? "list=" + list : "") + "]";
		}

		/**
		 * 返回含有id号的字符串名称
		 * @param mt MultiTree
		 * @return String
		 */
		public final String getNameAll(MultiTree mt) {
			if (mt == null || name == null) return null;
			if (!mt.isShowID) return name;
			if (id == null || id.length() == 0) return name;
			if (mt.isShowIDLeft) return "[" + id + "]" + name;
			return name + "[" + id + "]";
		}
	}

	public final boolean isShowID() {
		return isShowID;
	}

	public final void setShowID(boolean isShowID) {
		this.isShowID = isShowID;
	}

	public final boolean isShowIDLeft() {
		return isShowIDLeft;
	}

	public final void setShowIDLeft(boolean isShowIDLeft) {
		this.isShowIDLeft = isShowIDLeft;
	}

}
