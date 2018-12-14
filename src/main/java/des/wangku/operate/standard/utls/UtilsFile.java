package des.wangku.operate.standard.utls;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import des.wangku.operate.standard.PV;

/**
 * 文件的读写操作
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsFile {
	/** 日志 */
	static final Logger logger = Logger.getLogger(UtilsFile.class);

	/**
	 * 把excel写到文件中
	 * @param filename String
	 * @param workbook Workbook
	 * @return boolean
	 */
	public static final boolean writeWorkbookFile(String filename, Workbook workbook) {
		try {
			File file = new File(filename);
			FileOutputStream fileoutputStream = new FileOutputStream(file);
			workbook.write(fileoutputStream);
			fileoutputStream.close();
			workbook.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * @param filenamepath String
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(String filenamepath) {
		return readFile(filenamepath, true, "\n", false);
	}

	/**
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * 是否过滤#右侧数据
	 * @param filenamepath String
	 * @param isReadUtf8 boolean
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(String filenamepath, final boolean isReadUtf8, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(400);
		File file = new File(filenamepath);
		if (!file.exists()) return sb;
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock;
			do {
				lock = filechannel.tryLock(0L, Long.MAX_VALUE, true);
			} while (null == lock);
			Thread.sleep(10);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String str = isReadUtf8 ? UtilsCode.changedLine(randomFile.readLine()) : randomFile.readLine();
				if (str == null) continue;
				str = str.trim();
				if (str.length() == 0) continue;
				if (delnotes && str.indexOf('#') >= 0) str = str.substring(0, str.indexOf('#'));
				sb.append(str);
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(enterStr);
			}
			lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		return sb;
	}

	/**
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * 按utf-9进行读取<br>
	 * 过滤以"#"为开始的行,
	 * @param filenamepath String
	 * @param enterStr String
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(String filenamepath, String enterStr) {
		StringBuilder sb = new StringBuilder(400);
		File file = new File(filenamepath);
		if (!file.exists()) return sb;
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock;
			do {
				lock = filechannel.tryLock(0L, Long.MAX_VALUE, true);
			} while (null == lock);
			Thread.sleep(10);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String str = UtilsCode.changedLine(randomFile.readLine());
				if (str == null) continue;
				str = str.trim();
				if (str.length() == 0) continue;
				if (str.indexOf('#') == 0) continue;
				sb.append(str);
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(enterStr);
			}
			lock.release();
			randomFile.close();
		} catch (Exception e) {
		}

		return sb;
	}

	/**
	 * 建立output目录下的随机文件。需要输入扩展名
	 * @param proFolder String
	 * @param fileExt String
	 * @return File
	 */
	public static File mkModelRNDFile(String proFolder, String fileExt) {
		String filename = UtilsRnd.getNewFilenameNow(4, 1) + "." + fileExt;
		return mkModelFile(proFolder, filename, fileExt);
	}

	/**
	 * 建立output目录下的指定文件。需要输入扩展名
	 * @param proFolder String
	 * @param filename String
	 * @param fileExt String
	 * @return File
	 */
	public static File mkModelFile(String proFolder, String filename, String fileExt) {
		String path = PV.getJarBasicPath() + "/" + PV.ACC_OutputCatalog + ((proFolder == null || proFolder.length() == 0) ? "" : "/" + proFolder);
		File filefolder = new File(path);
		if (!filefolder.exists()) filefolder.mkdirs();
		if (filename == null) filename = UtilsRnd.getNewFilenameNow(4, 1);
		filename += "." + fileExt;
		String filenameAll = path + "/" + filename;
		return new File(filenameAll);
	}

	/**
	 * 通过文件的绝对地址得到文件全称
	 * @param filename String
	 * @return String
	 */
	public static final String getFileName(String filename) {
		int p = filename.lastIndexOf('\\');
		int p1 = filename.lastIndexOf('/');
		if (p1 > p) p = p1;
		if (p == -1) return null;
		return filename.substring(p + 1);
	}

	/**
	 * 通过文件得到文件全称
	 * @param file File
	 * @return String
	 */
	public static final String getFileName(File file) {
		if (file == null) return null;
		return getFileName(file.getAbsolutePath());
	}

	/**
	 * 把网上图片保存到地址 文件名不变
	 * @param picurl String
	 * @param path String
	 */
	public static final void saveFile(String picurl, String path) {
		try {
			String filename = getFileName(picurl);//picurl.substring(picurl.indexOf('/'),picurl.length());
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
	 * 把网上图片保存到地址 文件名不变 ，如果多个网址，则使用|进行间隔
	 * @param picurl String
	 * @param path String
	 * @return String
	 */
	public static final String downloadPicture(String picurl, String path) {
		String[] arr = picurl.split("\\|");
		StringBuilder sb = new StringBuilder();
		for (String picurl2 : arr) {
			String filename = getFileName(picurl2);
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
}
