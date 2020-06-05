package streamingOperate.transformations;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 *reduce(func): 通过使用函数func（其接受两个参数并返回一个）聚合DStream的每个RDD中的元素,
 *返回单元素RDD的新DStream 。该函数是关联的，以便可以并行计算。
 *reduce 处理后返回的是一条数据结果
 *
 */
public class Operate_reduce {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_reduce");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
		/**
		 * 下面是拼接本次和下次的字符串来模拟reduce处理数据
		 * 
		 */
		JavaDStream<String> reduce = textFileStream.reduce(new Function2<String, String, String>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String call(String s1, String s2) throws Exception {
				
				return s1+"****"+s2;
			}
		});
		
		reduce.print();
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
