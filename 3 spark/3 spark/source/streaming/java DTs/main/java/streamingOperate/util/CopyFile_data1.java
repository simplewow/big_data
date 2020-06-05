package streamingOperate.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
/**
 * 此复制文件的程序是模拟在data目录下动态生成相同格式的txt文件，用于给sparkstreaming 中 textFileStream提供输入流。
 * @author root
 *
 */
public class CopyFile_data1 {
	public static void main(String[] args) throws IOException, InterruptedException {
		while(true){
			Thread.sleep(5000);
			String uuid = UUID.randomUUID().toString();
			System.out.println(uuid);
			copyFile(new File("data1.txt"),new File(".\\data\\"+uuid+"----data1.txt"));
		}
	}

	public static void copyFile(File fromFile, File toFile) throws IOException {
		FileInputStream ins = new FileInputStream(fromFile);
		FileOutputStream out = new FileOutputStream(toFile);
		byte[] b = new byte[1024];
		@SuppressWarnings("unused")
		int n = 0;
		while ((n = ins.read(b)) != -1) {
			out.write(b, 0, b.length);
		}

		ins.close();
		out.close();
	}
}