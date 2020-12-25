package des.wangku.operate.standard.utls;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 转换
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsConvert {
	/**
	 * 把String[] arrs转成URL[]
	 * @param arrs String[]
	 * @return URL[]
	 */
	public static final URL[] convert(String... arrs) {
		List<URL> list = new ArrayList<>(arrs.length);
		if (arrs.length == 0) return list.stream().toArray(URL[]::new);
		try {
			for (String e : arrs) {
				if (e == null) continue;
				URL url = new URL(e);
				list.add(url);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return list.stream().toArray(URL[]::new);
	}

	/**
	 * 把list字符串转成正整数型数组
	 * @param list List&lt;String&gt;
	 * @return int[]
	 */
	public static final int[] convertIntArray(String... arrs) {
		List<Integer> numList = new ArrayList<>(arrs.length);
		for (String e : arrs)
			if (UtilsVerification.isNumeric(e)) numList.add(Integer.parseInt(e));
		return integerListconvertIntArray(numList);
	}
	/**
	 * 把list字符串转成正负整数型数组
	 * @param list List&lt;String&gt;
	 * @return int[]
	 */
	public static final int[] convertIntegerArray(String... arrs) {
		List<Integer> numList = new ArrayList<>(arrs.length);
		for (String e : arrs)
			if (UtilsVerification.isDigital(e)) {
				e=e.trim();
				numList.add(Integer.parseInt(e));
			}
		return integerListconvertIntArray(numList);
	}

	public static void main(String[] args) {
		String key="6,-17,25, 15, -20,20a";
		String[] arrs=key.split(",");
		int[] arr=convertIntegerArray(arrs);
		for(int t:arr) {
			System.out.println(":"+t);
		}
	}
	/**
	 * 把list字符串转成整数型数组
	 * @param list List&lt;String&gt;
	 * @return int[]
	 */
	public static final int[] convertIntArray(List<String> list) {
		List<Integer> numList = new ArrayList<>(list.size());
		for (String e : list) {
			if (UtilsVerification.isNumeric(e)) {
				numList.add(Integer.parseInt(e));
			}
		}
		return integerListconvertIntArray(numList);
		/*
		 * int[] arr = new int[numList.size()];
		 * for (int i = 0; i < numList.size(); i++) {
		 * arr[i] = numList.get(i);
		 * }
		 * return arr;
		 */
	}

	/**
	 * 把数值对象list转成整数型数组
	 * @param list List&lt;Integer&gt;
	 * @return int[]
	 */
	public static final int[] integerListconvertIntArray(List<Integer> list) {
		return list.stream().mapToInt(Integer::intValue).toArray();
	}
}
