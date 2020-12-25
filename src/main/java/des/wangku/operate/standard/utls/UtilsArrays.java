package des.wangku.operate.standard.utls;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static des.wangku.operate.standard.utls.UtilsJsoup.ACC_JsoupRulePrecisePositioningInPage;
import static des.wangku.operate.standard.utls.UtilsJsoup.ACC_JsoupRuleInterval;
import static des.wangku.operate.standard.utls.UtilsJsoup.ACC_JsoupRuleIntervalSplit;
import static des.wangku.operate.standard.utls.UtilsJsoup.ACC_JsoupRulePrecisePositioningOutPage;

/**
 * 针对数组的操作
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("unchecked")
public class UtilsArrays {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsArrays.class);

	/**
	 * 合并数组，并过滤重复，并从小到大排序
	 * @param arr int[][]
	 * @return int[]
	 */
	public static final int[] mergedistinct(int[]... arr) {
		int len = 0;
		for (int[] e : arr)
			len += e.length;
		int[] result = new int[len];
		int p = 0;
		for (int[] e : arr) {
			loop: for (int i = 0; i < e.length; i++) {
				int v = e[i];
				for (int x = 0; x < p; x++) {
					if (result[x] == v) continue loop;
				}
				result[p++] = v;
			}
		}
		if (p == 0) return new int[0];
		int[] re = new int[p];
		System.arraycopy(result, 0, re, 0, p);
		Arrays.sort(re);
		return re;
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr int[]
	 * @return int[]
	 */
	public static final int[] desc(int... arr) {
		Arrays.sort(arr);
		return reverse(arr);
	}

	/**
	 * 对数组进行逆序
	 * @param arr int[]
	 * @return int[]
	 */
	public static final int[] reverse(int... arr) {
		int temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr byte[]
	 * @return byte[]
	 */
	public static final byte[] desc(byte... arr) {
		Arrays.sort(arr);
		return reverse(arr);
	}

	/**
	 * 对数组进行逆序
	 * @param arr byte[]
	 * @return byte[]
	 */
	public static final byte[] reverse(byte... arr) {
		byte temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr short[]
	 * @return short[]
	 */
	public static final short[] desc(short... arr) {
		Arrays.sort(arr);
		return reverse(arr);
	}

	/**
	 * 对数组进行逆序
	 * @param arr short[]
	 * @return short[]
	 */
	public static final short[] reverse(short... arr) {
		short temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr long[]
	 * @return long[]
	 */
	public static final long[] desc(long... arr) {
		Arrays.sort(arr);
		return reverse(arr);
	}

	/**
	 * 对数组进行逆序
	 * @param arr long[]
	 * @return long[]
	 */
	public static final long[] reverse(long... arr) {
		long temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr float[]
	 * @return float[]
	 */
	public static final float[] desc(float... arr) {
		Arrays.sort(arr);
		return reverse(arr);
	}

	/**
	 * 对数组进行逆序
	 * @param arr float[]
	 * @return float[]
	 */
	public static final float[] reverse(float... arr) {
		float temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr double[]
	 * @return double[]
	 */
	public static final double[] desc(double... arr) {
		Arrays.sort(arr);
		return reverse(arr);
	}

	/**
	 * 对数组进行逆序
	 * @param arr double[]
	 * @return double[]
	 */
	public static final double[] reverse(double... arr) {
		double temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	public static final boolean[] asc(boolean... arr) {
		return booleanSort(false, arr);

	}

	public static final boolean[] desc(boolean... arr) {
		return booleanSort(true, arr);
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr boolean[]
	 * @return boolean[]
	 */
	private static final boolean[] booleanSort(boolean first, boolean... arr) {
		int len = arr.length;
		loop: for (int i = 0, len2 = len - 1; i < len2; i++) {
			if (first == arr[i]) continue;
			for (int ii = i + 1; ii < len; ii++) {
				if (first == arr[ii]) {
					arr[i] = first;
					arr[ii] = !first;
					continue loop;
				}
			}
			break;
		}
		return arr;
	}

	/**
	 * 对数组进行逆序
	 * @param arr boolean[]
	 * @return boolean[]
	 */
	public static boolean[] reverse(boolean... arr) {
		boolean temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr char[]
	 * @return char[]
	 */
	public static char[] desc(char... arr) {
		Arrays.sort(arr);
		return reverse(arr);
	}

	/**
	 * 对数组进行逆序
	 * @param arr char[]
	 * @return char[]
	 */
	public static final char[] reverse(char... arr) {
		char temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	/**
	 * 对数组进行倒序排序
	 * @param arr T[]
	 * @return T[]
	 */
	public static final <T> T[] descObj(T... arr) {
		Arrays.sort(arr);
		return reverse(arr);
	}

	/**
	 * 对数组进行逆序
	 * @param arr T[]
	 * @return T[]
	 */
	public static final <T> T[] reverse(T... arr) {
		T temp;
		for (int i = 0, to, len = arr.length, half = len / 2; i < half; i++) {
			temp = arr[i];
			to = len - i - 1;
			arr[i] = arr[to];
			arr[to] = temp;
		}
		return arr;
	}

	public static void main11(String[] args) {
		for (int e : desc(3, 1, 7, 6))
			System.out.print(e + "\t");
		System.out.println();
		for (String e : descObj("b", "e", "a", "ff"))
			System.out.print(e + "\t");
		System.out.println();
		for (Float e : desc(10.2f, 20f, 6f, 8.5f))
			System.out.print(e + "\t");
		System.out.println();
		boolean[] a = { false, true, false, false, true, false };
		for (boolean e : a)
			System.out.print(e + "\t");
		System.out.println();
		for (boolean e : desc(a))
			System.out.print(e + "\t");
		System.out.println();
		for (boolean e : asc(a))
			System.out.print(e + "\t");
		System.out.println();
		for (int f : desc(UtilsRnd.getRndIntArray(6, 5, 90)))
			System.out.print(f + "\t");
		/*
		 * int[][] arr = { { 2, 4, 12, 7 }, { 3, 5, 12 }, { 1, 11, 7, 15 }
		 * };
		 * int[] result = mergedistinct(arr);
		 * for (int i = 0; i < result.length; i++)
		 * System.out.print(":" + result[i] + "\t");
		 */
	}

	/**
	 * 多个数组合并成一个数组
	 * @param arr int[][]
	 * @return int[]
	 */
	public static final int[] merge333(int[]... arr) {
		int len = 0;
		for (int[] e : arr)
			len += e.length;
		int[] result = new int[len];
		int i = 0;
		for (int[] e : arr)
			for (int f : e)
				result[i++] = f;
		return result;
	}

	/**
	 * 多个数组合并成一个数组
	 * @param type Class&lt;T&gt;泛型类型
	 * @param arr T[]
	 * @return T[]
	 */
	public static final <T> T[] merge(Class<T> type, T[]... arr) {
		int len = 0;
		for (T[] e : arr)
			len += e.length;
		T[] result = (T[]) Array.newInstance(type, len);
		int i = 0;
		for (T[] e : arr)
			for (T f : e)
				result[i++] = f;
		return result;
	}

	/**
	 * 把list转成数组
	 * @param type Class&lt;T&gt;泛型类型
	 * @param list List&lt;T&gt;
	 * @return T[]
	 */
	public static final <T> T[] toArray(Class<T> type, List<T> list) {
		T[] arr = (T[]) Array.newInstance(type, 0);
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
	 * 把a,0,2,d,8字符串转成int[]数组
	 * @param str String
	 * @return int[]
	 */
	public static int[] getArrayInteger(String str) {
		int[] arr = {};
		if (str == null || str.length() == 0) return arr;
		return UtilsConvert.convertIntArray(str.split(","));
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
	 * 是否存在字符串，允许null判断
	 * @param key String
	 * @param arr String[]
	 * @return boolean
	 */
	public static final boolean isExist(String key, String[] arr) {
		return isExistIndex(key, arr) > -1;
	}

	/**
	 * 是否存在字符串，允许null判断，并返回下标，如果没有找到，则返回-1
	 * @param key String
	 * @param arr String[]
	 * @return int
	 */
	public static final int isExistIndex(String key, String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			String e = arr[i];
			if ((key == null && e == null) || (key != null && key.equals(e))) return i;
		}
		return -1;
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

	/**
	 * 判断下标有效，不区别行与列，判断大于0
	 * @param arrs int[]
	 * @return boolean
	 */
	public static final boolean isSuffixValid(int... arrs) {
		for (int i : arrs)
			if (i < 0) return false;
		return true;
	}

	/**
	 * 过滤数组中空值，包括null或""空字符串
	 * @param arr String[]
	 * @return String[]
	 */
	public static final String[] getFilterNullBlankValue(String... arr) {
		return getFilterNullBlankValue(true, true, arr);
	}

	/**
	 * 过滤数组中空值null
	 * @param arr String[]
	 * @return String[]
	 */
	public static final String[] getFilterNullValue(String... arr) {
		return getFilterNullBlankValue(false, true, arr);
	}

	/**
	 * 过滤数组中空值，包括null或""空字符串
	 * @param isNull boolean
	 * @param isBlank boolean
	 * @param arr String[]
	 * @return String[]
	 */
	public static final String[] getFilterNullBlankValue(boolean isNull, boolean isBlank, String... arr) {
		if (arr.length == 0) return new String[0];
		List<String> list = new ArrayList<>(arr.length);
		for (String e : arr) {
			if (e == null) {
				if (isNull) list.add(e);
				continue;
			}
			if (e.length() == 0) {
				if (isBlank) list.add(e);
				continue;
			}
			list.add(e);
		}
		String[] arrs = {};
		return list.toArray(arrs);
	}

	/**
	 * 把规则关键字放入数组中
	 * @param arr String[]
	 * @return String[]
	 */
	@Deprecated
	public static final String[] getJsoupRuleKeyDeprecated(String... arr) {
		String[] arrs = {};
		List<String> list = new ArrayList<>();
		for (String e : arr) {
			if (e == null || e.length() == 0) continue;
			if (e.indexOf("||") > -1) {
				String[] ar = e.split("\\|\\|");
				jsoupRuleSingleKeyDeprecated(list, ar);
				continue;
			}
			jsoupRuleSingleKeyDeprecated(list, e);
		}
		list = UtilsList.distinct(list);
		return list.toArray(arrs);
	}

	/**
	 * 把规则关键字放入数组中
	 * @param arr String[]
	 * @return String[]
	 */
	public static final String[] getJsoupRuleKey(String... arr) {
		String[] arrs = {};
		List<String> list = new ArrayList<>();
		for (String e : arr) {
			if (e == null || e.length() == 0) continue;
			jsoupRuleSingleKey(list, e);
		}
		list = UtilsList.distinct(list);
		return list.toArray(arrs);
	}
	/**
	 * 把规则关键字放入list中，是最终数组，已经完成分组
	 * @param list List&lt;String&gt;
	 * @param key String
	 * @param arr String[]
	 */
	private static final void jsoupRuleSingleKey(List<String> list, String key) {
			if (key == null) return;
			key=key.trim();
			if (key.length() == 0) return;
			if (key.indexOf(ACC_JsoupRuleInterval) > -1) {
				String[] arr = key.split(ACC_JsoupRuleIntervalSplit);
				for(String e:arr)jsoupRuleSingleKey(list,e);
				return;
			}
			if (key.indexOf(ACC_JsoupRulePrecisePositioningOutPage) > -1) {
				list.add(key);
				return;
			}
			if (key.indexOf(ACC_JsoupRulePrecisePositioningInPage) > -1) {
				list.add(key);
				return;
			}
			if (key.indexOf("||") > -1) {
				String[] arr = key.split("\\|\\|");
				for(String f:arr)list.add(f);
				return;
			}
			if (key.indexOf("|") > -1) {
				String[] arr = key.split("\\|");
				for(String f:arr)list.add(f);
				return;
			}
			String[] arrs = key.split("[\\|;,]+");
			for (String e : arrs) {
				if (e == null) continue;
				e = e.trim();
				if (e.length() == 0) continue;
				list.add(e);
			}
	}
	/**
	 * 把规则关键字放入list中
	 * @param list List&lt;String&gt;
	 * @param arr String[]
	 */
	@Deprecated
	private static final void jsoupRuleSingleKeyDeprecated(List<String> list, String... arr) {
		for (String e : arr) {
			if (e == null || e.length() == 0) continue;
			if (e.indexOf('|') > -1) {
				list.add(e);
				continue;
			}
			if (e.indexOf(ACC_JsoupRulePrecisePositioningInPage) > -1) {
				list.add(e);
				continue;
			}
			String[] arrs = e.split(";");
			for (String f : arrs) {
				String[] ar = f.split(",");
				for (String g : ar) {
					if (g == null || g.length() == 0) continue;
					g = g.trim();
					if (g.length() == 0) continue;
					list.add(g);
				}
			}
		}
	}

	/**
	 * UtilsJsoup工具读取多个关键时，只支持关键字，以分号与逗号与空格进行分格 ","/";"/" "
	 * @param arr String[]
	 * @return String[]
	 */
	public static final String[] jsoupSingleKeyArray(String... arr) {
		List<String> list = new ArrayList<>();
		for (String e : arr) {
			if (e == null || (e = e.trim()).length() == 0) continue;
			String[] arrs = e.split("[,; ]");
			for (String f : arrs) {
				if (f == null || (f = f.trim()).length() == 0) continue;
				list.add(f);
			}
		}
		String[] arrs = {};
		if (list.size() == 0) return arrs;
		return list.toArray(arrs);
	}
	/**
	 * 判断数值数组的状态 <br>
	 * 1:所有数值正数与零<br>
	 * 0:有正数与负数与零<br>
	 * -1:所有数值为负数<br>
	 * @param arr int[]
	 * @return int
	 */
	public static final int arrayIsState(int...arr) {
		int len=arr.length;
		int count=0;
		for(int e:arr)
			if(e>=0)count++;
		if(count==0)return -1;
		if(count>0 && count<len)return 0;
		return 1;
		
	}

	/**
	 * 整理数组，过滤null或过滤两侧空格且长度为0的单元
	 * @param type Class
	 * @param arr T[]
	 * @return T[]
	 */
	public static final <T> T[] arrangement(Class<T> type, T... arr) {
		T[] ar = (T[]) Array.newInstance(type, 0);
		if(type==null || arr.length==0)return ar;
		List<T> list = new ArrayList<>(ar.length);
		for (T t : arr) {
			if (t == null) continue;
			if (t.toString().trim().length() == 0) continue;
			list.add(t);
		}
		return list.toArray(ar);
	}

	public static void main(String[] args) {
		String content="abc,def|xx;gg|tO|ee|To|cc|TTO|ee|ToO|eee";
		//String[] arrs=content.split("[\\|;,]+");
		String[] arrs=content.split("\\|[Tt]{1}[oO]{1}\\|");
		for(String e:arrs) {
			System.out.println( ":" + e );
		}
		System.out.println( ":" + content.indexOf("|to|") );
		
		/*
		String[] arr = { "abc", "deg ", null, "", " xxx", "null ", "    ", null, " dd " };
		String[] ar = arrangement(String.class, arr);
		for (int i = 0, len = ar.length; i < len; i++) {
			System.out.println(i + ":(" + ar[i] + ")");
		}
	*/	
	}
}
