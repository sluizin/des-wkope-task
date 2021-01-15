package des.wangku.operate.standard.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Joiner;

import des.wangku.operate.standard.database.DBSource;
import des.wangku.operate.standard.utls.UtilsJsoupExt;
import des.wangku.operate.standard.utls.UtilsRegular;

public class TestingTYC {
	static Connection conn=null;

	public static void main(String[] args) {
		conn=DBSource.getMYSQLDEVLocalhost("wk_word");
		
		insertaaa();
		
		
		
		//checkUrl2();
	}
	static void checkUrl2() {
		for(int i=155;i<=159;i++) {
			String href="http://jinyici.xpcha.com/list_0_"+i+".html";
			System.out.println("page:"+i);
			System.out.println("href:"+href);
			Document doc=UtilsJsoupExt.getDoc(href);
			if(doc==null)continue;
			Elements targetdivs=doc.getElementsByClass("shaixuan_5");
			if(targetdivs.size()==0)continue;
			Element targetdiv=targetdivs.first();
			Elements adiv=targetdiv.select("a[href]");
			for(Element link:adiv) {
				String key=link.text().trim();
				if(isExistWordFormDB(key))continue;
				String line=","+key+",";
				String hr=link.attr("abs:href");
				System.out.println("["+i+"]("+key+")hr:"+hr);
				Document doc2=UtilsJsoupExt.getDoc(hr);
				if(doc2==null)continue;
				Elements arr2=doc2.getElementsByClass("shaixuan_1");
				//System.out.println("arr2:"+arr2.size());
				if(arr2.size()==0)continue;
				Element con=arr2.first();
				Elements all=con.select("span");
				for(Element a:all) {
					String key2=a.text();
					key2=key2.replaceAll("：", "");
					line+=key2+",";
					//System.out.println(key+"\t"+key2);
				}
				insert(line);
			}		
			
		}
	}
	static boolean isExistWordDB(String... arrs) {
		if(arrs==null)return true;
		if(arrs.length==1)return isExistWordFormDB(arrs[0]);
		String str=Joiner.on("|").skipNulls().join(arrs);
		String sql="select id from synonymword where word regexp '"+str+"' limit 1;";
		try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	static boolean isExistWordFormDB(String word) {
		if(word==null || word.length()==0)return true;
		String sql="select id from synonymword where word like '%,"+word+",%' limit 1;";
		try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return false;
	}
	static void insert(String word) {
		if(word==null || word.length()==0)return;
		String sql="insert into synonymword (word)values('"+word+"');";
		try(Statement stmt = conn.createStatement();){
			stmt.execute(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	static void insertaaa() {
		String filename="e:/Synonym.txt";
		File file = new File(filename);
		try (FileReader in = new FileReader(file); BufferedReader br = new BufferedReader(in);) {
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] lines=line.split(",");
				boolean is=false;
				for(String e:lines) {
					is|=!isExistWordFormDB(e);
					if(is) {
						System.out.println("not find:"+e);
						break;
					}
				}
				if(is) {
					line=","+line+",";
					//insert(line);
					System.out.println("line:"+line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main2(String[] args) {
		String filename="G:/Synonym.txt";
		File file = new File(filename);
		try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw);) {
			List<String> list=checkUrl1();
			
			for(String e:list) {
				bw.write(e);
				bw.newLine();
				bw.flush();
			}				
		} catch (Exception ff) {
			ff.printStackTrace();
		}
}
	static List<String> checkUrl1() {
		String href="http://www.hydcd.com/tongyicicidian.htm";
		List<String> list=new ArrayList<>();
		
		Document doc=UtilsJsoupExt.getDoc(href);
		if(doc==null)return list;
		Element targetdiv=doc.getElementById("table1");
		if(targetdiv==null)return list;
		Elements arr=targetdiv.select("a");
		for(Element e:arr) {
			System.out.println("e:"+e.text());
			String u=e.attr("abs:href");
			String line="";
			Document doc2=UtilsJsoupExt.getDoc(u);
			
			Elements arr2=doc2.getElementsByAttributeValue("color", "#000020");
			Element result=arr2.first();
			String content=result.text();
			String[] arrs=UtilsRegular.getRegexArray(content,'【','】');
			for(String k1:arrs) {
				line+="\t"+k1;
			}
			//if(list.size()>20)break;
			list.add(line);
		}
		return list;
	}
}
