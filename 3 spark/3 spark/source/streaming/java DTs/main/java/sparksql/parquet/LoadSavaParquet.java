package sparksql.parquet;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
/**
 * DataFrame存储到某一个路径下，默认存储格式是parquet
 * @author root
 *
 */
public class LoadSavaParquet {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("LoadSavaParquet");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlContext = new SQLContext(jsc);
		/**
		 * 读取parquet文件,以下两种方式：
		 * path 也可以为HDFS路径：“hdfs://node1:9000/parquet.parquet”
		 */
//		sqlContext.read().format("parquet").load("./sparksql/parquet.parquet").show();
		DataFrame parquet = sqlContext.read().parquet("./sparksql/parquet.parquet");
		
		/**
		 * 写入parquet,一般要指定SaveMode为如果存在当前文件覆盖写入
		 * path 也可以为HDFS路径：“hdfs://node1:9000/parquet.parquet”
		 */
		parquet.write().mode(SaveMode.Overwrite).parquet("./sparksql/parquet.parquet1");
		parquet.write().format("parquet").mode(SaveMode.Overwrite).save("./sparksql/parquet.parquet2");
	}
}
