package streamingOperate.transformations;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * 与map类似，但每个输入项可以映射到0个或更多的输出项。
 * 也就是说输入项为1，输出项可以为0到多个
 * @author root
 *
 */
public class Operate_flatMap {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Operate_flatmap");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		/**
		 * 下面的例子是读入的每一行，然后每一行中按空格切分，将每个单词以Iterable的形式返回。即：输入一行，输出多个单词
		 */
		JavaDStream<String> flatMap = textFileStream.flatMap(new FlatMapFunction<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Iterable<String> call(String t) throws Exception {
				return Arrays.asList(t.split(" "));
			}
		});
		
		flatMap.print(1000);
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
