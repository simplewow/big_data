package streamingOperate.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import sparksql.Person;
/**
 * 使用创建自定义类型的方式使用sql
 * @author root
 *
 */
public class SQLWithStreaming2 {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WindowOnStreaming");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		jsc.checkpoint("./checkpoint");
		JavaReceiverInputDStream<String> lines = jsc.socketTextStream("node1", 9999);
		
		JavaDStream<String> window = lines.window(Durations.minutes(60), Durations.seconds(15));
		
		JavaDStream<Person> personDS = window.map(new Function<String, Person>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Person call(String lines) throws Exception {
				String[] split = lines.split(" ");
				Person person = new Person(Integer.valueOf(split[0]),split[1],split[2],Integer.valueOf(split[3]));
				return person;
			}
		});
		
		personDS.foreachRDD(new VoidFunction<JavaRDD<Person>>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void call(JavaRDD<Person> personRDD) throws Exception {
				
				SQLContext sqlContext = SQLContext.getOrCreate(personRDD.context());
				
				DataFrame createDataFrame = sqlContext.createDataFrame(personRDD, Person.class);
				createDataFrame.registerTempTable("person");
				sqlContext.sql("select id,name,gender,age from person").show();
			}
		});
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
