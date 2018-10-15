package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import des.wangku.operate.standard.swt.SourceTree;

/**
 * 针对tree的静态方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public final class UtilsSWTTree {
	static final Logger logger = Logger.getLogger(UtilsSWTTree.class);
	/** tree显示名称时多项信息的分隔符 */
	public static final String ACC_IntervalCharacter = "|";
	/** 解析tree的名称时分组split时的分隔符 */
	public static final String ACC_IntervalSplitCharacter = "\\|";
	/** tree中多选时在text中的分隔符 */
	public static final String ACC_TextChar = ";";

	/**
	 * 更改此项是否为选中状态，并关联上下多级的状态
	 * @param t TreeItem
	 * @param check boolean
	 */
	public static final void changeSelectTreeCheck(TreeItem t, boolean check) {
		if (t == null) return;
		changeSelectTreeCheckNext(t, check);
		changeSelectTreeCheckParent(t);
	}

	/**
	 * 更改子项是否为选中状态
	 * @param t TreeItem
	 * @param check boolean
	 */
	public static final void changeSelectTreeCheckNext(TreeItem t, boolean check) {
		if (t == null) return;
		t.setChecked(check);
		for (TreeItem e : t.getItems())
			changeSelectTreeCheckNext(e, check);
	}

	/**
	 * 更改父项的状态
	 * @param f TreeItem
	 */
	public static final void changeSelectTreeCheckParent(TreeItem f) {
		if (f == null) return;
		TreeItem parent = f.getParentItem();
		if (parent == null) return;
		parent.setChecked(getTreeItemNextisCheckAll(parent));
		changeSelectTreeCheckParent(parent);
	}

	/**
	 * 判断TreeItem下所有的子项，如果有一条没有选中则返回false，如果为null则代表终极项
	 * @param f TreeItem
	 * @return Boolean
	 */
	public static final Boolean getTreeItemNextisCheckAll(TreeItem f) {
		if (isTreeUltimate(f)) return null;
		for (TreeItem e : f.getItems()) {
			if (e.getChecked() == false) return false;
			Boolean isNext = getTreeItemNextisCheckAll(e);
			if (isNext != null && isNext == false) return false;
		}
		return true;
	}

	/**
	 * 是否是终极项 为null则返回真
	 * @param f TreeItem
	 * @return boolean
	 */
	public static final boolean isTreeUltimate(TreeItem f) {
		if (f == null) return true;
		if (f.getItemCount() == 0) return true;
		return false;
	}

	/**
	 * 得到tree里所有的选中项
	 * @param treearrs Tree[]
	 * @return Set &lt; TreeItem &gt; 
	 */
	public static final Set<TreeItem> getTreeItemCheckAll(Tree... treearrs) {
		Set<TreeItem> set = new HashSet<TreeItem>();
		for (Tree tree : treearrs) {
			for (TreeItem e : tree.getItems())
				treeItemCheckAll(set, e, true);
		}
		return set;
	}

	/**
	 * 得到tree里所有的选中项
	 * @param treearrs Tree[]
	 * @return List &lt; TreeItem &gt; 
	 */
	public static final List<TreeItem> getTreeItemCheckAllList(Tree... treearrs) {
		List<TreeItem> list = new ArrayList<TreeItem>();
		for (Tree tree : treearrs) {
			for (TreeItem e : tree.getItems())
				treeItemCheckAll(list, e, true);
		}
		return list;
	}

	/**
	 * 把TreeItem提到set中，判断是否选中，或未选，或忽略，后期再处理
	 * @param set Set&lt;TreeItem&gt;
	 * @param isCheck Boolean
	 * @param f TreeItem
	 */
	public static final void treeItemCheckAll(Set<TreeItem> set, TreeItem f, Boolean isCheck) {
		if (f == null) return;
		if (isCheck == null || isCheck == f.getChecked()) set.add(f);
		for (TreeItem e : f.getItems())
			treeItemCheckAll(set, e, isCheck);
	}

	/**
	 * 把TreeItem提到list中，判断是否选中，或未选，或忽略，后期再处理
	 * @param list List &lt; TreeItem &gt; 
	 * @param isCheck Boolean
	 * @param f TreeItem
	 */
	public static final void treeItemCheckAll(List<TreeItem> list, TreeItem f, Boolean isCheck) {
		if (f == null) return;
		if (isCheck == null || isCheck == f.getChecked()) if (!list.contains(f)) list.add(f);
		for (TreeItem e : f.getItems())
			treeItemCheckAll(list, e, isCheck);
	}

	/**
	 * 得到Tree中text的串值
	 * @param arrs CharSequence[]
	 * @return String
	 */
	public static final String getTreeText(CharSequence... arrs) {
		StringBuilder sb = new StringBuilder(20);
		for (int i = 0; i < arrs.length; i++) {
			if (i > 0) sb.append(ACC_IntervalCharacter);
			sb.append(arrs[i]);
		}
		return sb.toString();
	}

	/**
	 * 得到tree中选中的项目，是否是终极项集合，依据SourceTree中的深度
	 * @param tree SourceTree
	 * @param isUltimate boolean
	 * @return Set&lt;TreeItem&gt;
	 */
	public static final Set<TreeItem> getSourceTreeItem(SourceTree tree, boolean isUltimate) {
		Set<TreeItem> set = UtilsSWTTree.getTreeItemCheckAll(tree);
		if (isUltimate) {
			for (Iterator it = set.iterator(); it.hasNext();) {
				TreeItem e = (TreeItem) it.next();
				//int deep=getTreeItemDeep(e);
				//logger.debug(e.getText()+"\tdeep:"+deep);
				if (getTreeItemDeep(e) != tree.getDeep()) {
					it.remove();//试图删除迭代出来的元素
				}
			}
		}
		return set;
	}

	/**
	 * 得到tree中选中的项目，是否是终极项集合，依据SourceTree中的深度
	 * @param tree SourceTree
	 * @param isUltimate boolean
	 * @return List  &lt;  TreeItem  &gt; 
	 */
	public static final List<TreeItem> getSourceTreeItemList(SourceTree tree, boolean isUltimate) {
		List<TreeItem> list = UtilsSWTTree.getTreeItemCheckAllList(tree);
		if (isUltimate) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				TreeItem e = (TreeItem) it.next();
				if (getTreeItemDeep(e) != tree.getDeep()) {
					it.remove();
				}
			}
		}
		return list;
	}

	/**
	 * 提取所有tree下的内容至输入框 textChar为null时使用分号进行间隔
	 * @param text_key Text
	 * @param textChar String
	 * @param treearrs SourceTree[]
	 */
	public static final void addTreeAllValue(Text text_key, String textChar, SourceTree... treearrs) {
		if (treearrs == null || text_key == null) return;
		text_key.setText("");
		for (SourceTree tree : treearrs) {
			Set<TreeItem> set = UtilsSWTTree.getTreeItemCheckAll(tree);
			for (TreeItem e : set) {
				addTextValue(e, text_key, tree.getDeep(), tree.getValueIndex(), textChar);
			}
		}
	}

	/**
	 * 添加一条信息
	 * @param item TreeItem
	 * @param text_key Text
	 * @param valueDeep int
	 * @param valueIndex int
	 * @param textChar String
	 */
	public static final void addTextValue(TreeItem item, Text text_key, int valueDeep, int valueIndex, String textChar) {
		if (item == null || text_key == null || getTreeItemDeep(item) != valueDeep) return;
		String itemText = item.getText().trim();
		String textvalue = text_key.getText();
		if (textvalue.length() > 0) textvalue += (textChar == null ? ACC_TextChar : textChar);
		String[] arrs = itemText.split(ACC_IntervalSplitCharacter);
		if (arrs.length <= valueIndex) return;
		textvalue += arrs[valueIndex];
		text_key.setText(textvalue);
	}

	/**
	 * 查看TreeItem的深度，0为根
	 * @param item TreeItem
	 * @return int
	 */
	public static final int getTreeItemDeep(TreeItem item) {
		return getTreeItemDeep(item, 0);
	}

	/**
	 * 查看TreeItem的深度，以递归的形式进行检索
	 * @param item TreeItem
	 * @param i int
	 * @return int
	 */
	public static final int getTreeItemDeep(TreeItem item, int i) {
		TreeItem p = item.getParentItem();
		if (p == null) return i;
		return getTreeItemDeep(p, ++i);
	}

	/**
	 * 更改Tree中所有的Check状态
	 * @param isCheck boolean
	 * @param tree Tree
	 */
	public static final void changeCehckTreeItem(boolean isCheck, Tree tree) {
		if (tree == null) return;
		changeCehckTreeItem(isCheck, tree.getItems());
	}

	/**
	 * 更改TreeItem中所有的Check状态
	 * @param isCheck boolean
	 * @param treeItems TreeItem[]
	 */
	public static final void changeCehckTreeItem(boolean isCheck, TreeItem... treeItems) {
		if (treeItems == null || treeItems.length == 0) return;
		for (TreeItem t : treeItems) {
			t.setChecked(isCheck);
			TreeItem[] arrs = t.getItems();
			for (TreeItem e : arrs) {
				changeCehckTreeItem(isCheck, e);
			}
		}
	}

	public static Tree getChildTree(Composite composite) {
		if (composite == null) return null;
		if (composite instanceof Tree) return (Tree) composite;
		Control[] controls = composite.getChildren();
		for (Control e : controls) {
			if (e instanceof Tree) return (Tree) e;
		}
		return null;
	}
}
