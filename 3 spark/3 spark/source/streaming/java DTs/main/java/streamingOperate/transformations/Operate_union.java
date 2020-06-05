package streamingOperate.transformations;


import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * union:返回一个新的DStream，它包含源DStream和otherDStream中元素的并集。
 * @author root
 *
 */
public class Operate_union {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_union");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,
				Durations.seconds(5));
		JavaDStream<String> textFileStream1 = jsc.textFileStream("data");
		JavaDStream<String> textFileStream2 = jsc.textFileStream("data");
		
		JavaDStream<String> union = textFileStream1.union(textFileStream2);
		union.print(1000);
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
