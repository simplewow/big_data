package streamingOperate.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
/**
 * 模拟统计最近20秒内 读取的单词的个数
 * 
 * 
 * @author root
 *
 */
public class WindowOnStreaming {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WindowOnStreaming");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		jsc.checkpoint("./checkpoint");
		JavaReceiverInputDStream<String> lines = jsc.socketTextStream("node1", 9999);
		JavaPairDStream<String, Integer> words = lines.flatMapToPair(new PairFlatMapFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Iterable<Tuple2<String, Integer>> call(String lines)throws Exception {
				String[] split = lines.split(",");
				ArrayList<Tuple2<String, Integer>> tuplelist = new ArrayList<Tuple2<String,Integer>>(); 
				for(String word : split){
					tuplelist.add(new Tuple2<String, Integer>(word, 1));
				}
				System.out.println("------读取了一次-----");
				return tuplelist;
			}
		});
		
		JavaPairDStream<String, Integer> reduceByKeyAndWindow = words.reduceByKeyAndWindow(new Function2<Integer, Integer, Integer>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1+v2;
			}
		}, new Function2<Integer, Integer, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1-v2;
			}
			/**
			 *  每10秒计算最30秒的值
			 */
		}, Durations.seconds(20), Durations.seconds(10));
		/**
		 * 为了方便测试:
		 * 窗口宽度：20秒
		 * 窗口滑动：10秒
		 * 我们输入数据的时候:
		 * 第一次5秒输入:a,b,c
		 * 第二次5秒输入：d,e,f
		 * 第三次5秒不输入
		 * 第四次5秒不输入
		 * 
		 * 然后看文件，观察在第30秒的时候是不是文件中的数据重新都没有
		 * 
		 * 下面我们将结果写入文件，方便查看
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String format = sdf.format(new Date());
		reduceByKeyAndWindow.print();
		reduceByKeyAndWindow.dstream().saveAsTextFiles("./savedata/prefix"+format+"--", "txt");
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
