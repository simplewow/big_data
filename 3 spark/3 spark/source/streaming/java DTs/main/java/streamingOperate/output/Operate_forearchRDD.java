package streamingOperate.output;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * forearchRDD是output类算子
 * 对于从流中RDD应用func的最通用的一个output 操作。
 * 该功能应将每个RDD中的数据推送到外部系统，
 * 例如将RDD保存到文件，或将其通过网络写入数据库。
 * 请注意，函数func是在运行streaming应用程序的Driver端进程中执行，通常在其中有对Streaming RDDs的强制操作。
 * @author root
 *
 */
public class Operate_forearchRDD {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Operate_forearchRDD");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
		textFileStream.foreachRDD(new VoidFunction<JavaRDD<String>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void call(JavaRDD<String> t) throws Exception {
				t.foreach(new VoidFunction<String>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void call(String t) throws Exception {
						System.out.println(t);
						
					}
				});
			}
		});
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
