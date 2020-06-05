package streamingOperate.output;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * print:在运行streaming程序的Driver节点端打印Dstream每批次数据的前10个元素。这对开发和调试非常有用。
 * @author root
 *
 */
public class Operate_print {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Operate_print");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		textFileStream.print(1000);
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
