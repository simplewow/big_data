package sparksql.windowfun;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;
/**
 * row_number()开窗函数：
 * 主要是按照某个字段分组，然后取前几个的值，相当于 分组取topN
 * 注意：
 * 如果SQL语句里面使用到了开窗函数，那么这个SQL语句必须使用HiveContext来执行，HiveContext默认情况下在本地无法创建
 * @author root
 *
 */
public class Row_Number {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Row_Number");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext hiveContext = new HiveContext(jsc);
		
		hiveContext.sql("drop table if exists sales");
		hiveContext.sql("create table if not exists sales (riqi string,leibie string,jine double) "
				+ "row format delimited fields terminated by '\t'");
		hiveContext.sql("load data local inpath '/root/test/sales' into table sales");
		
		DataFrame sql = hiveContext.sql("select * from (select riqi,leibie,jine, row_number() over (partition by leibie order by jine desc) rank from sales) t  where rank <=4");
		sql.show();
	
		hiveContext.sql("drop table if exists top4sales");
		sql.write().saveAsTable("top4sales");
		jsc.close();
	}
}
