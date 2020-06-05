package sparksql.UDF_UDAF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF2;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class UDF {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("UDF").setMaster("local");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlContext = new SQLContext(jsc);
		
		List<String> list = Arrays.asList(
				"a",
				"b",
				"c",
				"dfasd",
				"edasf",
				"fdaf",
				"bdsa",
				"cdsa",
				"b",
				"c");
		
		JavaRDD<Row> map = jsc.parallelize(list).map(new Function<String, Row>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Row call(String s) throws Exception {
				
				return RowFactory.create(s);
			}
		});
		
		
		List<StructField> structFields= new ArrayList<StructField>();
		structFields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
		StructType createStructType = DataTypes.createStructType(structFields);
		
		DataFrame createDataFrame = sqlContext.createDataFrame(map, createStructType);
		createDataFrame.registerTempTable("tab");
		
		/**
		 * 注册一个udf函数,返回每条数据的长度
		 * 
		 * 注意：根据UDF函数参数的个数来决定是实现哪一个UDF  UDF1，UDF2 ...,UDF1就是传入一个参数，UDF2就是传入2个参数，依次类推。。。。
		 * 
		 */
		sqlContext.udf().register("StrLen", new UDF1<String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Integer call(String string) throws Exception {
				
				return string.length();
			}
		}, DataTypes.IntegerType);
		
		sqlContext.sql("select name ,StrLen(name) from tab").show();
		
		/**
		 * 注册一个udf，返回所有数据中拼成传入参数+~结尾返回
		 */
		sqlContext.udf().register("fun", new UDF2<String,Integer,String>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String call(String s, Integer c) throws Exception {
				return s+c+"~";
			}}, DataTypes.StringType);
		
		sqlContext.sql("select name ,fun(name,10) from tab").show();
	}
	
}
