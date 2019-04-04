package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 对list进行分割，或线程池的分配
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsList {

	/**
	 * 将一个list均分成n个list,主要通过偏移量来实现的
	 * @param source List&lt;T&gt;
	 * @param n int
	 * @param <T> T
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

	/**
	 * 数组list按长度进行倒序输出
	 * @param list list&lt;T&gt;
	 * @param <T> T
	 * @return list&lt;T&gt;
	 */
	public static final <T> List<T> getOrderListByLenDESC(List<T> list) {
		Collections.sort(list, new SortByLengthComparator<T>());
		Collections.reverse(list);
		return list;
	}

	/**
	 * 按长度排序对象
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 * @param <T>
	 */
	private static final class SortByLengthComparator<T> implements Comparator<T> {
		@Override
		public int compare(T var1, T var2) {
			if (var1 == null) {
				if (var2 == null) return 0;
				return -1;
			}
			if (var2 == null) return 1;
			if (var1.toString().length() > var2.toString().length()) {
				return 1;
			} else if (var1.toString().length() == var2.toString().length()) {
				return 0;
			} else {
				return -1;

			}
		}
	}

	/**
	 * 判断数组是否含有值，如为null或为空，则返回true
	 * @param list List&lt;String&gt;
	 * @return boolean
	 */
	public static final boolean isBlankLines(List<String> list) {
		for (String e : list) {
			if (e != null && e.length() > 0) return false;
		}
		return true;
	}

	/**
	 * 沉淀二维list中的指定区域
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param x0 int
	 * @param y0 int
	 * @param x1 int
	 * @param y1 int
	 */
	public static final void setPrecipitateList(List<List<String>> list, int x0, int y0, int x1, int y1) {
		for (int y = y0; y <= y1; y++) {
			loop1: for (int x = x1; x >= x0; x--) {
				String e = listArrayGet(list, x, y);//list.get(x).get(y);
				if (e != null && e.length() > 0) continue;
				/* 发现空值，同列顶部沉淀 */
				for (int i = x - 1; i >= x0; i--) {
					String f = listArrayGet(list, i, y);//list.get(i).get(y);
					if (f != null && f.length() > 0) {
						listArrayExchange(list, x, y, i, y);
						continue loop1;
					}
				}

			}

		}

	}

	/**
	 * 上浮二维list中的指定区域
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param x0 int
	 * @param y0 int
	 * @param x1 int
	 * @param y1 int
	 */
	public static final void setGoUPList(List<List<String>> list, int x0, int y0, int x1, int y1) {
		for (int y = y0; y <= y1; y++) {
			loop1: for (int x = x0; x <= x1; x++) {
				String e = listArrayGet(list, x, y);//list.get(x).get(y);
				if (e != null && e.length() > 0) continue;
				/* 发现空值，同列底部向上浮 */
				for (int i = x + 1; i <= x1; i++) {
					String f = listArrayGet(list, i, y);//list.get(i).get(y);
					if (f != null && f.length() > 0) {
						listArrayExchange(list, x, y, i, y);
						continue loop1;
					}
				}
			}
		}
	}

	/**
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param x1 int
	 * @param y1 int
	 * @param x2 int
	 * @param y2 int
	 */
	public static final void listArrayExchange(List<List<String>> list, int x1, int y1, int x2, int y2) {
		String f = listArrayGet(list, x1, y1);
		String g = listArrayGet(list, x2, y2);
		listArraySet(list, x1, y1, g);
		listArraySet(list, x2, y2, f);
	}

	/**
	 * 设置二维list中某个位置的值
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param x int
	 * @param y int
	 * @param e String
	 */
	public static final void listArraySet(List<List<String>> list, int x, int y, String e) {
		if (list == null) return;
		List<String> l = list.get(x);
		if (l == null) return;
		l.set(y, e);
	}

	/**
	 * 得到二维list中某个位置的值
	 * @param list List&lt;List&lt;String&gt;&gt;
	 * @param x int
	 * @param y int
	 * @return String
	 */
	public static final String listArrayGet(List<List<String>> list, int x, int y) {
		if (list == null) return null;
		if (list.size() == 0) return null;
		List<String> l = list.get(x);
		if (l == null) return null;
		return l.get(y);

	}

	public static void main(String[] args) {
		List<List<String>> list = new ArrayList<>();
		{
			List<String> l = new ArrayList<>();
			String[] arr = { "a", "b", "c", "d", "e", "f" };
			Collections.addAll(l, arr);
			list.add(l);
		}
		{
			List<String> l = new ArrayList<>();
			String[] arr = { "a1", "b1", "", "d1", "", "f1" };
			Collections.addAll(l, arr);
			list.add(l);
		}
		{
			List<String> l = new ArrayList<>();
			String[] arr = { "a2", "", "", "", "e2", "" };
			Collections.addAll(l, arr);
			list.add(l);
		}
		{
			List<String> l = new ArrayList<>();
			String[] arr = { "", "b3", "c3", "d3", "", "f3" };
			Collections.addAll(l, arr);
			list.add(l);
		}
		{
			List<String> l = new ArrayList<>();
			String[] arr = { "a4", "b4", "", "d4", "", "f4" };
			Collections.addAll(l, arr);
			list.add(l);
		}
		show(list);
		setGoUPList(list, 1, 1, 4, 4);
		show(list);
		setPrecipitateList(list, 1, 1, 4, 4);
		show(list);

	}

	static final void show(List<List<String>> list) {
		for (List<String> li : list) {
			for (String e : li) {
				System.out.print(e + "\t");
			}
			System.out.println();
		}
		System.out.println("==================================================");
	}
	/**
	 * 对二维list进行判断，是否出现不行列的行,如果所有行同样数量的列，则返回True
	 * @param list  List&lt;List&lt;String&gt;&gt;
	 * @return boolean
	 */
	public static final boolean testing(List<List<String>> list) {
		int len = -1;
		for (List<String> e : list) {
			if (len == -1) len = e.size();
			if (e.size() != len) return false;
		}
		if (len == -1) return false;
		return true;
	}

}
