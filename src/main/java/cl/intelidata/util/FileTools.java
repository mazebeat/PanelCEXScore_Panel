package cl.intelidata.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileTools {

	/**
	 * 
	 * @param path
	 * @param extension
	 * @return
	 */
	public ArrayList<File> filesByExtension(String path, String extension) {
		ArrayList<File> list = new ArrayList<File>();
		File directory = new File(path);

		try {
			if (directory.exists() && directory.isDirectory()) {
				File[] files = directory.listFiles();

				for (File file : files) {
					String fileName = file.getName();

					if (fileName.indexOf(extension) != -1) {
						list.add(file);
						System.out.println("File found: " + file.getName());
					}
				}
			} else {
				System.out.println("Error in " + path);
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		return list;
	}

	/**
	 * 
	 * @param list
	 * @param path
	 * @throws IOException
	 */
	public void moveFile(File origin, String path) {
		try {
			String dest = path + System.getProperty("file.separator")
					+ origin.getName();
			File destiny = new File(dest);
			this.moveFileUsingStream(origin, destiny);
			if (origin.exists()) {
				origin.delete();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @param list
	 * @param path
	 * @throws IOException
	 */
	public void moveFiles(ArrayList<File> list, String path) {
		try {
			for (File origin : list) {
				String dest = path + System.getProperty("file.separator")
						+ origin.getName();
				File destiny = new File(dest);
				this.moveFileUsingStream(origin, destiny);
				if (origin.exists()) {
					origin.delete();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	private void moveFileUsingStream(File source, File dest) throws IOException {
		InputStream inputSource = null;
		OutputStream outputSource = null;

		try {
			inputSource = new FileInputStream(source);
			outputSource = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];
			int length;

			while ((length = inputSource.read(buffer)) > 0) {
				outputSource.write(buffer, 0, length);
			}

			source.delete();
			System.out.println("Moving file " + source.getName() + " to "
					+ dest.getAbsolutePath());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} finally {
			inputSource.close();
			outputSource.close();
		}
	}

	/**
	 * 
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void moveFile2(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel origen = null;
		FileChannel destino = null;
		try {
			origen = new FileInputStream(sourceFile).getChannel();
			destino = new FileOutputStream(destFile).getChannel();

			long count = 0;
			long size = origen.size();
			while ((count += destino.transferFrom(origen, count, size - count)) < size)
				;
			sourceFile.delete();
			System.out.println("Moving file " + sourceFile.getName() + " to "
					+ destFile.getAbsolutePath());
		} finally {
			if (origen != null) {
				origen.close();
			}
			if (destino != null) {
				destino.close();
			}
		}
	}

	/**
	 * 
	 * @param list
	 * @param pattern
	 * @return
	 */
	public ArrayList<File> searchFilesByPattern(ArrayList<File> list,
			final String pattern) {
		ArrayList<File> outList = new ArrayList<File>();

		for (File file : list) {
			String fileName = file.getName();

			if (fileName.startsWith(pattern)) {
				outList.add(file);
				System.out.println("File found: " + file.getName());
			}
		}

		return outList;
	}

	/**
	 * 
	 * @param list
	 * @param pattern
	 * @return
	 */
	public ArrayList<File> removeFilesIntoListByPattern(ArrayList<File> list,
			String pattern) {
		ArrayList<File> out = list;
		try {
			for (File file : out) {
				String fileName = file.getName();
				System.out.println(fileName);
				if (fileName != "" && fileName.startsWith(pattern)) {
					out.remove(file);
					System.out.println("File removed: " + file.getName());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return out;
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @return
	 */
	public ArrayList<File> removeFilesIntoListByList(ArrayList<File> in,
			ArrayList<File> out) {
		try {
			for (File file : out) {
				in.remove(file);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return in;
	}
	
	

}
