package sparksql.dataframe;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
/**
 * 从parquet文件创建DataFrame
 * @author root
 *
 */
public class CreateDataFrameFromParquetFile {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("CreateDataFrameFromParquetFile");
		SQLContext sqlContext = new SQLContext(new JavaSparkContext(conf));
		
		/**
		 * 这里我们没有parquet格式的文件，所以先生成一个parquet文件，然后再用sqlcontext来读取生成的parquet文件:
		 * 先将json 格式的文件json.txt 存成parquet文件
		 */
		DataFrame df = sqlContext.read().format("json").load("./sparksql/json.txt");
		/**
		 * 以下 两种方式都可以将DataFrame写成parquet文件，也可以将parquet文件存入hdfs上，下面是存入本地
		 */
		
		df.write().format("parquet").mode("overwrite").save("./sparksql/parquet.parquet");
//		df.write().mode("overwrite").parquet("./sparksql/parquet.parquet");
		
		/**
		 * 读取parquet文件为DataFrame
		 */
		DataFrame parquetdf = sqlContext.read().format("parquet").load("./sparksql/parquet.parquet");
		parquetdf.show();
		
		/**
		 * 下面可以将parquetdf DataFrame注册成一张临时表
		 */
		parquetdf.registerTempTable("parquet");
		
		sqlContext.sql("Select * from parquet where age > 19").show();
		/**
		 * 也可以这样省略以上步骤，直接sql 中 读取parquet文件路径
		 */
		sqlContext.sql("select * from parquet.`./sparksql/parquet.parquet` as t where t.name = 'zhangsan'").show();
	}
}
