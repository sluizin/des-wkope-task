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
public class UtilsConvert {
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
	 * 把list字符串转成整数型数组
	 * @param list List&lt;String&gt;
	 * @return int[]
	 */
	public static final int[] convertIntArray(List<String> list) {
		List<Integer> numList = new ArrayList<>(list.size());
		for (String e : list) {
			if (UtilsVerification.isDigital(e)) {
				numList.add(Integer.parseInt(e));
			}
		}
		int[] arr = new int[numList.size()];
		for (int i = 0; i < numList.size(); i++) {
			arr[i] = numList.get(i);
		}
		return arr;
	}
}
