package des.wangku.operate.standard.utls;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Workbook;

import des.wangku.operate.standard.Pv;
import static des.wangku.operate.standard.utls.UtilsConsts.ACC_ENTER;

/**
 * 文件的读写操作
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsFile {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(UtilsFile.class);

	/**
	 * 把网上图片保存到地址 文件名不变 ，如果多个网址，则使用|进行间隔
	 * @param picurl String
	 * @param path String
	 * @return String
	 */
	public static final String downloadPicture(String picurl, final String path) {
		String[] arr = picurl.split("\\|");
		StringBuilder sb = new StringBuilder();
		for (String picurl2 : arr) {
			String filename = UtilsFileMethod.getFileName(picurl2);
			try {
				URL url = new URL(picurl2);
				DataInputStream dataInputStream = new DataInputStream(url.openStream());
				String newFilename = path + "/" + filename;
				FileOutputStream fileOutputStream = new FileOutputStream(new File(newFilename));
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int length;
				while ((length = dataInputStream.read(buffer)) > 0)
					output.write(buffer, 0, length);
				fileOutputStream.write(output.toByteArray());
				if (sb.length() > 0) sb.append("|");
				sb.append(newFilename);
				dataInputStream.close();
				fileOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 更换字符串，并保存文件 支持多个关键字 以|间隔
	 * @param fileName String
	 * @param oldstr String
	 * @param newStr String
	 * @return int 更改数量
	 */
	public static final int fileModify(String fileName, String oldstr, String newStr) {
		int hits = 0;
		File file = new File(fileName);
		if (!file.exists()) return 0;
		try (FileReader in = new FileReader(file); BufferedReader br = new BufferedReader(in); CharArrayWriter caw = new CharArrayWriter();) {
			String line = null;
			final String findStr = "(?:" + oldstr + ")";/* 支持多个关键字 以|间隔 */
			while ((line = br.readLine()) != null) {
				int count = UtilsRegular.getPatternMultiKeyCount(line, findStr);
				if (count > 0) {
					line = line.replaceAll(findStr, newStr);
					hits += count;
				}
				caw.write(line);
				caw.append(ACC_ENTER);
			}
			br.close();
			if (hits == 0) return 0;
			FileWriter out = new FileWriter(file);
			caw.writeTo(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hits;
	}

	/**
	 * 从目录中得到某个文件名，结果为null，即没有找到，找到后返回找到的File
	 * @param file File
	 * @param filename String
	 * @return File
	 */
	public static final File getFilePath(File file, String filename) {
		if (file == null) return null;
		if (filename == null || filename.length() == 0) return null;
		if (file.getName().equalsIgnoreCase(filename)) return file;
		File[] list = file.listFiles();
		for (File e : list) {
			if (e.isFile()) {
				if (e.getName().equalsIgnoreCase(filename)) return e;
			}
			if (e.isDirectory()) {
				File result = getFilePath(e, filename);
				if (result != null) return result;
			}
		}
		return null;
	}

	/**
	 * 从目录中得到某个文件名，结果为null，即没有找到，找到后返回找到的路径
	 * @param file File
	 * @param filename String
	 * @return String
	 */
	public static final String getFileNamePath(File file, String filename) {
		if (file == null) return null;
		if (filename == null || filename.length() == 0) return null;
		if (file.getName().equalsIgnoreCase(filename)) return file.getAbsolutePath();
		File[] list = file.listFiles();
		for (File e : list) {
			if (e.isFile()) {
				if (e.getName().equalsIgnoreCase(filename)) return e.getAbsolutePath();
			}
			if (e.isDirectory()) {
				String result = getFileNamePath(e, filename);
				if (result != null) return result;
			}
		}
		return null;
	}

	/**
	 * 从目录中得到某个文件名，结果为null，即没有找到，找到后返回找到的路径
	 * @param catalog String
	 * @param filename String
	 * @return File
	 */
	public static final File getFilePath(String catalog, String filename) {
		if (catalog == null || catalog.length() == 0) return null;
		return getFilePath(new File(catalog), filename);
	}

	/**
	 * 从目录中提出所有文件，指名扩展名，忽略大小写
	 * @param catalog String
	 * @param extendName String
	 * @return List&lt;File&gt;
	 */
	public static final List<File> getFileAll(String catalog, String extendName) {
		List<File> list = getFileAll(new File(catalog));
		if (extendName == null || extendName.trim().length() == 0) return list;
		List<File> nlist = new ArrayList<>(list.size());
		String right = "." + extendName.trim().toLowerCase();
		for (File e : list) {
			String name = e.getName().toLowerCase();
			if (name.endsWith(right)) nlist.add(e);
		}
		return nlist;
	}
	/**
	 * 
	 * 从目录中提出所有文件，指名扩展名，忽略大小写，并进行排序
	 * @param catalog String
	 * @param extendName String
	 * @param com Comparator&lt;File&gt;
	 * @return List&lt;File&gt;
	 */
	public static final List<File> getFileAll(String catalog, String extendName,Comparator<File> com) {
		List<File> list=getFileAll(catalog, extendName);
		Collections.sort(list, com);
		return list;
	}

	/**
	 * 从目录中提出所有文件
	 * @param catalog String
	 * @return List&lt;File&gt;
	 */
	public static final List<File> getFileAll(String catalog) {
		return getFileAll(new File(catalog));
	}

	/**
	 * 从目录中提出所有文件
	 * @param file File
	 * @return List&lt;File&gt;
	 */
	public static final List<File> getFileAll(File file) {
		List<File> list = new ArrayList<>();
		if (file == null || file.isFile()) return list;
		getFileAllPrivate(list, file);
        Collections.sort(list, (File o1, File o2) -> o1.compareTo(o2)); // 升序
		return list;
	}

	/**
	 * 从目录中提出所有文件
	 * @param list List&lt;File&gt;
	 * @param file File
	 */
	private static final void getFileAllPrivate(List<File> list, File file) {
		if (file == null || file.isFile()) return;
		File[] files = file.listFiles();
		for (File e : files) {
			if (e.isFile()) list.add(e);
			if (e.isDirectory()) getFileAllPrivate(list, e);
		}
	}

	/**
	 * 从目录中得到某个文件名，结果为null，即没有找到，找到后返回找到的路径
	 * @param path String
	 * @param filename String
	 * @return String
	 */
	public static final String getFileNamePath(String path, String filename) {
		if (path == null || path.length() == 0) return null;
		return getFileNamePath(new File(path), filename);
	}

	/**
	 * 从目录中判断是否有此文件
	 * @param file File
	 * @param filename String
	 * @return String
	 */
	public static final boolean isExistFile(File file, String filename) {
		return getFileNamePath(file, filename) != null;
	}

	/**
	 * 从目录中判断是否有此文件
	 * @param path String
	 * @param filename String
	 * @return String
	 */
	public static final boolean isExistFile(String path, String filename) {
		return getFileNamePath(path, filename) != null;
	}

	public static final boolean isExistFileContentByKey(File file, boolean isReadUtf8, int maxlen, String... arrs) {
		if (file == null) return false;
		if (!file.isFile()) return false;
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock = null;
			lock = readfileFirst(filechannel, lock);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String line = isReadUtf8 ? UtilsCode.changedLine(randomFile.readLine()) : randomFile.readLine();
				if (line == null) continue;
				if (line.length() > maxlen) continue;
				if (UtilsString.isExistKey(line, arrs)) return true;
			}
			if (lock != null) lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 读取文件之前把文件锁定
	 * @param filechannel FileChannel
	 * @param lock FileLock
	 * @return FileLock
	 */
	private static final FileLock readfileFirst(FileChannel filechannel, FileLock lock) {
		try {
			do {
				lock = filechannel.tryLock(0L, Long.MAX_VALUE, true);
			} while (null == lock);
			Thread.sleep(10);
		} catch (Exception e) {
		}
		return lock;
	}

	/**
	 * 建立output目录下的指定文件。需要输入扩展名
	 * @param proFolder String
	 * @param filename String
	 * @param fileExt String
	 * @return File
	 */
	public static final File mkOutputFile(String proFolder, String filename, String fileExt) {
		String path = Pv.getOutpoutCatalog() + ((proFolder == null || proFolder.length() == 0) ? "" : "/" + proFolder);
		File filefolder = new File(path);
		if (!filefolder.exists()) filefolder.mkdirs();
		if (filename == null) filename = UtilsRnd.getNewFilenameNow(4, 1);
		filename += "." + fileExt;
		String filenameAll = path + "/" + filename;
		return new File(filenameAll);
	}

	/**
	 * 建立output目录下的随机文件。需要输入扩展名
	 * @param proFolder String
	 * @param fileExt String
	 * @return File
	 */
	public static final File mkOutputRNDFile(String proFolder, String fileExt) {
		String filename = UtilsRnd.getNewFilenameNow(4, 1);
		return mkOutputFile(proFolder, filename, fileExt);
	}

	/**
	 * 从 file中读出字符串以utf-8读
	 * @param file File
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(File file) {
		try (InputStream is2 = new FileInputStream(file); InputStream in = new BufferedInputStream(is2);) {
			return UtilsFile.readFile(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new StringBuilder();
	}

	/**
	 * 从InputStream中读出字符串以utf-8读
	 * @param in InputStream
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(InputStream in) {
		StringBuilder sb = new StringBuilder(100);
		final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];
		try {
			Reader in2 = new InputStreamReader(in, "UTF-8");
			for (;;) {
				int rsz = in2.read(buffer, 0, buffer.length);
				if (rsz < 0) break;
				sb.append(buffer, 0, rsz);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return sb;
	}

	/**
	 * 通过RandomAccessFile读文件 utf-8 按行读 randomFile.readLine<br>
	 * @param filenamePath String
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(String filenamePath) {
		return readFile(filenamePath, true, "\n", false);
	}

	/**
	 * 通过RandomAccessFile读文件 utf-8 按行读 randomFile.readLine<br>
	 * @param filenamePath String
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(File file, boolean isReadUtf8) {
		return readFile(file.getAbsolutePath(), isReadUtf8, "\n", false);
	}

	/**
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * @param filenamePath String
	 * @param isReadUtf8 boolean
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(String filenamePath, boolean isReadUtf8) {
		return readFile(filenamePath, isReadUtf8, "\n", false);
	}

	/**
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * 是否过滤#右侧数据
	 * @param filenamePath String
	 * @param isReadUtf8 boolean
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(String filenamePath, boolean isReadUtf8, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(400);
		File file = new File(filenamePath);
		if ((!file.exists()) || file.isDirectory()) return sb;
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock = null;
			lock = readfileFirst(filechannel, lock);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String line = isReadUtf8 ? UtilsCode.changedLine(randomFile.readLine()) : randomFile.readLine();
				if (line == null) continue;
				String str = line.trim();
				if (str.length() == 0) continue;
				if (delnotes && str.indexOf('#') >= 0) str = str.substring(0, str.indexOf('#'));
				sb.append(str);
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(enterStr);
			}
			if (lock != null) lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		return sb;
	}

	/**
	 * 通过RandomAccessFile读文件 utf-8 按行读 randomFile.readLine<br>
	 * 按utf-9进行读取<br>
	 * 过滤以"#"为开始的行,
	 * @param filenamePath String
	 * @param enterStr String
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(String filenamePath, String enterStr) {
		StringBuilder sb = new StringBuilder(400);
		File file = new File(filenamePath);
		if (!file.exists()) return sb;
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock = null;
			lock = readfileFirst(filechannel, lock);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String str = UtilsCode.changedLine(randomFile.readLine());
				if (str == null) continue;
				str = str.trim();
				if (str.length() == 0) continue;
				if (str.indexOf('#') == 0) continue;
				sb.append(str);
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(enterStr);
			}
			if (lock != null) lock.release();
			randomFile.close();
		} catch (Exception e) {
		}

		return sb;
	}

	/**
	 * 通过RandomAccessFile读文件<br>
	 * utf-8 按行读
	 * @param file String
	 * @return StringBuilder
	 */
	public static final StringBuilder readUTF8(String file) {
		return readAll(file, "UTF-8");
	}

	/**
	 * 通过RandomAccessFile读文件<br>
	 * GBK 按行读
	 * @param file String
	 * @return StringBuilder
	 */
	public static final StringBuilder readGBK(String file) {
		return readAll(file, "GBK");
	}

	/**
	 * 通过RandomAccessFile读文件<br>
	 * @param file String
	 * @param code code
	 * @return StringBuilder
	 */
	public static final StringBuilder readAll(String file, String code) {
		if (file == null) return new StringBuilder(0);
		File f = new File(file);
		if (!f.exists()) return new StringBuilder(0);
		return readAll(f, code);

	}

	/**
	 * 通过RandomAccessFile读文件
	 * @param file File
	 * @param code String
	 * @return StringBuilder
	 */
	public static final StringBuilder readAll(File file, String code) {
		if (file == null || !file.exists()) return new StringBuilder(0);
		StringBuilder sb = new StringBuilder(200);
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock = null;
			lock = readfileFirst(filechannel, lock);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String str = UtilsCode.changedLine(randomFile.readLine(), code);
				if (str == null) continue;
				sb.append(str);
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(ACC_ENTER);
			}
			if (lock != null) lock.release();
			randomFile.close();
		} catch (Exception e) {
		}

		return sb;
	}

	/**
	 * 一次性读取所有文件内容，如果有问题，则返回空
	 * @param fileName String
	 * @param encoding String
	 * @return String
	 */
	public static final String readFileToString(String fileName, boolean isReadUtf8, String... arr) {
		if (fileName == null) return "";
		return readFileToString(new File(fileName), isReadUtf8, arr);
	}

	/**
	 * 一次性读取所有文件内容，如果有问题，则返回null
	 * @param file File
	 * @param isReadUtf8 boolean
	 * @param arrs String[]
	 * @return String
	 */
	public static final String readFileToString(File file, boolean isReadUtf8, String... arrs) {
		if (file == null) return null;
		if (!file.isFile()) return null;
		StringBuilder sb = new StringBuilder();
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock = null;
			lock = readfileFirst(filechannel, lock);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String line = isReadUtf8 ? UtilsCode.changedLine(randomFile.readLine()) : randomFile.readLine();
				if (line == null) continue;
				if (UtilsString.isExistKey(line, arrs)) {
					System.out.println("line:" + line);
					sb.append(line);
				}
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(ACC_ENTER);
			}
			if (lock != null) lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		return sb.toString();
	}

	/**
	 * 以utf-8读取文件所有内容，以行宽最大值标准，
	 * @param file File
	 * @param maxLen int
	 * @return String
	 */
	public static final String readFileToStringByMaxLen(File file, int maxLen) {
		if (file == null) return null;
		if (!file.isFile()) return null;
		StringBuilder sb = new StringBuilder();
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock = null;
			lock = readfileFirst(filechannel, lock);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String line = UtilsCode.changedLine(randomFile.readLine());
				if (line == null) continue;
				if (line.length() == 0) continue;
				if (line.length() <= maxLen) {
					//System.out.println("line:"+line);
					sb.append(line);
				}
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(ACC_ENTER);
			}
			if (lock != null) lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		return sb.toString();
	}

	/**
	 * 把网上图片保存到地址 文件名不变
	 * @param picurl String
	 * @param path String
	 */
	public static final void saveFile(String picurl, String path) {
		try {
			String filename = UtilsFileMethod.getFileName(picurl);//picurl.substring(picurl.indexOf('/'),picurl.length());
			URL url = new URL(picurl);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(30000);
			InputStream input = con.getInputStream();
			byte[] bs = new byte[1024 * 2];
			int len;
			OutputStream os = new FileOutputStream(path + "/" + filename);
			while ((len = input.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			os.close();
			input.close();
		} catch (Exception e) {
		}
	}
	/**
	 * 要显示的长字符串时头部保留用于logger里显示时的字符串长度
	 */
	static final int ACC_ShowLogger_WriteFileInforLen=26;
	/**
	 * 写文件，以追加的方式
	 * @param file File
	 * @param content String
	 */
	public static final void writeFile(File file, String content) {
		if (file == null || content == null) return;
		try {
			mkdirs(file);
			if (!file.exists()) file.createNewFile();
			FileWriter fw = new FileWriter(file, true);
			fw.write(content);
			String cut = content.trim();
			if (cut.length() > ACC_ShowLogger_WriteFileInforLen) cut = cut.substring(0, ACC_ShowLogger_WriteFileInforLen);
			logger.debug("add File[" + file.getAbsolutePath() + "] content:" + cut);
			fw.close();

		} catch (Exception e) {
			System.out.println("error:" + file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	/**
	 * 写文件，以追加的方式
	 * @param filename String
	 * @param content String
	 */
	public static final void writeFile(String filename, String content) {
		if (filename == null || content == null) return;
		writeFile(new File(filename), content);
	}

	/**
	 * 向文件头部添加字符串
	 * @param file File
	 * @param content String
	 */
	public static final void writeFileHeaderSmall(File file, String content) {
		if (file == null || content == null) return;
		try {
			byte[] header = content.getBytes();
			if (!file.exists()) file.createNewFile();
			RandomAccessFile src = new RandomAccessFile(file, "rw");
			int srcLength = (int) src.length();
			byte[] buff = new byte[srcLength];
			src.read(buff, 0, srcLength);
			src.seek(0);
			src.write(header);
			src.seek(header.length);
			src.write(buff);
			src.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 向文件头部添加字符串
	 * @param filepath String
	 * @param content String
	 */
	public static final void writeFileHeaderSmall(String filepath, String content) {
		if (filepath == null || content == null) return;
		writeFileHeaderSmall(new File(filepath), content);
	}

	/**
	 * 把excel写到文件中
	 * @param filename String
	 * @param wb Workbook
	 * @return boolean
	 */
	public static final boolean writeWorkbookFile(String filename, Workbook wb) {
		return writeWorkbookFile(filename, wb, true);
	}

	/**
	 * 把excel写到文件中， 是否关闭workbook
	 * @param filename String
	 * @param wb Workbook
	 * @param close boolean
	 * @return boolean
	 */
	public static final boolean writeWorkbookFile(String filename, Workbook wb, boolean close) {
		try {
			File file = new File(filename);
			File catalog = file.getParentFile();
			if (!catalog.exists()) catalog.mkdirs();/* 如果没有目录，则建立目录 */
			FileOutputStream fos = new FileOutputStream(file);
			wb.write(fos);
			fos.close();
			if (close) wb.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 如果此文件没有目录，则建立相应的目录
	 * @param file File
	 */
	public static final void mkdirs(File file) {
		if (file == null) return;
		File catalog = file.getParentFile();
		if (!catalog.exists()) catalog.mkdirs();/* 如果没有目录，则建立目录 */
	}

	/**
	 * 从输入流中获取字节数组
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	public static final void zipTxt(String savePath,String prefix, String catalog) {
		List<File> list = getFileAll(catalog,"txt");
		String rnd= UtilsRnd.getNewFilenameNow(4, 1);
		String nameleft = prefix + "_"+rnd;
		//File file = new File(savePath+"/"+mkRNDFileName(prefix,"txt"));
		int i=0;
		long big=40 * 1024 * 1024;
		for (File e : list) {
			String filename=nameleft+"_"+i+".txt";
			File file = new File(savePath+"/"+filename);
			if(file.length()>big) {
				i++;
				continue;
			}
			String name = e.getName();
			String absolute = e.getAbsolutePath();
			String fileEncode=UtilsCpdetector.getFilecharset(absolute);   
			writeFile(file, ACC_ENTER+"第000章 " + name + " "+ absolute + ACC_ENTER);
			String content = readAll(e, fileEncode).toString();
			writeFile(file, ACC_ENTER+content + ACC_ENTER);
		}
	}
	public static final void showCatalogTxtString(String catalog,String val) {
		List<File> list = getFileAll(catalog,"txt");
		class infor implements Comparable<infor>{
			File e;
			int sort=0;
			@Override
			public int compareTo(infor o) {
				return sort-o.sort;
			}
		}
		List<infor> nlist=new ArrayList<>(list.size());
		for (File e : list) {
			String absolute = e.getAbsolutePath();
			String fileEncode=UtilsCpdetector.getFilecharset(absolute);
			String content = readAll(e, fileEncode).toString();
			int count=UtilsRegular.getPatternCount(content,val);
			//System.out.println(e.getName()+":"+count);
			if(count<15)continue;
			infor c=new infor();
			c.e=e;
			c.sort=count;
			nlist.add(c);
		}
		Collections.sort(nlist);
		for (infor e : nlist) {
			System.out.println(e.e.getName()+":"+e.sort);
		}
		
	}

	public static void main(String[] args) {
		String path="G:\\Download\\save\\短篇";
		//zipTxt("g:/","短篇",path);
		showCatalogTxtString(path,ACC_ENTER);
		/*
		String path = "G:/Download/bf";
		List<File> list = getFileAll(path);

        Collections.sort(list, (File o1, File o2) -> o1.getName().compareTo(o2.getName())); // 升序

		File file = new File("g:/"+mkRNDFileName("短篇","txt"));
		for (File e : list) {
			String name = e.getAbsolutePath();
			writeFile(file, ACC_ENTER+"第000章 " + name + ACC_ENTER);
			//String content=readFile(file.getAbsolutePath(),false).toString();
			//String content = readAll(e, "gbk").toString();
			//System.out.println("content:"+content);
			//writeFile(file, ACC_ENTER+content + ACC_ENTER);
		}
		*/
	}
}
