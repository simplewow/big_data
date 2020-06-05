package streamingOperate.transformations;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
/**
 * cogroup相当于，一个key join上所有value，都给放到一个Iterable里面去！
 * 当（K，V）格式和（K，W）格式的Dstream使用时，返回一个新的DStream（K，Seq [V]，Seq [W]）格式的元组。
 * @author root
 * 数据：
 *  a 100
 *	b 200
 *	c 300
 *	a 400
 *	b 500
 *	c 600
 *
 */
public class Operate_cogroup {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_cogroup");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
		JavaPairDStream<String, Integer> mapToPair = textFileStream.mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String,Integer> call(String lines) throws Exception {
				return new Tuple2<String, Integer>(lines.split(" ")[0].trim(), Integer.valueOf(lines.split(" ")[1].trim()));
			}
		});
		
		mapToPair.cogroup(mapToPair).print(1000);
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
