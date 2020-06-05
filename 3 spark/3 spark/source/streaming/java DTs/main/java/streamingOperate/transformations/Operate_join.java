package streamingOperate.transformations;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
/**
 * join :
 * 当（K，V）格式和（K，W）格式的两个Dstream使用时，返回一个新的（K，（V，W））格式的DStream。
 * 注意：join 作用在（K,V）格式的DStream
 * @author root
 *
 */
public class Operate_join {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_join");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
		JavaPairDStream<String, Integer> flatMapToPair = textFileStream.flatMapToPair(new PairFlatMapFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			public Iterable<Tuple2<String, Integer>> call(String t)
					throws Exception {
				return Arrays.asList(new Tuple2<String , Integer>(t.trim(), 1));
			}
		});
		flatMapToPair.join(flatMapToPair).print(1000);
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
		
	}
}
