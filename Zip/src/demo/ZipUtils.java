package demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUtils {
	private static final List<ZipEntry> entries = new ArrayList<ZipEntry>();

	public ZipUtils() {
	}

	public static void unZipDirectory(String zipFileDirectory,
			String outputDirectory) throws ZipException, IOException {
		File file = new File(zipFileDirectory);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".zip")) {
				unzip(zipFileDirectory + File.separator + files[i].getName(),
						outputDirectory);
			}
		}
	}

	public static void unzip(String string, String outputDirectory)
			throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(string);
		Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();
		while (enu.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) enu.nextElement();
			if (entry.isDirectory()) {
				String fileName = entry.getName().substring(0,
						entry.getName().length() - 1);
				String directoryPath = outputDirectory + File.separator
						+ fileName;
				File directory = new File(directoryPath);
				directory.mkdir();
			}
			entries.add(entry);
		}
		unzip(zipFile, entries, outputDirectory);
	}

	private static void unzip(ZipFile zipFile, List<ZipEntry> entries2,
			String outputDirectory) throws IOException {
		// TODO Auto-generated method stub
		Iterator<ZipEntry> it = entries.iterator();
		while (it.hasNext()) {
			ZipEntry zipEntry = (ZipEntry) it.next();
			MultiThreadEntry mte = new MultiThreadEntry(zipFile, zipEntry,
					outputDirectory);

			Thread thread = new Thread(mte);
			thread.start();
		}
	}
	//读取压缩文件中的内容名称
	public static List<String> readZipFile(String file) throws Exception {  
		List<String> list = new ArrayList<String>();
        InputStream in = new BufferedInputStream(new FileInputStream(file));  
        ZipInputStream zin = new ZipInputStream(in); 
        ZipEntry ze;  
        while ((ze = zin.getNextEntry()) != null) {  
            if (ze.isDirectory()) {
            } else {  
            	String zeName = new String(ze.getName().getBytes("iso-8859-1"),"utf-8");
            	list.add(zeName);
            }  
        }  
        zin.closeEntry();
		return list;  
    }  
	//删除文件
	private static void deleteFile(String Path) {
		// TODO Auto-generated method stub
		File file = new File(Path);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    }  
	}
	public static void main(String args[]) throws Exception {
		List<String> list = readZipFile("D:\\Temp\\Temp.zip");
		for (int i = 0; i < list.size(); i++) {
			System.err.println(list.get(i));
		}
		ZipUtils.unzip("D:\\Temp\\Temp.zip", "D:\\Temp");
		ZipUtils.deleteFile("D:\\Temp\\Temp.zip");
	}
}

/**
 * 使用多线程，提高效率
 * @author 立强
 *
 */
class MultiThreadEntry implements Runnable {
	public static final int BUFFER_SIZE = 4096;
	private BufferedInputStream bis;
	private ZipEntry zipEntry;
	private String outputDirectory;

	public MultiThreadEntry(ZipFile zipFile, ZipEntry zipEntry,
			String outputDirectory) throws IOException {
		this.zipEntry = zipEntry;
		this.outputDirectory = outputDirectory;
		bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
	}

	public void run() {
		try {
			unzipFiles(zipEntry, outputDirectory);
		} catch (IOException e) {
			try {
				bis.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void unzipFiles(ZipEntry zipEntry, String outputDirectory)
			throws IOException {
		byte[] data = new byte[BUFFER_SIZE];
		String entryName = zipEntry.getName();
		entryName = new String(entryName.getBytes("GBK"));
		FileOutputStream fos = new FileOutputStream(outputDirectory
				+ File.separator + entryName);
		if (zipEntry.isDirectory()) {

		} else {
			BufferedOutputStream bos = new BufferedOutputStream(fos,
					BUFFER_SIZE);
			int count = 0;
			while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
				bos.write(data, 0, count);
			}
			bos.flush();
			bos.close();
		}
	}
}
