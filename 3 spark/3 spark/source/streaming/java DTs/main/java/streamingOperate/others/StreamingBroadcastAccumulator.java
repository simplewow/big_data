package streamingOperate.others;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

/**
 * 
 * Broadcast与Accumulator在streaming中的使用
 * 
 * 下面逻辑是将List类型的黑名单广播到executor中
 * 如果不是List类型的广播变量而是RDD类型，可以使用参考BlackListDemo类中写法使用广播出去的rdd和被过滤的相LeftOuterJoin方式。
 * @author root
 *
 */
public class StreamingBroadcastAccumulator {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("StreamingBroadcastAccumulator");
		JavaStreamingContext  jsc = new JavaStreamingContext(conf, Durations.seconds(5));
		/**
		 * 定义广播变量
		 * 下面模拟将a,b广播出去，然后在读取到的文件中过滤显示结果
		 */
		List<String> asList = Arrays.asList("a","b");
		
		final Broadcast<List<String>> blackList = jsc.sparkContext().broadcast(asList);
		/**
		 * 定义累加器
		 * 下面模拟在读取文件的同时，a,b一共被过滤了多少次
		 */
		final Accumulator<Integer> accumulator = jsc.sparkContext().accumulator(0, "accumulator");
		
		JavaDStream<String> textFileStream = jsc.textFileStream("./data");
		
		JavaPairDStream<String, Integer> maptopair = textFileStream.mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, Integer> call(String arg0) throws Exception {
				
				return new Tuple2<String, Integer>(arg0.trim(), 1);
			}
		});
		
		maptopair.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void call(JavaPairRDD<String, Integer> pairRdd)throws Exception {
				final List<String> black = blackList.getValue();
				if(!pairRdd.isEmpty()){
					pairRdd.foreach(new VoidFunction<Tuple2<String,Integer>>() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						public void call(Tuple2<String, Integer> tuple)throws Exception {
							if(black.contains(tuple._1)){
								accumulator.add(tuple._2);
							}
						}
					}); 
					/**
					 * 注意：在task中不能读取accumulator的值
					 */
					System.out.println("accumulator value is :"+accumulator.value());
				}
			}
		});
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
