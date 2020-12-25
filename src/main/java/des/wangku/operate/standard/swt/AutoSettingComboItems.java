package des.wangku.operate.standard.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
/**
 * 
 * 自动设置系统<br>
 * 各栏目的设置
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class AutoSettingComboItems {

	/**
	 * {"name":"坐标a","val":[ {"name": "第一方案","val": {"text": [{"no": 1,"text": "金银花"}]}}]}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class Project {
		String name = "";
		List<Plan> val = new ArrayList<>();

		public Project() {

		}

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final List<Plan> getVal() {
			return val;
		}

		public final void setVal(List<Plan> val) {
			this.val = val;
		}

	}

	/**
	 * {"name": "第一方案","val": {"text": [{"no": 1,"text": "金银花"}]}}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class Plan {
		String name = "";
		Item val = new Item();

		public Plan() {

		}

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final Item getVal() {
			return val;
		}

		public final void setVal(Item val) {
			this.val = val;
		}

	}

	/**
	 * 		{"text": [
	 * 					{"no": 1,"text": "金银花"}
	 * 				]
	 * 		,
	 * 		"button": [
	 * 					{"no": 1,"selected": true}
	 * 					]
	 * 		}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class Item {
		public Item() {

		}

		List<ItemText> text = new ArrayList<>();
		List<ItemButton> button=new ArrayList<>();
		List<ItemSpinner> spinner=new ArrayList<>();

		@SuppressWarnings("unchecked")
		static final <T>List<T> getList(Control[] controls,Class<T> t){
			List<T> list=new ArrayList<>(controls.length);
			for(Control c:controls) {
				if(c.getClass().equals(t)) {
					list.add((T)c);
				}
			}
			return list;
		}
		/**
		 * 向control中设置值
		 * @param controls Control[]
		 */
		public void setControlVal(Control[] controls) {
			if(controls==null || controls.length==0)return;
			{
				List<Text> list=getList(controls,Text.class);
				for(int i=0,len=list.size();i<len;i++) {
					ItemText it=getItemText(i);
					if(it==null)continue;
					it.addValue(list.get(i));
					//ItemText.addValue(list.get(i), getItemText(i));
					
				}
			}
			{
				List<Button> list=getList(controls,Button.class);
				for(int i=0,len=list.size();i<len;i++) {
					ItemButton ib=getItemButton(i);
					if(ib==null)continue;
					ib.addValue(list.get(i));
				}
			}
			{
				List<Spinner> list=getList(controls,Spinner.class);
				for(int i=0,len=list.size();i<len;i++) {
					ItemSpinner ib=getItemSpinner(i);
					if(ib==null)continue;
					ib.addValue(list.get(i));
				}
			}
		}
		/**
		 * 向control中设置值
		 * @param controls Control[]
		 */
		public void setControlVal2(Control[] controls) {
			/*
			int pointText = 0;
			int pointButton = 0;
			for (int i = 0; i < controls.length; i++) {
				Control c = controls[i];
				if(ItemText.addValue(c, getItemText(pointText))) {
					pointText++;
					continue;
				}
				if(ItemButton.addValue(c, getItemButton(pointButton))) {
					pointButton++;
					continue;
				}
				*/
				/*
				if (c instanceof Text) {
					Text text = (Text) c;
					ItemText val = getItemText(pointText++);
					if (val != null) {
						text.setText(val.text);
					}
					continue;
				}
				if(c instanceof Button) {
					Button button = (Button) c;
					ItemButton val = getItemButton(pointButton++);
					if (val != null) {
						button.setSelection(val.selected);
						if(val.text!=null)button.setText(val.text);
					}
					continue;
				}
			}
				*/

		}
		/**
		 * 得到某个下标的值，如果没有找到，则返回null
		 * @param no int
		 * @return ItemText
		 */
		public ItemText getItemText(int no) {
			for (ItemText e : text) {
				if (e.no == no) return e;
			}
			return null;
		}

		/**
		 * 得到某个下标的值，如果没有找到，则返回null
		 * @param no int
		 * @return ItemButton
		 */
		public ItemButton getItemButton(int no) {
			for (ItemButton e : button) {
				if (e.no == no) return e;
			}
			return null;			
		}
		
		/**
		 * 得到某个下标的值，如果没有找到，则返回null
		 * @param no int
		 * @return ItemButton
		 */
		public ItemSpinner getItemSpinner(int no) {
			for (ItemSpinner e : spinner) {
				if (e.no == no) return e;
			}
			return null;			
		}
		
		
		public final List<ItemText> getText() {
			return text;
		}

		public final void setText(List<ItemText> text) {
			this.text = text;
		}

		public final List<ItemButton> getButton() {
			return button;
		}

		public final void setButton(List<ItemButton> button) {
			this.button = button;
		}
		public final List<ItemSpinner> getSpinner() {
			return spinner;
		}
		public final void setSpinner(List<ItemSpinner> spinner) {
			this.spinner = spinner;
		}
		
		

	}
	/**
	 * {"no": 1,"text": "金银花"}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ItemText{
		int no = -1;
		String text = null;

		public ItemText() {

		}

		public final int getNo() {
			return no;
		}

		public final void setNo(int no) {
			this.no = no;
		}

		public final String getText() {
			return text;
		}

		public final void setText(String text) {
			this.text = text;
		}

		boolean addValue(Text c) {
			if(c==null)return false;
			c.setText(text);
			return true;
		}
		@Deprecated
		static boolean addValue(Text c,ItemText val) {
			if(c==null || val==null)return false;
			c.setText(val.text);
			return true;
		}

	}
	/**
	 * {"no": 1,"selected": true}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ItemButton{
		int no=-1;
		boolean selected;
		String text=null;
		public ItemButton() {

		}
		public final int getNo() {
			return no;
		}
		public final void setNo(int no) {
			this.no = no;
		}
		public final boolean isSelected() {
			return selected;
		}
		public final void setSelected(boolean selected) {
			this.selected = selected;
		}
		public final String getText() {
			return text;
		}
		public final void setText(String text) {
			this.text = text;
		}

		boolean addValue(Button c) {
			if(c==null)return false;
			c.setSelection(selected);
			if(text!=null)c.setText(text);
			return true;
			
		}
		@Deprecated
		static boolean addValue(Button c,ItemButton val) {
			if(c==null || val==null)return false;
			c.setSelection(val.selected);
			if(val.text!=null)c.setText(val.text);
			return true;
		}
		
	}

	/**
	 * {"no": 1,"select":2}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ItemSpinner{
		int no = -1;
		int select=0;

		public ItemSpinner() {

		}

		public final int getNo() {
			return no;
		}

		public final void setNo(int no) {
			this.no = no;
		}


		public final int getSelect() {
			return select;
		}

		public final void setSelect(int select) {
			this.select = select;
		}

		boolean addValue(Spinner c) {
			if(c==null)return false;
			c.setSelection(select);
			return true;
		}

	}
}
