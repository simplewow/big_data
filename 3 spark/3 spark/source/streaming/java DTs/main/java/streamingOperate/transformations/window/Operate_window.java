package streamingOperate.transformations.window;


import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

/**
 * window:返回基于源DStream的窗口批次计算的新DStream
 * @author root
 *
 */
public class Operate_window {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_window");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		JavaDStream<String> window2 = textFileStream.window(Durations.seconds(15), Durations.seconds(5));
//		window2.foreachRDD(new VoidFunction<JavaRDD<String>>() {
//			
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			public void call(JavaRDD<String> arg0) throws Exception {
//				System.out.println("***************");
//				
//			}
//		});
		/**
		 * 首先将textFileStream转换为tuple格式统计word字数
		 */
//		JavaPairDStream<String, Integer> mapToPair = textFileStream.flatMap(new FlatMapFunction<String, String>() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			public Iterable<String> call(String t) throws Exception {
//				return Arrays.asList(t.split(" "));
//			}
//		}).mapToPair(new PairFunction<String, String, Integer>() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			public Tuple2<String, Integer> call(String t) throws Exception {
//				return new Tuple2<String, Integer>(t.trim(), 1);
//			}
//		});
//		
//		JavaPairDStream<String, Integer> window = mapToPair.window(Durations.seconds(15),Durations.seconds(5));
//		window.count().print();
//		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
