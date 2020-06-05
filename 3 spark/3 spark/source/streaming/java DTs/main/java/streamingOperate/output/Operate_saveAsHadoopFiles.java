package streamingOperate.output;

import java.util.Arrays;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
/**
 * saveAsHadoopFiles(prefix, [suffix])：
 * 将此DStream的内容另存为Hadoop文件。每批次数据产生的文件名称格式基于：prefix和suffix: "prefix-TIME_IN_MS[.suffix]". 
 * 
 * spark中普通rdd可以直接只用saveAsTextFile(path)的方式，保存到本地，但是此时DStream的只有saveAsTextFiles()方法，没有传入路径的方法，
 * 其参数只有prefix, suffix
 * 其实：DStream中的saveAsTextFiles方法中又调用了rdd中的saveAsTextFile方法，我们需要将path包含在prefix中
 *
 */
public class Operate_saveAsHadoopFiles {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_saveAsHadoopFiles");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
		JavaDStream<String> flatMap = textFileStream.flatMap(new FlatMapFunction<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Iterable<String> call(String t) throws Exception {
				
				return Arrays.asList(t.split(" "));
			}
		});
		JavaPairDStream<String, Integer> mapToPair = flatMap.mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, Integer> call(String t) throws Exception {
				return new Tuple2<String, Integer>(t.trim(), 1);
			}
			
		});
		//保存在当前路径中savedata路径下，以prefix开头，以suffix结尾的文件。
		//存hdfs上路径示例：
//		mapToPair.saveAsHadoopFiles("hdfs://node1:9000/log/prefix", "suffix", Text.class, IntWritable.class, TextOutputFormat.class);
		//存本地路径示例：		
//		mapToPair.saveAsHadoopFiles(".\\savedata\\prefix", "suffix", Text.class, IntWritable.class, TextOutputFormat.class);
		//也可以这样写：
		mapToPair.saveAsHadoopFiles("./savedata/prefix", "suffix", Text.class, IntWritable.class, TextOutputFormat.class);

		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
