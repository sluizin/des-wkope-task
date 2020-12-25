package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 集合、聚集类工具
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsCollect {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsCollect.class);

	/**
	 * 分组平均
	 * @param sourcelist List&lt;CollectGroup&gt;
	 * @param num int
	 * @return List&lt;List&lt;CollectGroup&gt;&gt;
	 */
	public static final List<List<CollectGroup>> groupAverage(List<CollectGroup> sourcelist, int num, boolean isDesc) {
		/* 过滤null */
		List<CollectGroup> sourlist = sourcelist.parallelStream().filter(Objects::nonNull).collect(Collectors.toList());
		List<List<CollectGroup>> list = new ArrayList<>(num);
		if (sourlist.size() == 0) return list;
		if (sourlist.size() <= num) {/* 如果列表总数小于等于num，则平分列表 */
			for (CollectGroup e : sourlist) {
				List<CollectGroup> list2 = new ArrayList<>();
				list2.add(e);
				list.add(list2);
			}
			return list;
		}
		Collections.sort(sourlist, new Comparator<CollectGroup>() {
			public int compare(CollectGroup o1, CollectGroup o2) {
				if (o1.number() > o2.number()) return -1;
				if (o1.number() < o2.number()) return 1;
				return 0;
			}
		});
		for(int i=0,len=sourlist.size();i<len && i<6;i++) {
			CollectGroup e=sourlist.get(i);
			System.out.println("max:"+e.toString()+"\t"+e.number());
		}
		for (int i = 0; i < num; i++)
			list.add(new ArrayList<>());/* 先把输出结果格式化 */
		for (CollectGroup e : sourlist) {
			List<CollectGroup> min = min(list);
			min.add(e);
		}
		if(!isDesc) {
			for (List<CollectGroup> f : list) {
			Collections.reverse(f);	
			}
		}
		return list;
	}

	static final List<CollectGroup> min(List<List<CollectGroup>> list) {
		List<CollectGroup> e = null;
		int min = 0;
		for (List<CollectGroup> f : list) {
			int count = 0;
			for (CollectGroup g : f)
				count += g.number();
			if (e == null) {
				e = f;
				min = count;
				continue;
			}
			if (count < min) {
				e = f;
				min = count;
			}
		}
		return e;
	}

	public static void main(String[] args) {
		List<CollectGroup> list = new ArrayList<>();
		list.add(new Z("a", 21));
		list.add(new Z("b", 5));
		list.add(new Z("c", 4));
		list.add(new Z("d", 7));
		list.add(new Z("e", 17));
		list.add(new Z("f", 5));
		list.add(new Z("ff", 15));
		list.add(new Z("p", 16));
		list.add(new Z("o", 13));
		list.add(new Z("g", 9));
		list.add(new Z("h", 2));
		list.add(new Z("i", 12));
		list.add(new Z("j", 5));
		list.add(new Z("k", 18));
		list.add(new Z("l", 1));
		list.add(new Z("m", 8));
		list.add(new Z("n", 9));
		List<List<CollectGroup>> list2 = groupAverage(list, 4, false);
		for (int i = 0; i < list2.size(); i++) {
			List<CollectGroup> list3 = list2.get(i);
			System.out.print(i + ":");
			int count = 0;
			for (CollectGroup e : list3)
				count += e.number();
			System.out.print("[" + count + "]\t");
			for (CollectGroup e : list3) {
				Z z = (Z) e;
				System.out.print(z.name + ":" + z.number() + "\t");
			}
			System.out.println();

		}
	}
	public static final void showList(List<List<CollectGroup>> list) {
		for (int i = 0; i < list.size(); i++) {
			List<CollectGroup> list3 = list.get(i);
			System.out.print(i + ":");
			int count = 0;
			for (CollectGroup e : list3)
				count += e.number();
			System.out.print("[" + count + "]\t");
			for (CollectGroup e : list3) {
				System.out.print(e.toString() + ":" + e.number() + "\t");
			}
			System.out.println();

		}
		
	}
	static class Z implements CollectGroup {
		String name = "";
		int num = 0;

		public Z(String name, int num) {
			this.name = name;
			this.num = num;
		}

		@Override
		public int number() {
			return num;
		}

	}

	/**
	 * 分组集合接口
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public interface CollectGroup {
		/**
		 * 数量，用于分组排序
		 * @return int
		 */
		public int number();
	}
}
