package com.sxt.test;

import org.apache.hadoop.io.Text;

import com.sxt.transformer.hive.DateDimensionUDF;

public class Test {
public static void main(String[] args) {
	
	 Text t = new Text("2020-04-08");
	DateDimensionUDF d =new DateDimensionUDF();
	System.out.println(d.evaluate(t));
}
}
