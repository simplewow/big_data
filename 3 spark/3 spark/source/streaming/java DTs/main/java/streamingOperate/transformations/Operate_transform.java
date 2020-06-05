package streamingOperate.transformations;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
/**
 * transform:
 * 通过对Dstream中的每个RDD应用RDD到RDD函数，来返回一个新的DStream。这可以用于对DStream进行任意RDD操作。
 * 
 * @author root
 *
 */
public class Operate_transform {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_transform");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
		textFileStream.transform(new Function<JavaRDD<String>,JavaRDD<String>>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public JavaRDD<String> call(JavaRDD<String> v1) throws Exception {
				v1.foreach(new VoidFunction<String>() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void call(String t) throws Exception {
						System.err.println("**************"+t);
					}
				});
				return v1;
			}
			
		}).print();
		
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
