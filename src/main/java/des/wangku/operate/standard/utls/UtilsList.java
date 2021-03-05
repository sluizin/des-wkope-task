package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 对list进行分割，或线程池的分配
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("unchecked")
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
	 * 将一个数组均分成n个list,主要通过偏移量来实现的
	 * @param arrs T[];
	 * @param n int
	 * @param <T> T
	 * @return List&lt;List&lt;T&gt;&gt;
	 */
	public static <T> List<List<T>> averageAssign(T[] arrs, int n) {
		if (arrs.length == 0) return new ArrayList<List<T>>();
		List<T> list = Arrays.asList(arrs);
		return averageAssign(list, n);
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
		List<T> list2 = getOrderListByLenASC(list);
		Collections.reverse(list2);
		return list2;
	}

	/**
	 * 数组list按长度进行正序输出
	 * @param list list&lt;T&gt;
	 * @param <T> T
	 * @return list&lt;T&gt;
	 */
	public static final <T> List<T> getOrderListByLenASC(List<T> list) {
		Collections.sort(list, new SortByLengthComparator<T>());
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
		for (String e : list)
			if (e != null && e.length() > 0) return false;
		return true;
	}

	/**
	 * 去重，不允许null
	 * @param list List&lt;T&gt;
	 */
	public static final <T> List<T> distinct(List<T> list) {
		if (list == null || list.size() < 2) return list;
		return list.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
	}

	/**
	 * 去重，不允许null
	 * @param arr T[]
	 * @return List&lt;T&gt;
	 */
	public static final <T> List<T> distinct(T... arr) {
		return distinct(Arrays.asList(arr));
	}

	/**
	 * 去重，不允许null
	 * @param es Elements
	 * @return Elements
	 */
	public static final Elements distinct(Elements es) {
		if (es == null || es.size() < 2) return es;
		List<Element> list = es;
		return new Elements(distinct(list));
	}

	/**
	 * 去重，不允许null，只针对html内容，跟位置无关
	 * @param es Elements
	 * @return Elements
	 */
	public static final Elements distinctHtml(Elements es) {
		if (es == null || es.size() < 2) return es;
		Elements ess = new Elements();
		loop: for (Element e : es) {
			for (Element f : ess)
				if (f.html().equals(e.html())) continue loop;
			ess.add(e);
		}
		return ess;
	}

	/**
	 * 去重，不允许null，只针对Text内容，跟位置无关
	 * @param es Elements
	 * @return Elements
	 */
	public static final Elements distinctText(Elements es) {
		Elements ess = new Elements();
		if (es == null || es.size() == 0) return ess;
		loop: for (Element e : es) {
			for (Element f : ess) {
				if (f.text().equals(e.text())) continue loop;
			}
			ess.add(e);
		}
		return ess;
	}

	/**
	 * 按指定大小，分隔集合，将集合按规定个数分为n个部分
	 * @param <T>
	 * @param list
	 * @param len
	 * @return
	 */
	public static <T> List<List<T>> splitList(List<T> list, int len) {
		if (list == null || list.isEmpty() || len < 1) return Collections.emptyList();
		List<List<T>> result = new ArrayList<>(len);
		int size = list.size();
		int count = (size + len - 1) / len;
		for (int i = 0; i < count; i++) {
			List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
			result.add(subList);
		}
		return result;
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
		boolean isgo = false;

		StringBuilder sb = new StringBuilder();
		sb.append("<dd><a href='/4.html'>4</a></dd>");
		sb.append("<dd><a href=\"/1.html\">1</a></dd>");
		sb.append("<dd><a href=\"/2.html\">2</a></dd>");
		sb.append("<dd><a href='/3.html'>3</a></dd>");
		sb.append("<dd><a href='/4.html'>4</a></dd>");

		sb.append("<dd><a href='/5.html'>5</a></dd>");
		sb.append("<dd><a href='/6.html'>6</a></dd>");
		sb.append("<dd><a href='/7.html'>7</a></dd>");
		sb.append("<dd><a href='/8.html'>8</a></dd>");
		sb.append("<dd><a href='/9.html'>9</a></dd>");
		sb.append("<dd><a href='/4.html'>4</a></dd>");
		sb.append("<dd><a href='/7.html'>7</a></dd>");
		sb.append("<dd><a href='/4.html'>4</a></dd>");

		Document doc = UtilsJsoupExt.getDocument("http://www.99114.com", sb.toString());
		System.out.println("doc.html():" + doc.html());

		System.out.println("--------------------------------------------------------");
		System.out.println("doc.body:" + doc.body());
		System.out.println("--------------------------------------------------------");
		Elements es = UtilsJsoup.getElementAll(doc, "{T}dd");
		for (Element e : es) {
			System.out.println("[" + es.size() + "]e:" + e.html());
			System.out.println("-----------------------------");
		}
		System.out.println("--------------------------------------------------------" + doc.baseUri());
		Elements ess = UtilsList.distinctHtml(es);
		for (Element e : ess) {
			System.out.println("[" + ess.size() + "]e:" + e.html() + "\thref:" + e.attr("abs:href"));
			Elements as = e.select("a[href]");
			System.out.println("=" + as.first().attr("abs:href"));
			System.out.println("=============================");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("--------------------------------------------------------");
		System.out.println("--------------------------------------------------------");
		System.out.println("--------------------------------------------------------");
		System.out.println("--------------------------------------------------------");
		System.out.println("--------------------------------------------------------");

		if (isgo) {
			String[] arrss = { "http://www.gazww.com/4823.shtml", "aa", "", "http://www.gazww.com/4823.shtml", "", ""

			};
			List<String> aalist = UtilsList.distinct(arrss);
			for (String e : aalist)
				System.out.println("e:" + e);
			List<String> bblist = new ArrayList<>();
			bblist.add("cc");
			bblist.add("http://www.gazww.com/4823.shtml");
			bblist.add("ee");
			bblist.add("http://www.gazww.com/4823.shtml");
			bblist.add("ee");
			List<String> bblist2 = UtilsList.distinct(bblist);
			for (String e : bblist2)
				System.out.println("eee:" + e);

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
	 * @param list List&lt;List&lt;String&gt;&gt;
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
