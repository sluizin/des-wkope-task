package des.wangku.operate.standard.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.task.InterfaceThreadRunUnit;

public class ThreadRun implements Runnable, Callable<Boolean> {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(ThreadRun.class);
	List<InterfaceThreadRunUnit> list = new ArrayList<>();
	AbstractTask parent = null;

	public ThreadRun(AbstractTask parent, List<InterfaceThreadRunUnit> list) {
		this.parent = parent;
		this.list = list;
	}

	@Override
	public Boolean call() throws Exception {
		return null;
	}

	@Override
	public void run() {
		if (list == null) return;
		try {
			for (int i = 0, len = list.size(); (!parent.getIsBreak()) && i < len; i++) {
				InterfaceThreadRunUnit e = list.get(i);
				e.run();
				if (e.getSleepTime() > 0 && e.getSleepTime() < 30000) Thread.sleep(e.getSleepTime());
				update(e);
			}
		} catch (InterruptedException ee) {
			throw new RuntimeException(ee);
		}
	}

	/**
	 * 更新。使用异步线程
	 * @param e InterfaceThreadRunUnit
	 */
	private void update(InterfaceThreadRunUnit e) {
		parent.parentDisplay.asyncExec(new Runnable() {
			@Override
			public void run() {
				e.show();
				e.autoCollect();
				parent.collect();
				parent.ThreadRunDialog.setValueAdd(e.getKeyword());
			}
		});
	}

}
