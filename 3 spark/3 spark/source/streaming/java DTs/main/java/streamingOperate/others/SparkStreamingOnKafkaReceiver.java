package streamingOperate.others;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import scala.Tuple2;

public class SparkStreamingOnKafkaReceiver {
 
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("SparkStreamingOnKafkaReceiver").setMaster("local[2]");
		conf.set("spark.streaming.receiver.writeAheadLog.enable","true");
		conf.set("spark.streaming.concurrentJobs", "10");
		
		JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(5));
  		jsc.checkpoint("d://receivedata");
		/**
		 * 可以直接读取Flume
		 */
//		JavaReceiverInputDStream lines = FlumeUtils.createStream(jsc,"192.168.5.1", 9999);
//		JavaReceiverInputDStream lines = FlumeUtils.createPollingStream(jsc, "192.168.5.128", 8899);
		
		
		Map<String, Integer> topicConsumerConcurrency = new HashMap<String, Integer>();
		topicConsumerConcurrency.put("kfk", 1);
		
		/**
		 * 第一个参数是StreamingContext
		 * 第二个参数是ZooKeeper集群信息（接受Kafka数据的时候会从Zookeeper中获得Offset等元数据信息）
		 * 第三个参数是Consumer Group
		 * 第四个参数是消费的Topic以及并发读取Topic中Partition的线程数
		 * 
		 * 注意：
		 * 1、KafkaUtils.createStream 使用五个参数的方法，设置receiver的存储级别
		 * 2、在java里面使用多个receiver，需要将JavaPairReceiverInputDStream转换成javaDstream使用toJavaDstream
		 * 
		 */
		JavaPairReceiverInputDStream<String,String> lines = KafkaUtils.createStream(
				jsc,
				"192.168.126.111:2181,192.168.126.112:2181,192.168.126.113:2181",
				"MyFirstConsumerGroup", 
				topicConsumerConcurrency/*,StorageLevel.MEMORY_AND_DISK()*/);
		
		
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<Tuple2<String,String>, String>() { 
			//如果是Scala，由于SAM转换，所以可以写成val words = lines.flatMap { line => line.split(" ")}

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Iterable<String> call(Tuple2<String,String> tuple) throws Exception {
				return Arrays.asList(tuple._2.split("\t"));
			}
		});
		
		  
		JavaPairDStream<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, Integer> call(String word) throws Exception {
				return new Tuple2<String, Integer>(word, 1);
			}
		});
		
		  
		JavaPairDStream<String, Integer> wordsCount = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() { 
			//对相同的Key，进行Value的累计（包括Local和Reducer级别同时Reduce）
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1 + v2;
			}
		});
		
		 
		wordsCount.print();
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}

}
