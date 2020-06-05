package streamingOperate.transformations;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

import com.google.common.base.Optional;
/**
 * updateStateByKey:
 * 返回一个新的“状态”Dstream,通过给定的func来更新之前的每个状态的key对应的value值，这也可以用于维护key的任意状态数据。
 * 注意：作用在（K,V）格式的DStream上
 * 
 *  updateStateByKey的主要功能:
 * 1、Spark Streaming中为每一个Key维护一份state状态，state类型可以是任意类型的的， 可以是一个自定义的对象，那么更新函数也可以是自定义的。
 * 2、通过更新函数对该key的状态不断更新，对于每个新的batch而言，Spark Streaming会在使用updateStateByKey的时候为已经存在的key进行
 * 		state的状态更新
 * 	（对于每个新出现的key，会同样的执行state的更新函数操作），
 * 	如果要不断的更新每个key的state，就一定涉及到了状态的保存和容错，这个时候就需要开启checkpoint机制和功能 
 * @author root
 *
 */
public class Operate_updateStateByKey {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_count");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		jsc.checkpoint("checkpoint");
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		
		/**
		 * 实现一个累加统计word的功能
		 */
		JavaPairDStream<String, Integer> mapToPair = textFileStream.flatMap(new FlatMapFunction<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Iterable<String> call(String t) throws Exception {
				
				return Arrays.asList(t.split(" "));
			}
		}).mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, Integer> call(String t) throws Exception {
				return new Tuple2<String, Integer>(t.trim(), 1);
			}
		});
		
		JavaPairDStream<String, Integer> updateStateByKey = mapToPair.updateStateByKey(new Function2<List<Integer>, Optional<Integer>, Optional<Integer>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Optional<Integer> call(List<Integer> values, Optional<Integer> state)
					throws Exception {
				/**
				 * values:经过分组最后 这个key所对应的value  [1,1,1,1,1]
				 * state:这个key在本次之前之前的状态
				 */
				Integer updateValue = 0;
				
				if(state.isPresent()){
					updateValue = state.get();
				}
				for(Integer i : values){
					updateValue += i;
				}
				return Optional.of(updateValue);
			}
		});
		
		updateStateByKey.print();
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
	
}
