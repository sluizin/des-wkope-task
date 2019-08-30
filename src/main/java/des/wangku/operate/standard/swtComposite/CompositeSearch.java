package des.wangku.operate.standard.swtComposite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
/**
 * 从Composite中检索对象
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class CompositeSearch {
	/**
	 * 从Composite中提取所有Spinner
	 * @param base Composite
	 * @return List&lt;Spinner&gt;
	 */
	public static final List<Spinner> searchSpinner(Composite base) {
		List<Spinner> list=new ArrayList<>();
		searchSpinner(list,base);
		return list;
	}
	private static final void searchSpinner(List<Spinner> list,Composite c) {
		if(c instanceof org.eclipse.swt.widgets.Spinner) {
			Spinner e=(Spinner)c;
			list.add(e);
		}
		
	}
}
