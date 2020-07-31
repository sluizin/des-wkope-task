package des.wangku.operate.standard.subengineering.reediting;

import java.util.ArrayList;
import java.util.List;

/**
 * 段落再编辑
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Paragraph {
	public static final String getResetPara(List<paraClass> list,int i) {
		if(list.size() %2==1)return "";
		StringBuilder sb=new StringBuilder();
		int len=list.size();
		int half=len/2;
		int a1=0,a2=0,a3=0;
		if(i>half) {
			a1=i-half-1;
		}else {
			a1=i+half-1;		
		}
		a2=(i % 2==0)?i+1:i-1;
		
		a3=len-(i+1);
		sb.append(list.get(a1).first());
		sb.append(list.get(a2).center());
		sb.append(list.get(a3).end());
		return sb.toString();
	}
	public static void main(String[] args) 
	{
		List<paraClass> list=new ArrayList<>();
		list.add(new Ti("1","2","3"));
		list.add(new Ti("a","b","c"));
		list.add(new Ti("A","B","C"));
		list.add(new Ti("X","Y","Z"));
		list.add(new Ti("7","8","9"));
		list.add(new Ti("x","y","z"));
		for(int i=0;i<list.size();i++)
		System.out.println(i+"=="+getResetPara(list,i));
	}
	public static final class Ti implements paraClass{
		String a1,a2,a3;
		public Ti(String a1,String a2,String a3) {
			this.a1=a1;
			this.a2=a2;
			this.a3=a3;
		}
		@Override
		public String first() {
			return a1;
		}

		@Override
		public String center() {
			return a2;
		}

		@Override
		public String end() {
			return a3;
		}
		
	}
	/**
	 * 段落接口，如前段，中间段，后段
	 * 
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static interface paraClass{
		/**
		 * 前段
		 * @return String
		 */
		public String first();
		/**
		 * 中间段
		 * @return String
		 */
		public String center();
		/**
		 * 尾段
		 * @return String
		 */
		public String end();
		
	}
}
