package des.wangku.operate.standard.subengineering.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 小说的lucene
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Story {
	static final String StorySourcePath="G:\\Download\\save\\超长篇";
	static final String StoryLuceneStoragePath="E:\\Service\\lucene-storage\\story";
	public static void main(String[] args) 
	{
		System.out.println("Hello World!");
		makeStorage(StorySourcePath,StoryLuceneStoragePath);
		
	}
	public static final void makeStorage(String fileCatalog,String indexCatalog ) {
		Path a=Paths.get(fileCatalog);
		Path b=Paths.get(indexCatalog);
		makeStorage(a,b);
	}
	@SuppressWarnings({"unchecked","rawtypes"})
	public static final void makeStorage(Path fileDoc,Path index ) {
		try {
			// 索引在硬盘上面的存储位置
			Directory directory = FSDirectory.open(index);
	        Analyzer analyzer = new StandardAnalyzer();
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
	        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
	        if (Files.isDirectory(fileDoc)) {
                System.out.println("isDirectory");
				SimpleFileVisitor s=new SimpleFileVisitor<Path>() {
	                //重写visitFile的方法，这对于
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                    //传入的file是一个文件类型的，attrs是该文件的一些属性
	                    indexDocs(file,indexWriter);
	                    System.out.println(""+file.toString());
	                    return FileVisitResult.CONTINUE;
	                }
	            };
	            Files.walkFileTree(fileDoc,s);
	        }else {
	        	indexDocs(fileDoc,indexWriter);
	        }
			
		}catch(Exception ee) {
			
		}
	}
	public static void indexDocs(Path path, IndexWriter indexWriter) throws IOException {
        //将文件以类的方式读入
        InputStream inputStream = Files.newInputStream(path);
        //存入的文档
        Document document = new Document();
        //存入文档的属性,第一个是字段名，第二个是内容，第三个是否存储内容
        //Field有很多实现类，对于不同类型的字段，有不同的实现类来操作StringField是存储String类型的字段，不进行分词
        document.add(new StringField("filePath", path.toString(), Field.Store.YES));
        //Field有很多实现类，对于不同类型的字段，有不同的实现类来操作StringField是存储String类型的字段，不进行分词
        document.add(new StringField("fileName", path.getFileName().toString(), Field.Store.YES));
        //TextField存入比较大的文本内容，要进行分词。一个是字段名，一个是Reader
        //new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")))通过utf-8格式，获取带缓存的Reader
        document.add(new TextField("content",new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")))));
        //LongPoint用于存储long类型数据，不分词
        document.add(new LongPoint("modified",Files.getLastModifiedTime(path).toMillis()));
        //在存入索引时，打出操作动作
        System.out.println("adding files:"+path);
        //添加文档
        indexWriter.addDocument(document);
        //显示关闭流
        inputStream.close();
    }

}
