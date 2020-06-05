package streamingOperate.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

import com.google.common.base.Optional;
/**
 * 下面模拟使用transform算子和过滤RDD类型的黑名单，使用到了LeftOuterJoin
 * 如果广播出去的黑名单不是一个list，可以使用这种方式
 * @author root
 *
 */
public class BlackListDemo {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("BlackList");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		JavaReceiverInputDStream<String> socketTextStream = jsc.socketTextStream("node1", 9999);
		/**
		 * 模拟黑名单
		 */
		List<Tuple2<String, Boolean>> blacklist = new ArrayList<Tuple2<String, Boolean>>();
		blacklist.add(new Tuple2<String, Boolean>("zhangsan2", true));
		blacklist.add(new Tuple2<String, Boolean>("lisi2", true));
		final JavaPairRDD<String, Boolean> blacklistRDD = jsc.sparkContext().parallelizePairs(blacklist);
		
		JavaPairDStream<String, String> nameDStream = socketTextStream.mapToPair(new PairFunction<String, String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, String> call(String name) throws Exception {
				return new Tuple2<String, String>(name, name+"~");
			}
		});
		
		nameDStream.transform(new Function<JavaPairRDD<String, String>, JavaRDD<String>>() {

			private static final long serialVersionUID = 1L;

			/**
			 * 使用transform将DStream里面的RDD抽取出来后，调用了RDD的action类算子
			 */
			public JavaRDD<String> call(JavaPairRDD<String, String> nameTransform)throws Exception {
				/**
				 * 正常名单和黑名单leftOuterJoin
				 * 数据格式为：***********(zhangsan,(zhangsan~,Optional.absent()))
				 *		   ***********(zhangsan2,(zhangsan2~,Optional.of(true)))
				 */
				JavaPairRDD<String, Tuple2<String, Optional<Boolean>>> leftOuterJoin = nameTransform.leftOuterJoin(blacklistRDD);
				/**
				 * 将leftOuterJoin过滤掉为掉Optional.of(true)的值
				 */
				JavaPairRDD<String, Tuple2<String, Optional<Boolean>>> filter = leftOuterJoin.filter(new Function<Tuple2<String,Tuple2<String,Optional<Boolean>>>, Boolean>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public Boolean call(Tuple2<String, Tuple2<String, Optional<Boolean>>> filterTuple)throws Exception {
						Optional<Boolean> option = filterTuple._2._2;
						if(option.isPresent()){
							System.out.println("value -- "+ !option.get());
							return !option.get();
						};
						return true;
					}
				});
				/**
				 * tramsform 要求返回JavaRDD<String>类型，map算子可以返回一对一的JavaRDD类型
				 */
				return filter.map(new Function<Tuple2<String,Tuple2<String,Optional<Boolean>>>, String>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public String call(Tuple2<String, Tuple2<String, Optional<Boolean>>> arg0)throws Exception {
						return arg0._1;
					}
				});
			}}).print();
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
		
	}
}
