package streamingOperate.transformations;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * map 算子操作
 * map算子是输入一个元素输出一个元素
 * @author root
 *
 */
public class Operate_map {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_map");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		/**
		 * 使用textFileStream读取文件的注意事项：
		 * jsc.textFileStream(dataDirectory);
		 * 
		 * 1.这些文件格式必须相同，如：统一为文本文件；
		 * 2.这些文件在目录dataDirectory中的创建形式比较特殊：必须以原子方式被“移动”或“重命名”至目录dataDirectory中；
         * 3.一旦文件被“移动”或“重命名”至目录dataDirectory中，文件不可以被改变，例如：追加至这些文件的数据可能不会被处理。
		 */
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		JavaDStream<String> map = textFileStream.map(new Function<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String call(String s) throws Exception {
				return s.trim();
			}
		});
		
		map.print(1000);
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
