package streamingOperate.demo;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
/**
 * 模拟在线统计每次输入的单词个数
 *
 */

public class WordCountOnline {
	
	public static void main(String[] args) {
		 
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WordCountOnline");
		/**
		 * 在创建streaminContext的时候 设置batch Interval
		 */
		JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(5));
		
		JavaReceiverInputDStream<String> lines = jsc.socketTextStream("node1", 9999);
		
		
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Iterable<String> call(String lines) throws Exception {
				return Arrays.asList(lines.split(" "));
			}});

		JavaPairDStream<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, Integer> call(String word) throws Exception {
				return new Tuple2<String, Integer>(word, 1);
			}});

		JavaPairDStream<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1+v2;
			}});
		 
 		counts.print();
 		 
 		jsc.start();
 		jsc.awaitTermination();
 		/**
 		 * JavaStreamingContext.stop()无参的stop方法会将sparkContext一同关闭，stop(false)
 		 */
 		jsc.stop(false);
 		jsc.close();
	}
}
