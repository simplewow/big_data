package sparksql.dataframe;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import sparksql.Person;
/**
 * 非json的RDD转换成DataFrame
 * 使用反射的方式将RDD转换成为DataFrame
 * 1、自定义的类必须是public
 * 2、自定义的类必须是可序列化的
 * 3、RDD转成DataFrame的时候，会根据自定义类中的字段名进行排序。
 * 
 * @author root
 *
 */
public class CreateDataFrameFromRDDWithReflect {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("CreateDataFrameFromRDD");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		SQLContext sqlContext = new SQLContext(jsc);
		
		JavaRDD<String> textFile = jsc.textFile("./sparksql/rddfile.txt");
		JavaRDD<Person> map = textFile.map(new Function<String, Person>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Person call(String line) throws Exception {
				String[] split = line.split(" ");
				Person person = new Person();
				person.setId(Integer.valueOf(split[0]));
				person.setName(split[1]);
				person.setGender(split[2]);
				person.setAge(Integer.valueOf(split[3]));
				return person;
			}
		});
		
		DataFrame df = sqlContext.createDataFrame(map, Person.class);
		df.registerTempTable("person");
		
		DataFrame sql = sqlContext.sql("select name from person where id >2");
		
		sql.show();
		/**
		 * DataFrame转换成JavaRDD
		 */
		JavaRDD<Row> javaRDD = df.javaRDD();
		javaRDD.map(new Function<Row, Person>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Person call(Row row) throws Exception {
				Integer id = row.getAs("id");
				String name = row.getAs("name");
				String gender = row.getAs("gender");
				Integer age = row.getAs("age");
				Person person = new Person(id,name,gender,age);
				return person;
			}
		}).foreach(new VoidFunction<Person>() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void call(Person t) throws Exception {
				System.out.println(t.toString());
			}
		});
	}
}
