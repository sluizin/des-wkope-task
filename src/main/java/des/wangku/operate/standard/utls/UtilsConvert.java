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
		if (arrs.length > 0) try {
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
}
