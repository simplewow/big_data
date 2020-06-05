package sparksql.dataframe;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
/**
 * 从一个json文件中创建DataFrame
 */
public class CreateDataFrameFromJsonFile {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("CreateDataFrameFromJsonFile");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		
		/**
		 * 创建sqlcontext  (org.apache.spark.sql.SQLContext)
		 */
		SQLContext sqlContext = new SQLContext(jsc);
		
		/**
		 * 创建DataFrame，DataFrame的底层是一个个的RDD，RDD的泛型是Row。
		 * DataFrame = RDD<Row>
		 * 以下两种方式创建都可以
		 */
//		DataFrame df = sqlContext.read().format("json").load("./sparksql/json.txt");
		DataFrame df = sqlContext.read().json("./sparksql/json.txt");
		
		/**
		 * 显示 DataFrame中的内容，默认显示前20行。
		 * 注意：当有多个列时，显示的列先后顺序是按列的ascii码先后显示。
		 */
		df.show();
		
		/**
		 * 以树结构的形式显示DataFrame的Schema
		 */
		df.printSchema();
		
		/**
		 * 只查询“name”这列
		 */
		df.select("name").show();
		
		/**
		 * 查询所有的行，给“age”这一列加1,并显示新的列为“newage”，plus方法会自动把String类型自动转成int+1，再转换成String型。
		 * 注意：原来为null的不会加1
		 * 
		 * 相当于：SELECT name,age+1 as newage from table 
		 */
		df.select(df.col("name"), df.col("age").plus(1).as("newage")).show();
		df.printSchema();
		
		/**
		 * 查找所有的“age”加1大于19的人
		 * 相当于：SELECT name,age FROM table WHERE (age+1) > 19
		 */
		df.filter(df.col("age").plus(1).gt("19")).show();
		
		
		/**
		 * 以“age”分组，查询各类age的人数 
		 * 相当于：SELECT COUNT(*) FROM p GROUP BY age
		 */
		df.groupBy("age").count().show();
		
		System.out.println("--------------------------------------------------------------");
		
		/**
		 * 将DataFrame注册成临时一张表dftable，方便使用sql语句查询，dftable这张表不会雾化到磁盘，只是逻辑上的表，存在内存中
		 */
		df.registerTempTable("dftable");
		
		/**
		 * 使用类似于sql语句查询表
		 */
		DataFrame sql = sqlContext.sql("SELECT * from dftable");
		sql.show();
		
		sqlContext.sql("SELECT * FROM dftable t1 join dftable t2 ON t1.name = t2.name").show();
		
	}
}
