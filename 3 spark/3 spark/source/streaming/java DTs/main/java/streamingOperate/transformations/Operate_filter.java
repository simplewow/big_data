package streamingOperate.transformations;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * 通过func函数过滤返回为true的记录，返回一个新的Dstream
 * @author root
 *
 */
public class Operate_filter {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_filter");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		/**
		 * 下面的例子是每次读入一行数据，通过观察文件，看到第一行为“crosses the repetition to duplicate daily,”开头
		 * 所以下面将过滤只显示以“crosses the repetition to duplicate daily,”开头的行
		 */
		textFileStream.filter(new Function<String,Boolean>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Boolean call(String line) throws Exception {
				return line.startsWith("a 100");
			}
			
		}).print(1000);
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
