package des.wangku.operate.standard.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.task.InterfaceThreadRunUnit;
/**
 * 工作线程
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public class WorkThreadRun implements Runnable, Callable<Boolean> {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(WorkThreadRun.class);
	List<InterfaceThreadRunUnit> list = new ArrayList<>();
	AbstractTask parent = null;

	public WorkThreadRun(AbstractTask parent, List<InterfaceThreadRunUnit> list) {
		this.parent = parent;
		this.list = list;
	}
	@Override
	public Boolean call() throws Exception {
		return null;
	}

	@Override
	public void run() {	
		for (int i = 0, len = list.size(); (!parent.getIsBreak()) && i < len; i++) {
			InterfaceThreadRunUnit e = list.get(i);
			e.run();
			parent.parentDisplay.asyncExec(new Runnable() {
				@Override
				public void run() {
					e.show();
					parent.collect();
					parent.ThreadRunDialog.setValueAdd(e.getKeyword());
				}
			});
		}
	}

}
