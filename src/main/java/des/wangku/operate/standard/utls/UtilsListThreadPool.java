package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsListThreadPool {

	/**
	 * 将一个list均分成n个list,主要通过偏移量来实现的
	 * @param source List&lt;T&gt;
	 * @param n int
	 * @return List&lt;List&lt;T&gt;&gt;
	 */
	public static <T> List<List<T>> averageAssign(List<T> source, int n) {
		List<List<T>> result = new ArrayList<List<T>>();
		int remaider = source.size() % n;  //(先计算出余数)
		int number = source.size() / n;  //然后是商
		int offset = 0;//偏移量
		for (int i = 0; i < n; i++) {
			List<T> value = null;
			if (remaider > 0) {
				value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
				remaider--;
				offset++;
			} else {
				value = source.subList(i * number + offset, (i + 1) * number + offset);
			}
			result.add(value);
		}
		return result;
	}

	/**
	 * 通过list的大小决定使用池的大小
	 * @param listLen int
	 * @param maxThread int
	 * @return int
	 */
	public static int getMaxThreadPoolCount(int listLen, int maxThread) {
		if ((listLen / maxThread) > 2) return maxThread;
		int size = listLen / 2;
		if (size > 1) return size;
		return 1;
	}
}
