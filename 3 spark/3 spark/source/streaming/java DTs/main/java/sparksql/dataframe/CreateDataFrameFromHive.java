package sparksql.dataframe;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;

/**
 * 使用Hive数据源创建DataFrame
 * 注意：HiveContext默认情况下在本地无法创建，要将代码打包提交到集群运行。
 * 
 * yarn cluster 集群提交命令：
 * ./spark-submit 
 * --master yarn 
 * --deploy-mode cluster  
 * --class sparksql.dataframe.CreateDataFrameFromHive 
 * --executor-memory 1G 
 * /root/test/SparkStreamingOperate20170706-0.0.1-SNAPSHOT-jar-with-dependencies.jar
 * 
 * 
 * @author root
 *
 */
public class CreateDataFrameFromHive {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf();
		JavaSparkContext jsc = new JavaSparkContext(conf);
		/**
		 * HiveContext是SQLContext的子类
		 */
		SQLContext hiveContext = new HiveContext(jsc);
		/**
		 * 下面简单给出使用spark on hive 的用法
		 */
		//删除hive中的infos表
		hiveContext.sql("DROP TABLE IF EXISTS infos");
		//在hive中创建infos表
		hiveContext.sql("CREATE TABLE IF NOT EXISTS infos (name STRING,age INT) "
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'");
		//创建好infos表后，给infos表导入数据
		hiveContext.sql("LOAD DATA LOCAL INPATH '/root/test/infos' INTO TABLE infos");
		
		//删除score表
		hiveContext.sql("DROP TABLE IF EXISTS score");
		//创建score表
		hiveContext.sql("CREATE TABLE IF NOT EXISTS score (name STRING,score INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'");
		//给score表导入数据
		hiveContext.sql("LOAD DATA LOCAL INPATH '/root/test/score' INTO TABLE score");
		
		//读取hive 创建DataFrame
		DataFrame resultDF = hiveContext.sql("SELECT t1.name,t1.age,t2.score FROM infos t1 , score t2 "
				+ "WHERE t1.name = t2.name AND t2.score > 60");
		/**
		 * 将查询出的数据保存到hive表中
		 */
		//使用result库
//		hiveContext.sql("USE result");
		hiveContext.sql("DROP TABLE IF EXISTS result");
		resultDF.write().saveAsTable("result");
		//以下这个路径是保存在hdfs上的txt文件，目录在hdfs://XXXX/user/root/result/下,默认为parquet文件
		resultDF.write().save("./sparksql/result");
		/**
		 * 读取hive表result 
		 */
		DataFrame table = hiveContext.table("result");
		table.show();
		Row[] collect = hiveContext.table("result").collect();
		for(Row row : collect){
			System.out.println("row-----:"+row);
		}
		
		jsc.close();
	}
}
