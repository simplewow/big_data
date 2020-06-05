package streamingOperate.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
/**
 * 在spark steaming 中使用sparksql查询
 * 
 * 
 * @author root
 *
 */
public class SQLWithStreaming {
	public static void main(String[] args) {
		
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WindowOnStreaming");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		jsc.checkpoint("./checkpoint");
		JavaReceiverInputDStream<String> lines = jsc.socketTextStream("node1", 9999);
		
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Iterable<String> call(String lines) throws Exception {
				
				return Arrays.asList(lines.split(" "));
			}
		});
		/**
		 * 每分钟计算最近一小时的数据量
		 */
		JavaDStream<String> windowWords = words.window(Durations.minutes(60), Durations.seconds(15));
		
		windowWords.foreachRDD(new VoidFunction<JavaRDD<String>>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void call(JavaRDD<String> JavaRDD) throws Exception {
				org.apache.spark.api.java.JavaRDD<Row> wordRowRDD = JavaRDD.map(new Function<String, Row>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public Row call(String word) throws Exception {
						return RowFactory.create(word);
					}
				});
				
				SQLContext sqlContext = SQLContext.getOrCreate(JavaRDD.context());
				/**
				 * 创建schema
				 */
				List<StructField> fields = new ArrayList<StructField>();
				fields.add(DataTypes.createStructField("word", DataTypes.StringType, true));
				StructType createStructType = DataTypes.createStructType(fields);
				DataFrame allwords= sqlContext.createDataFrame(wordRowRDD, createStructType);
//				allwords.show();//显示打印
				/**
				 * 注册成临时表
				 */
				allwords.registerTempTable("words");
				DataFrame result = sqlContext.sql("select word ,count(*) rank from words group by word order by rank");
				result.show(10000);
//				org.apache.spark.api.java.JavaRDD<Row> javaRDD2 = result.javaRDD();
//				javaRDD2.foreach(new VoidFunction<Row>() {
//
//					/**
//					 * 
//					 */
//					private static final long serialVersionUID = 1L;
//
//					public void call(Row row) throws Exception {
//						System.out.println("row-------"+row);
//					}
//				});
			}
		});
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}
