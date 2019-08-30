package des.wangku.operate.standard.utls;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 针对数组的操作
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("unchecked")
public class UtilsArrays {
	/**
	 * 把list转成数组
	 * @param type Class&lt;T&gt;泛型类型
	 * @param list List&lt;T&gt;
	 * @return T[]
	 */
	public static final <T> T[] toArray(Class<T> type,List<T> list) {
		T[] arr =  (T[]) Array.newInstance(type, 0);      
		return list.toArray(arr);
	}
	/**
	 * 把对象转成具体的数组
	 * @param obj Object
	 * @param <T> T
	 * @return T[]
	 */
	public static final <T> T[] getArray(Object obj) {
		T[] arrs = null;
		if (obj == null) return arrs;
		if (obj.getClass().isArray()) return arrs;
		int length = Array.getLength(obj);
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < length; i++) {
			list.add((T) Array.get(obj, i));
		}
		return list.toArray(arrs);
	}

	/**
	 * 是否存在过滤字符串
	 * @param key String
	 * @param arr String[]
	 * @return boolean
	 */
	public static final boolean isfilterArr(String key, String[] arr) {
		for (int i = 0; i < arr.length; i++)
			if (key.indexOf(arr[i]) > -1) return true;
		return false;
	}

	/**
	 * 是否存在过滤数值
	 * @param v int
	 * @param arr int[]
	 * @return boolean
	 */
	public static final boolean isfilterArr(int v, int... arr) {
		for (int i = 0; i < arr.length; i++)
			if (v == arr[i]) return true;
		return false;
	}

	/**
	 * 判断多个String是否是相同的Host地址，忽略大小写与协议
	 * @param urlArrs String[]
	 * @return boolean
	 */
	public static final boolean isSameHost(String... urlArrs) {
		URL[] arrs = UtilsConvert.convert(urlArrs);
		return isSameHost(arrs);
	}

	/**
	 * 判断多个URL是否是相同的Host地址，忽略大小写与协议
	 * @param urlArrs URL[]
	 * @return boolean
	 */
	public static final boolean isSameHost(URL... urlArrs) {
		if (urlArrs.length < 2) return false;
		String hostFirst = null;
		for (URL url : urlArrs) {
			String host = url.getHost().toLowerCase();
			if (hostFirst == null) {
				hostFirst = host;
				continue;
			}
			if (!hostFirst.equals(host)) return false;
		}
		return true;
	}
}
