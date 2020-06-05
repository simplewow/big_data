package streamingOperate.transformations;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
/**
 * countByValue:
 * 当调用类型为K的元素的DStream时，返回（K，Long）键值对的新DStream，
 * 其中键值对的value是key对应的value在Dstream中出现的次数.
 * @author root
 *
 */
public class Operate_countByValue {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_countByValue");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
		JavaPairDStream<String, Integer> mapToPair = textFileStream.mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, Integer> call(String s) throws Exception {
				return new Tuple2<String, Integer>(s.split(" ")[0].trim(),Integer.valueOf(s.split(" ")[1].trim()));
			}
		});
		JavaPairDStream<Tuple2<String, Integer>, Long> countByValue = mapToPair.countByValue();
		countByValue.print(1000);
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
