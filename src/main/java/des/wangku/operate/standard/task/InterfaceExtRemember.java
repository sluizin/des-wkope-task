package des.wangku.operate.standard.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import des.wangku.operate.standard.utls.UtilsDate;

/**
 * 扩展，针对历史记录记忆
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceExtRemember extends InterfaceProjectFile {
	/**
	 * 读取同项目的rem文件
	 * @return
	 */
	public default File getProRemFile() {
		return mkModelFile("rem");
	}

	/**
	 * 添加到记忆文件中
	 * @param line String
	 */
	public default void addRemLine(String line) {
		if (line == null) return;
		File f = getProRemFile();
		if (f == null) return;
		try (FileWriter fw = new FileWriter(f, true); BufferedWriter bw = new BufferedWriter(fw);) {
			bw.write(line);
			bw.newLine();
			bw.flush();
		} catch (Exception ff) {
			logger.debug(ff.getMessage());
		}
	}

	/**
	 * 通过id得到历史记录
	 * @param id String
	 * @return String[]
	 */
	public default String[] getRemValueArray(String id) {
		List<String> list = getRemDistinctValue(id);
		String[] arr = {};
		if (list.size() == 0) return arr;
		return list.toArray(arr);
	}
	/**
	 * 通过id得到历史记录
	 * @param id String
	 * @return String[]
	 */
	public default String[] getRemMultiValueArray(String id) {
		List<String> list = getRemMultiValue(id);
		String[] arr = {};
		if (list.size() == 0) return arr;
		return list.toArray(arr);
	}	
	/**
	 * 通过id得到历史记录
	 * @param id String
	 * @return List&lt;String&gt;
	 */
	public default List<String> getRemDistinctValue(String id) {
		if (id == null) return new ArrayList<>();
		List<RemClass> list=getRemObject(id);
		List<String> list2=list.stream().map(t -> t.val).filter(Objects::nonNull).distinct().collect(Collectors.toList());
		return list2;
	}
	/**
	 * 通过id得到历史记录
	 * @param id String
	 * @return List&lt;String&gt;
	 */
	public default List<String> getRemMultiValue(String id) {
		if (id == null) return new ArrayList<>();
		List<RemClass> list2=getRemObject(id);
		List<String> list=new ArrayList<>(list2.size());
		for(RemClass e:list2) {
			list.add(e.val+"   ["+e.date+"]");
		}
		return list;
	}
	/**
	 * 通过id得到历史记录 可能含有重复项
	 * @param id String
	 * @return List&lt;RemClass&gt;
	 */
	public default List<RemClass> getRemObject(String id) {
		List<RemClass> list=new ArrayList<>();
		if (id == null) return list;
		File f = getProRemFile();
		if (f == null) return list;
		try (FileReader fr = new FileReader(f); BufferedReader bf = new BufferedReader(fr);) {
			String line;
			while ((line = bf.readLine()) != null) {
				if (line.length() == 0) continue;
				if(!JSON.isValidObject(line))continue;
				RemClass g = JSON.parseObject(line, RemClass.class);
				if (g == null || g.val == null || g.val.length() == 0) continue;
				if (id.equals(g.id)) list.add(g);
			}
			Collections.sort(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 记忆对象
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class RemClass implements Serializable,Comparable<RemClass> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 10000L;
		String date = UtilsDate.getDateTimeNow();
		String id = null;
		String val = null;

		public RemClass() {

		}

		public RemClass(String id, String val) {
			this.id = id;
			this.val = val;
		}

		public final String getDate() {
			return date;
		}

		public final void setDate(String date) {
			this.date = date;
		}

		public final String getId() {
			return id;
		}

		public final void setId(String id) {
			this.id = id;
		}

		public final String getVal() {
			return val;
		}

		public final void setVal(String val) {
			this.val = val;
		}

		@Override
		public String toString() {
			return "RemClass [date=" + date + ", id=" + id + ", val=" + val + "]";
		}

		@Override
		public int compareTo(RemClass arg0) {
			if(arg0.id.equals(id)) {
				return val.compareTo(arg0.val);
			}else {
				return id.compareTo(arg0.id);
			}
		}

	}


}
