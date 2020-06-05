package streamingOperate.transformations;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
/**
 * reduceByKey：
 * 当（K，V）格式的Dstream被调用时，返回一个新的（K,V）格式的Dstream, 
 * 其中使用给定的reduce函数聚合每个键的值
 * @author root
 *
 */
public class Operate_reduceByKey {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_reduceByKey");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		/**
		 * 使用reduceByKey必须将JavaDStream 转换成（K,V）格式
		 * 下面例子是读入的每一行，然后统计相同行出现的次数
		 */
		JavaPairDStream<String, Integer> flatMapToPair = textFileStream.flatMapToPair(new PairFlatMapFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			public Iterable<Tuple2<String, Integer>> call(String t)throws Exception {
				
				return Arrays.asList(new Tuple2<String,Integer>(t, 1));
			}
		});
		
		flatMapToPair.reduceByKey(new Function2<Integer, Integer, Integer>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1+v2;
			}
		}).print(1000);
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
