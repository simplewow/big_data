package sparksql.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import sparksql.Person;


public class SparkSQLDemo {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("SparkSQLDemo");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlContext = new SQLContext(jsc);
		
		/**
		 * 读取 json文件
		 */
		DataFrame json = sqlContext.read().json("./sparksql/scorejson.txt");
		json.registerTempTable("score");
		
		/**
		 * List<Person> 类型转换成RDD
		 */
		List<Person> list = new ArrayList<Person>();
		list.add(new Person(1, "zhangsan", "m", 10));
		list.add(new Person(2, "lisi", "f", 11));
		list.add(new Person(3, "wangwu", "m", 12));
		DataFrame createDataFrame = sqlContext.createDataFrame(list, Person.class);
		createDataFrame.registerTempTable("person1");
		
		/**
		 * structType 结构创建DataFrame
		 */
		JavaRDD<String> textFile = jsc.textFile("./sparksql/rddfile.txt");
		JavaRDD<Row> map = textFile.map(new Function<String, Row>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Row call(String line) throws Exception {
				String[] split = line.split(" ");
				return RowFactory.create(Integer.valueOf(split[0]),split[1],split[2],Integer.valueOf(split[3]));
			}
		});
		
		List<StructField> fields = new ArrayList<StructField>();
		fields.add(DataTypes.createStructField("id", DataTypes.IntegerType, true));
		fields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
		fields.add(DataTypes.createStructField("gender", DataTypes.StringType, true));
		fields.add(DataTypes.createStructField("age", DataTypes.IntegerType, true));
		
		StructType createStructType = DataTypes.createStructType(fields);
		
		DataFrame createDataFrame2 = sqlContext.createDataFrame(map, createStructType);
		createDataFrame2.registerTempTable("person");
		
		List<String> collect = createDataFrame.javaRDD().map(new Function<Row, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String call(Row v1) throws Exception {
				String name = v1.getAs("name");
				return name;
			}
		}).collect();
		
		String sql = "select * from person t where t.name in (";
		for(String s : collect){
			sql += "'"+s+"',";
		}
		sql = sql.substring(0, sql.length()-1);
		sql+= ")";
		
		sqlContext.sql(sql).show();
		
	}
}
