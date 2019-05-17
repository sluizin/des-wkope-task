package des.wangku.operate.standard.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.dialog.SearchText;
import des.wangku.operate.standard.task.InterfaceTablesDialog;
import des.wangku.operate.standard.utls.UtilsArrays;
import des.wangku.operate.standard.utls.UtilsSWTTools;
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
	/** 父窗口 */
	Composite parent;
	/** 子容器 */
	Shell shell;
	Display display = null;
	/** 本对象 */
	MultiTree base = this;
	private boolean isShowID = false;
	private boolean isShowIDLeft = false;

	protected void checkSubclass() {

	}

	public MultiTree(Composite parent, int style) {
		super(parent, style | ACC_style);
		checkSubclass();
		this.parent = parent;
		this.shell = new Shell(base.getShell());
		shell.setText("检索关键字");
		this.display = parent.getDisplay();
		addListener();
	}

	/**
	 * 添加监听器
	 */
	private final void addListener() {
		this.addListener(SWT.Selection, new Listener() {
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
		this.addKeyListener(addKeyListener());
		this.addListener(SWT.FocusIn, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				if (item == null) return;
			}
		});
	}
	/**
	 * 添加监听器 针对键盘
	 * @return KeyListener
	 */
	final KeyListener addKeyListener() {
		KeyListener t = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				/* 移除项目 */
				if (e.keyCode == SWT.DEL) {
					MultiTree b = (MultiTree) e.getSource();
					TreeItem[] arrs = b.getSelection();
					for (TreeItem ee : arrs) {
						if (ee == null || ee.isDisposed()) continue;
						ee.removeAll();
						ee.dispose();
					}
				}

				if (e.stateMask == SWT.CTRL && (e.keyCode == 'f')) {
					InterfaceTablesDialog parent = UtilsSWTTools.getParentInterfaceObj(base, InterfaceTablesDialog.class);
					//parent.setSearchDialog(new SearchDialog(table.getShell(), 0, table));
					parent.setSearchDialog((new SearchText(shell, 0)));
					Object obj = parent.getSearchDialog().open();
					if (obj == null) return;
					String value = (String) obj;
					List<TreeItem> list = base.getTreeItemList();
					for (TreeItem ff : list) {
						if (ff.getText().indexOf(value) > -1) {
							ff.setChecked(true);
							expandedParent(ff, true);
						}
					}
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

		};
		return t;
	}

	/**
	 * 得到指定级数位置的标签内容
	 * @param arrs int[]
	 * @return String
	 */
	public final String getTreeItemString(int... arrs) {
		TreeItem e = getTreeItem(arrs);
		if (e == null) return null;
		return e.getText();
	}

	/**
	 * @param arrs TreeItem[]
	 * @param index int
	 * @return TreeItem
	 */
	public final TreeItem getItems(TreeItem[] arrs, int index) {
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
	public final TreeItem getTreeItem(int... arrs) {
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
	public final void putJson(String json) {
		if (json == null || json.length() == 0) return;
		List<UnitClass> list = JSON.parseArray(json, UnitClass.class);
		putUnitClass(list);
	}

	/**
	 * 通过UnitClass 数组导入数据
	 * @param list List&lt;UnitClass&gt;
	 */
	public final void putUnitClass(List<UnitClass> list) {
		for (UnitClass e : list) {
			TreeItem t1 = mkItem(this, e.getNameAll(this), e.isCheck);
			if (t1 == null) continue;
			for (UnitClass f : e.list) {
				putUnitClass(f, t1);
			}
		}
	}

	/**
	 * 建立item
	 * @param parent TreeItem
	 * @param title String
	 * @param isCheck boolean
	 * @return TreeItem
	 */
	public final TreeItem mkItem(TreeItem parent, String title, boolean isCheck) {
		if (isExist(title)) return null;
		TreeItem t1 = new TreeItem(parent, SWT.NONE);
		privateMKItem(t1, title);
		t1.setChecked(isCheck);
		return t1;

	}

	/**
	 * 建立item
	 * @param parent TreeItem
	 * @param title String
	 * @return TreeItem
	 */
	public final TreeItem mkItem(TreeItem parent, String title) {
		return mkItem(parent, title, false);
	}

	/**
	 * 建立item
	 * @param parent Tree
	 * @param title String
	 * @param isCheck boolean
	 * @return TreeItem
	 */
	public final TreeItem mkItem(Tree parent, String title, boolean isCheck) {
		if (isExist(title)) return null;
		TreeItem t1 = new TreeItem(parent, SWT.NONE);
		privateMKItem(t1, title);
		t1.setChecked(isCheck);
		return t1;

	}

	/**
	 * 建立item
	 * @param parent Tree
	 * @param title String
	 * @return TreeItem
	 */
	public final TreeItem mkItem(Tree parent, String title) {
		return mkItem(parent, title, false);
	}

	/**
	 * 添加TreeItem的内容
	 * @param t1 TreeItem
	 * @param title title
	 */
	private final void privateMKItem(TreeItem t1, String title) {

		String id = getID(title);
		String name = title.replaceAll("\\[" + id + "\\]", "");
		String text = getTagTitle(id, name);
		privateMKItem(t1, id, text);
	}

	/**
	 * 得到标签名称，是否含有id号，或在左，或在右
	 * @param id String
	 * @param name String
	 * @return String
	 */
	final String getTagTitle(String id, String name) {
		if (!isShowID) return name;
		if (isShowIDLeft) return "[" + id + "]" + name;
		return name + "[" + id + "]";

	}

	/**
	 * 添加TreeItem的内容
	 * @param t1 TreeItem
	 * @param value Object
	 * @param title title
	 */
	private static final void privateMKItem(TreeItem t1, Object value, String title) {
		t1.setText(title);
		if (value != null) t1.setData(value);
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
		if (id.equals(f.getData())) return true;
		if (id.equals(getID(f.getText()))) return true;
		TreeItem[] arrs = f.getItems();
		for (TreeItem e : arrs) {
			if (isExist(id, e)) return true;
		}
		return false;
	}

	/**
	 * 通过内容得到id
	 * @param title String
	 * @return String
	 */
	static final String getID(String title) {
		return UtilsVerification.getStringID(title);
	}

	/**
	 * 按等级导入标签单元
	 * @param e UnitClass
	 * @param t TreeItem
	 */
	public final void putUnitClass(UnitClass e, TreeItem t) {
		if (e == null || t == null) return;
		TreeItem t1 = mkItem(t, e.getNameAll(this), e.isCheck);
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
	public final void add(String text, int... arrs) {
		if (text == null) return;
		TreeItem e = getTreeItem(arrs);
		if (e == null) {
			TreeItem t1 = new TreeItem(this, SWT.NONE);
			privateMKItem(t1, text);
			return;
		}
		TreeItem t1 = new TreeItem(e, SWT.NONE);
		privateMKItem(t1, text);
	}

	/**
	 * 添加多个字符串
	 * @param arrs int[]
	 * @param textarrs String[]
	 */
	public final void add(int[] arrs, String... textarrs) {
		for (String e : textarrs) {
			add(e, arrs);
		}
	}

	/**
	 * 添加多个字符串
	 * @param textarrs String[]
	 * @param arrs int[]
	 */
	public final void add(String[] textarrs, int... arrs) {
		add(arrs, textarrs);
	}

	/**
	 * Tree的最深度
	 * @return int
	 */
	public final int getDeep() {
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
	public final int getDeep(TreeItem e) {
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
	public final List<TreeItem> getCheckedList(int... filterArr) {
		TreeItem[] arrs = getItems();
		DeepClass dc = new DeepClass(0);
		for (TreeItem e : arrs) {
			if (UtilsArrays.isfilterArr(0, filterArr) && e.getChecked()) {
				dc.list.add(e);
			}
			getTreeItemCheckedList(dc, 0, e, filterArr);
		}
		return dc.list;
	}

	/**
	 * @param dc DeepClass
	 * @param deep int
	 * @param e TreeItem
	 * @param arr int[]
	 */
	private static final void getTreeItemCheckedList(DeepClass dc, int deep, TreeItem e, int... arr) {
		if (e == null) return;
		deep++;
		TreeItem[] arrs = e.getItems();
		for (TreeItem f : arrs) {
			if (UtilsArrays.isfilterArr(deep, arr) && f.getChecked()) {
				dc.list.add(f);
			}
			getTreeItemCheckedList(dc, deep, f, arr);
		}
	}

	/**
	 * 得到选中的词条数组
	 * @param filterArr int[]
	 * @return List&lt;TreeItem&gt;
	 */
	public final List<TreeItem> getTreeItemList(int... filterArr) {
		TreeItem[] arrs = getItems();
		DeepClass dc = new DeepClass(0);
		for (TreeItem e : arrs) {
			if (UtilsArrays.isfilterArr(0, filterArr)) {
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
	static final void getTreeItemList(DeepClass dc, int deep, TreeItem e, int... arr) {
		if (e == null) return;
		deep++;
		TreeItem[] arrs = e.getItems();
		for (TreeItem f : arrs) {
			if (UtilsArrays.isfilterArr(deep, arr)) {
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
	@Deprecated
	public final List<UnitClass> getList() {
		List<UnitClass> list = new ArrayList<>();
		TreeItem[] arrs = getItems();
		for (TreeItem e : arrs) {
			list.add(new UnitClass(e));
		}
		return list;
	}

	/**
	 * 得取树型的对象集 所有对象
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<TreeItem> getTreeItemList() {
		List<TreeItem> list = new ArrayList<>();
		TreeItem[] arrs = getItems();
		for (TreeItem e : arrs) {
			list.add(e);
			treeItemList(list, e);
		}
		return list;
	}

	/**
	 * 得取树型的对象集 指定某个对象
	 * @param e TreeItem
	 * @return list&lt;TreeItem&gt;
	 */
	public final List<TreeItem> getTreeItemList(TreeItem e) {
		List<TreeItem> list = new ArrayList<>();
		treeItemList(list, e);
		return list;
	}

	/**
	 * 嵌套调出所有此treeitem下的所有子标签
	 * @param list &lt;TreeItem&gt;
	 * @param e TreeItem
	 */
	private static final void treeItemList(List<TreeItem> list, TreeItem e) {
		if (list == null || e == null) return;
		for (TreeItem f : e.getItems()) {
			list.add(f);
			treeItemList(list, f);
		}
	}

	/**
	 * 得到所有选中的项目
	 * @return List&lt;UnitClass&gt;
	 */
	@Deprecated
	public final List<UnitClass> getSelectList() {
		List<UnitClass> sourceList = getList();
		List<UnitClass> list = new ArrayList<>();
		for (UnitClass e : sourceList) {
			if (e.isCheck) list.add(e);
			getSelectList(list, e);
		}
		return list;
	}

	/**
	 * 嵌套调出选中的单元选项 list
	 * @param list List&lt;UnitClass&gt;
	 * @param e UnitClass
	 */
	@Deprecated
	private static final void getSelectList(List<UnitClass> list, UnitClass e) {
		for (UnitClass f : e.list) {
			if (f.isCheck) list.add(f);
			getSelectList(list, f);
		}
	}

	/**
	 * 得到所有选中的项目
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<TreeItem> getSelectTreeItemList() {
		List<TreeItem> sourceList = getTreeItemList();
		List<TreeItem> list = new ArrayList<>();
		for (TreeItem e : sourceList) {
			if (e.getChecked()) list.add(e);
		}
		return list;
	}

	/**
	 * 嵌套调出选中的单元选项 list
	 * @param list List&lt;UnitClass&gt;
	 * @param e UnitClass
	 */
	static final void getSelectList(List<TreeItem> list, TreeItem e) {
		for (TreeItem f : e.getItems()) {
			if (f.getChecked()) list.add(f);
			getSelectList(list, f);
		}
	}

	/**
	 * 得到选择的ID list集
	 * @return List&lt;String&gt;
	 */
	@Deprecated
	public final List<String> getIDListUnitClass() {
		List<UnitClass> selectList = getSelectList();
		List<String> idList = new ArrayList<>();
		for (UnitClass e : selectList) {
			if (e.id == null) continue;
			idList.add(e.id);
		}
		return idList;
	}

	/**
	 * 得到选择的ID list集
	 * @return List&lt;String&gt;
	 */
	@Deprecated
	public final List<String> getIDList() {
		List<TreeItem> selectList = getSelectTreeItemList();
		List<String> idList = new ArrayList<>();
		for (TreeItem e : selectList) {
			String id = getID(e.getText());
			if (id == null) continue;
			idList.add(id);
		}
		return idList;
	}

	/**
	 * 得到选择的ID list集
	 * @return List&lt;String&gt;
	 */
	public final List<String> getIDsList() {
		List<Object> list = getDataList();
		List<String> idList = new ArrayList<>(list.size());
		for (Object e : list) {
			if (e == null) continue;
			idList.add(e.toString());
		}
		return idList;
	}

	/**
	 * 得到选择的ID list集
	 * @return List&lt;String&gt;
	 */
	public final List<Object> getDataList() {
		List<TreeItem> selectList = getSelectTreeItemList();
		List<Object> dataList = new ArrayList<>();
		for (TreeItem e : selectList) {
			Object obj = e.getData();
			if (obj == null) continue;
			dataList.add(obj);
		}
		return dataList;
	}

	/**
	 * 展开或关闭父类
	 * @param e TreeItem
	 * @param isExpanded boolean
	 */
	public static final void expandedParent(TreeItem e, boolean isExpanded) {
		if (e == null) return;
		TreeItem parent = e.getParentItem();
		if (parent == null) return;
		parent.setExpanded(isExpanded);
		expandedParent(parent, isExpanded);
	}

	/**
	 * 树形结构单元
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class UnitClass {
		/** 是否选中 */
		boolean isCheck = false;
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
			String content = e.getText();
			this.id = getID(content);
			if (id == null) return;
			this.isCheck = e.getChecked();
			this.name = UtilsVerification.getStringName(content);
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

		public final boolean isCheck() {
			return isCheck;
		}

		public final void setCheck(boolean isCheck) {
			this.isCheck = isCheck;
		}

		@Override
		public String toString() {
			return "UnitClass [isCheck=" + isCheck + ", " + (id != null ? "id=" + id + ", " : "") + (name != null ? "name=" + name + ", " : "") + (list != null ? "list=" + list : "") + "]";
		}

		/**
		 * 返回含有id号的字符串名称
		 * @param mt MultiTree
		 * @return String
		 */
		public final String getNameAll(MultiTree mt) {
			if (mt == null || name == null) return null;
			if (id == null || id.length() == 0) return name;
			return mt.getTagTitle(id, name);
		}
	}

	public final boolean isShowID() {
		return isShowID;
	}

	public final MultiTree setShowID(boolean isShowID) {
		this.isShowID = isShowID;
		return this;
	}

	public final boolean isShowIDLeft() {
		return isShowIDLeft;
	}

	public final void setShowIDLeft(boolean isShowIDLeft) {
		this.isShowIDLeft = isShowIDLeft;
	}

}
