package com.bjsxt.java.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Day01Java {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("test");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("./data/words");
        JavaRDD<String> sample = lines.sample(true, 0.1,100L);
        sample.foreach(new VoidFunction<String>() {
            @Override
            public void call(String s) throws Exception {
                System.out.println(s);
            }
        });

//        List<String> take = lines.take(3);
//        for(String one:take){
//            System.out.println(one);
//        }



//        String first = lines.first();
//        System.out.println(first);

//        long count = lines.count();
//        System.out.println(count);

//        List<String> collect = lines.collect();
//        for(String one:collect){
//            System.out.println(one);
//        }

//        JavaPairRDD<String, Integer> reduceRDD = lines.flatMap(new FlatMapFunction<String, String>() {
//            @Override
//            public Iterator<String> call(String s) throws Exception {
//                List<String> list = Arrays.asList(s.split(" "));
//                return list.iterator();
//            }
//        }).mapToPair(new PairFunction<String, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(String word) throws Exception {
//                return new Tuple2<String, Integer>(word, 1);
//            }
//        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer v1, Integer v2) throws Exception {
//                return v1 + v2;
//            }
//        });
//        reduceRDD.mapToPair(new PairFunction<Tuple2<String,Integer>, Integer, String>() {
//            @Override
//            public Tuple2<Integer, String> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
//                return stringIntegerTuple2.swap();
//            }
//        }).sortByKey(false).mapToPair(new PairFunction<Tuple2<Integer,String>, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(Tuple2<Integer, String> stringIntegerTuple2) throws Exception {
//                return stringIntegerTuple2.swap();
//            }
//        }).foreach(new VoidFunction<Tuple2<String, Integer>>() {
//            @Override
//            public void call(Tuple2<String, Integer> tp) throws Exception {
//                System.out.println(tp);
//            }
//        });

//        JavaPairRDD<String, String> result = lines.mapToPair(new PairFunction<String, String, String>() {
//            @Override
//            public Tuple2<String, String> call(String s) throws Exception {
//                return new Tuple2<String, String>(s, s + "#");
//            }
//        });


//        JavaRDD<Tuple2<String, String>> map = lines.map(new Function<String, Tuple2<String, String>>() {
//            @Override
//            public Tuple2<String, String> call(String line) throws Exception {
//                return new Tuple2<>(line, line + "*");
//            }
//        });
//        map.foreach(new VoidFunction<String>() {
//            @Override
//            public void call(String s) throws Exception {
//                System.out.println(s);
//            }
//        });

//        JavaRDD<String> result = lines.filter(new Function<String, Boolean>() {
//            @Override
//            public Boolean call(String line) throws Exception {
//                return "hello spark".equals(line);
//            }
//        });
//        long count = result.count();
//        System.out.println(count);
//        result.foreach(new VoidFunction<String>() {
//            @Override
//            public void call(String s) throws Exception {
//                System.out.println(s);
//            }
//        });

        sc.stop();

    }
}
