package streamingOperate.transformations;


import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 *count: 通过计算源DStream的每个RDD中的元素数量，返回单元素RDD的新DStream。
 *
 */
public class Operate_count {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_count");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		JavaDStream<Long> count = textFileStream.count();
		count.print();
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
