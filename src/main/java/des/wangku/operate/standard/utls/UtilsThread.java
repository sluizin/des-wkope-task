package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 针对线程的工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsThread {
	public static final long defaultSleepTime = 2000L;

	/**
	 * 线程暂停2000毫秒即2秒
	 */
	public static final void ThreadSleep() {
		ThreadSleep(defaultSleepTime);
	}

	/**
	 * 线程暂停多少毫秒
	 * @param sleep long
	 */
	public static final void ThreadSleep(long sleep) {
		if (sleep <= 0L) return;
		try {
			Thread.sleep(sleep);
		} catch (Exception e) {
		}
	}

	/**
	 * 启动多线程
	 * @param workList List&lt;UtilsInterfaceThreadUnit&gt;
	 * @param maxThreadNum int
	 */
	public static final void startThreadWork(List<UtilsInterfaceThreadUnit> workList, int maxThreadNum) {
		if (maxThreadNum <= 0) return;
		List<List<UtilsInterfaceThreadUnit>> list2 = UtilsList.averageAssign(workList, maxThreadNum);
		startThreadWork(list2);
	}

	/**
	 * 启动多线程
	 * @param list List&lt;&lt;UtilsInterfaceThreadUnit&gt;&gt;
	 */
	public static final void startThreadWork(List<List<UtilsInterfaceThreadUnit>> list) {
		int len = list.size();
		if (len == 0) return;
		ThreadPoolExecutor ThreadPool = new ThreadPoolExecutor(len, len, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.CallerRunsPolicy());
		ThreadPool.allowCoreThreadTimeOut(true);
		for (int i = 0; i < len; i++) {
			List<UtilsInterfaceThreadUnit> e = list.get(i);
			UtilsThreadRun task = new UtilsThreadRun(i, e);
			ThreadPool.submit((Runnable) task);
		}  
		ThreadPool.shutdown();
	}

	/**
	 * 自定义线程接口
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public interface UtilsInterfaceThreadUnit {
		/**
		 * 单元在线程中运行
		 */
		public void run();
	}

	private static class UtilsThreadRun implements Runnable, Callable<Boolean> {
		/** 日志 */
		static Logger logger = LoggerFactory.getLogger(UtilsThreadRun.class);
		int TheadNum = 0;
		List<UtilsInterfaceThreadUnit> list = new ArrayList<>();

		UtilsThreadRun(int TheadNum, List<UtilsInterfaceThreadUnit> list) {
			this.TheadNum = TheadNum;
			this.list = list;
		}

		@Override
		public Boolean call() throws Exception {
			return null;
		}

		@Override
		public void run() {
			if (list == null || list.size() == 0) return;
			for (int i = 0, len = list.size(); i < len; i++) {
				UtilsInterfaceThreadUnit e = list.get(i);
				logger.debug(showHead(i + 1));
				e.run();
				logger.debug(showHead(i + 1) + "Completed");
			}
		}

		String showHead(int i) {
			return "Thread[" + TheadNum + "][" + i + "/" + list.size() + "]Run...";
		}

	}
}
