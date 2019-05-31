package des.wangku.operate.standard.swt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.dialog.SearchText;
import des.wangku.operate.standard.task.InterfaceTablesDialog;
import des.wangku.operate.standard.utls.UtilsArrays;
import des.wangku.operate.standard.utls.UtilsSWTTools;
import des.wangku.operate.standard.utls.UtilsSWTTree;
import des.wangku.operate.standard.utls.UtilsString;
import des.wangku.operate.standard.utls.UtilsVerification;

/**
 * 重构Tree UI组件
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MultiTree extends Tree {
	private static int ACC_style = SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI;
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(MultiTree.class);
	/** 父窗口 */
	Composite parent;
	/** 子容器 */
	Shell shell;
	Display display = null;
	/** 本对象 */
	MultiTree base = this;

	boolean isFormatID = true;

	int maxIDLen = 0;

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
		this.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem f = (TreeItem) e.item;
				InterfaceMultiTreeExtend after = UtilsSWTTools.getParentInterfaceObj(base, InterfaceMultiTreeExtend.class);
				if (f != null && after != null) {
					after.multiTreeSelectedAfter(f);
					logger.info("MultiTree SelectionListener working:" + toStringTreeItem(f));
					//logger.info("MultiTree SelectionListener working:" + isafter);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

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
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Point point = new Point(e.x, e.y);
				TreeItem item = base.getItem(point);
				if (item == null) return;
				InterfaceMultiTreeExtend after = UtilsSWTTools.getParentInterfaceObj(base, InterfaceMultiTreeExtend.class);
				if (item != null && after != null) {
					after.multiTreeMouseDoubleClick(item);
					logger.info("MultiTree MouseDoubleClickListener working:" + toStringTreeItem(item));
					//logger.info("MultiTree SelectionListener working:" + isafter);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {

			}

		});
	}

	/**
	 * 添加监听器 针对键盘
	 * @return KeyListener
	 */
	private final KeyListener addKeyListener() {
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
		if (isFormatID) maxIDLen = maxIDLen(list, 0);
		for (UnitClass e : list) {
			TreeItem t1 = e.toTreeItem(base, this);//mkItem(this, e.getNameAll(),e.value, e.isCheck);
			if (t1 == null) continue;
			for (UnitClass f : e.list) {
				putUnitClass(f, t1);
			}
		}
	}

	static final int maxIDLen(List<UnitClass> list, int max) {
		for (UnitClass e : list) {
			int sort = e.id.length();
			if (sort > max) max = sort;
			int sort2 = maxIDLen(e.list, max);
			if (sort2 > max) max = sort2;
		}
		return max;
	}

	/**
	 * 导入数据，未重组数据
	 * @param list List&lt;UnitClass&gt;
	 */
	public final void put(List<UnitClass> list) {
		UnitClass result = new UnitClass();
		recombination(list, result);
		putUnitClass(result.list);
	}

	/**
	 * 导入数据
	 * @param rs ResultSet
	 */
	public final void put(ResultSet rs) {
		if (rs == null) return;
		List<UnitClass> list = resultSetChange(rs);
		/*
		 * for (UnitClass e : list) {
		 * System.out.println("e:" + e.toString());
		 * }
		 */
		put(list);
	}

	/**
	 * 把ResultSet转成List&lt;UnitClass&gt;<br>
	 * ResultSet : id,name,value,[targetid]
	 * @param rs ResultSet
	 * @return List&lt;UnitClass&gt;
	 */
	static final List<UnitClass> resultSetChange(ResultSet rs) {
		List<UnitClass> list = new ArrayList<>();
		try {
			int len = rs.getMetaData().getColumnCount();
			if (len < 4) return list;
			while (rs.next()) {
				UnitClass f = new UnitClass(rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4));
				list.add(f);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 重组对象组
	 * @param list List&lt;UnitClass&gt;
	 * @param result UnitClass
	 */
	static final void recombination(List<UnitClass> list, UnitClass result) {
		String targetid = result.id;
		for (int i = list.size(); i >= 0; i--) {
			if (list.size() <= i) continue;
			UnitClass e = list.get(i);
			String target = e.targetid;
			if ((targetid == target) || (targetid != null && targetid.equals(target))) {
				list.remove(i);
				result.list.add(e);
				Collections.sort(result.list);
				recombination(list, e);
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
	 * @param value String
	 * @param isCheck boolean
	 * @return TreeItem
	 */
	public final TreeItem mkItem(Tree parent, String title, String value, boolean isCheck) {
		if (isExist(title)) return null;
		TreeItem t1 = new TreeItem(parent, SWT.NONE);
		privateMKItem(t1, title, value);
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
		privateMKItem(t1, text, id);
	}

	/**
	 * 得到标签名称
	 * @param id String
	 * @param name String
	 * @return String
	 */
	static final String getTagTitle(String id, String name) {
		if (id == null) return name;
		return "[" + id + "]" + name;
	}

	/**
	 * 得到标签名称
	 * @param id String
	 * @param name String
	 * @return String
	 */
	static final String getTagTitle(int maxlen, String id, String name) {
		if (id == null) return name;
		String newid = format(id, maxlen);//String.format("%1$4s", id);
		return "[" + newid + "]" + name;
	}

	/**
	 * 格式化字符串，先判断是否为数字。如果为数字，则前面补0，如果为字符串，则左对齐
	 * @param id String
	 * @param maxlen int
	 * @return String
	 */
	static final String format(String id, int maxlen) {
		if (UtilsString.isNumber(id)) return String.format("%0" + maxlen + "d", Integer.parseInt(id));
		return String.format("%1$-" + maxlen + "s", id);
	}

	/**
	 * 添加TreeItem的内容
	 * @param t1 TreeItem
	 * @param ff UnitClass
	 */
	static final void privateMKItem(TreeItem t1, UnitClass ff) {
		privateMKItem(t1, ff.name, ff.value);
	}

	/**
	 * 添加TreeItem的内容
	 * @param t1 TreeItem
	 * @param title title
	 * @param value Object
	 */
	private static final void privateMKItem(TreeItem t1, String title, Object value) {
		if (title != null) t1.setText(title);
		if (value != null) t1.setData(value);
		t1.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.button == 1) { //按键不是左键跳出. 1左键,2中键,3右键
					System.out.println("t1:" + t1.getText());
					return;

				}
			}

		});
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
	 * @param e TreeItem
	 * @return String
	 */
	static final String getID(TreeItem e) {
		return getID(e.getText());
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
		TreeItem t1 = e.toTreeItem(this, t);//mkItem(t, e.getNameAll(), e.isCheck);
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
	public static final class UnitClass implements Comparable<UnitClass> {
		/** 是否选中 */
		boolean isCheck = false;
		/** 编号 唯一 */
		String id = null;
		/** 名称 */
		String name = null;
		/** 内容值 */
		String value = null;
		/** 关联字段 */
		String targetid = null;
		/** 下级子列 */
		List<UnitClass> list = new ArrayList<>();

		public UnitClass() {

		}

		public UnitClass(Object... arrs) {
			if (arrs.length < 4) return;
			if (arrs[0] != null) id = arrs[0].toString();
			if (arrs[1] != null) name = arrs[1].toString();
			if (arrs[2] != null) value = arrs[2].toString();
			if (arrs[3] != null) targetid = arrs[3].toString();
		}

		public UnitClass(String id, String name, String value, String targetid) {
			this.id = id;
			this.name = name;
			this.value = value;
			this.targetid = targetid;
		}

		public UnitClass(String id, String name, String value, String targetid, boolean isCheck) {
			this.id = id;
			this.name = name;
			this.value = value;
			this.targetid = targetid;
			this.isCheck = isCheck;
		}

		public UnitClass(String name) {
			this.name = name;
		}

		/**
		 * 把TreeItem转成UnitClass
		 * @param e TreeItem
		 */
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
			return "UnitClass [isCheck=" + isCheck + ", id=" + id + ", name=" + name + ", value=" + value + ", targetid=" + targetid + ", list=" + list + "]";
		}

		/**
		 * 返回含有id号的字符串名称
		 * @return String
		 */
		public final String getNameAll() {
			if (name == null) return null;
			if (id == null || id.length() == 0) return name;
			return getTagTitle(id, name);
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getTargetid() {
			return targetid;
		}

		public void setTargetid(String targetid) {
			this.targetid = targetid;
		}

		@Override
		public int compareTo(UnitClass arg0) {
			return this.id.compareTo(arg0.id);
		}

		public TreeItem toTreeItem(MultiTree base, Tree parent) {
			TreeItem t = new TreeItem(parent, SWT.NONE);
			return toTreeItemPrivate(base, t);
		}

		public TreeItem toTreeItem(MultiTree base, TreeItem parent) {
			TreeItem t = new TreeItem(parent, SWT.NONE);
			return toTreeItemPrivate(base, t);
		}
		/**
		 * 拼接TreeItem
		 * @param base MultiTree
		 * @param t TreeItem
		 * @return TreeItem
		 */
		private TreeItem toTreeItemPrivate(MultiTree base, TreeItem t) {
			String newname = getIDNameText(base);
			if (newname != null) t.setText(newname);
			t.setData(value != null ? value : id);
			t.setChecked(isCheck);
			return t;
		}
		/**
		 * 通过id,name得到具体显示内容
		 * @param base MultiTree
		 * @return String
		 */
		private String getIDNameText(MultiTree base) {
			if (name == null) return null;
			if (id == null || id.length() == 0) return name;
			if (base.isFormatID) return getTagTitle(base.maxIDLen, id, name);
			return getTagTitle(id, name);
		}
	}

	static final String toStringTreeItem(TreeItem f) {
		if (f == null) return "null";
		return f.getText() + "[" + f.getData() + "]";
	}
}
