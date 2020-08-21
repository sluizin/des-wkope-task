package des.wangku.operate.standard.testing;


public class BooksRead {
	

	public static void main(String[] args) 
	{
		String[] arr= {
				//"http://www.yshuwu.com/0/673/",
				//"http://www.niumore.com/book/985/",
				//"http://www.gazww.com/84686.shtml",
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
				"http://www.gazww.com/84730.shtml",
				"http://www.gazww.com/55171.shtml",
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
			if(e==null ||e.length()==0)continue;
			//String url=Books17Utils.getReadurl(e);
			//if(e==null ||e.length()==0)continue;
			System.out.println("URL:"+e);
			BooksReadUtils.makebooks(e);
		}
	}
}
