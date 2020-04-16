package com.sxt.log.test;

import java.util.Random;

import com.sxt.client.AnalyticsEngineSDK;

public class Test {
	public static String day = "20160607";
	static Random r = new Random();

	public static void main(String[] args) {
		AnalyticsEngineSDK.onChargeSuccess("orderid123", "zhangsan");
		AnalyticsEngineSDK.onChargeRefund("orderid456", "lisi");

//		try {
//			String d = day
//					+ String.format(
//							"%02d%02d%02d",
//							new Object[] { r.nextInt(24), r.nextInt(60),
//									r.nextInt(60) });
//			System.out.println(d);
//			Date date = new SimpleDateFormat("yyyyMMddhhmmss").parse(d);
//			System.out.println(date);
//			String datetime = date.getTime() + "";
//			System.out.println(datetime);
//			String prefix = datetime.substring(0, datetime.length() - 3);
//			System.out.println(prefix + "." + r.nextInt(1000));
//			System.out.println(new Date(1423634643000l));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}
}
