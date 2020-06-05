package streamingOperate.transformations;




import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;



/**
 * repartition:通过创建更多或更少的分区来更改此DStream中的并行级别。
 * 
 * @author root
 *
 */
public class Operate_repartition {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_repartition");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
//		JavaPairDStream<String, Integer> mapToPair = textFileStream.flatMap(new FlatMapFunction<String, String>() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			public Iterable<String> call(String t) throws Exception {
//				
//				return Arrays.asList(t.split(" "));
//			}
//		}).mapToPair(new PairFunction<String, String, Integer>() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			public Tuple2<String, Integer> call(String t) throws Exception {
//				
//				return new Tuple2<String, Integer>(t,1);
//			}
//		});
		
		JavaDStream<String> repartition = textFileStream.repartition(8);
		repartition.print(1000);
		repartition.foreachRDD(new VoidFunction<JavaRDD<String>>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void call(JavaRDD<String> rdd) throws Exception {
				System.out.println("rdd partition is "+rdd.partitions().size());
			}
		});
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
