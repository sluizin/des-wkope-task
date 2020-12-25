package des.wangku.operate.standard.testing;

import des.wangku.operate.standard.utls.UtilsArrays;

public class BooksRead {
	
	static final int maxThreadNum=10;
	public static void main333(String[] args) {
		String[] arr= {
				"",
				//"http://www.haxixs.com/files/article/info/49/49914.htm",
				//"http://www.haxixs.com/files/article/info/50/50000.htm",
				"",
				"http://www.blpv.cn/?84/84014/",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				""
		};
		for(String e:arr) {	
			BooksReadUtils.donwfile(e);;
		}
	}
	public static void main(String[] args) 
	{
		String[] arr= {
				"http://www.blpv.cn/?84/84014/",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				""
				
		};
		int[] arr_0= {};
		int[] arr_1= {
				
		};
		int[] arr_2= {
				
				
		};
		int[] arr_3= {
		};
		String[] arr0=book9info(0,arr_0);
		String[] arr1=book9info(1,arr_1);
		String[] arr2=book9info(2,arr_2);
		String[] arr3=book9info(3,arr_3);
		String[] arraySort=UtilsArrays.merge(String.class, arr,arr0,arr1,arr2,arr3);
		for(int i=0,len=arraySort.length;i<len;i++)
			System.out.println(i+":"+arraySort[i]);
		boolean isopen=false;
		if(isopen)return;
		boolean isMulit=true;
		if(isMulit) {
			BooksReadUtils.makelist(maxThreadNum, arraySort);
		}else {
			for(String e:ACC_OnlyArrs) {
				if(e==null ||e.length()==0)continue;
				//String url=Books17Utils.getReadurl(e);
				//if(e==null ||e.length()==0)continue;
				System.out.println("URL:"+e);
				BooksReadUtils.makebooks(e);
			}
		}
	}
	static final boolean isgo=true;

	static final String[] ACC_OnlyArrs= {
			"http://www.riluu.com/read/38118/",
			"http://www.riluu.com/read/37662/",
			"http://www.riluu.com/read/37438/",
			"http://www.riluu.com/read/37816/",
			"http://m.e7007.net/mulu/4993/",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
			
	};
	static String[] book9info(int v,int...arr) {
		String[] arrs=new String[arr.length];
		for(int i=0,len=arr.length;i<len;i++)
			arrs[i]="https://www.book9.info/book/"+v+"/"+arr[i]+"/";
		return arrs;
	}
}
