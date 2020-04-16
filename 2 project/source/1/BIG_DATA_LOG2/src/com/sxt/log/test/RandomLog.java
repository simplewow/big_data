package com.sxt.log.test;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomLog {
	public static String day = "20160610";
	public static String pre_url = "http%3A%2F%2Flocalhost%3A8080%2FBIG_DATA_LOG2%2Fdemo";
	static String[] enArr = { "e_l", "e_pv" };
	static List<String> uids = new ArrayList<String>();
	static List<String> sids = new ArrayList<String>();
	static String[] mids = { "Mary", "Patricia", "Linda", "Barbara",
			"Elizabeth", "Jennifer", "Maria", "Susan", "Margaret", "Dorothy",
			"Lisa", "Nancy", "Karen", "Betty", "Helen", "Sandra", "Donna",
			"Carol", "Ruth", "Sharon", "Michelle", "Laura", "Sarah",
			"Kimberly", "Deborah", "Jessica", "Shirley", "Cynthia", "Angela",
			"Melissa", "Brenda", "Amy", "Anna", "Rebecca", "Virginia",
			"Kathleen", "Pamela", "Martha", "Debra", "Amanda", "Stephanie",
			"Carolyn", "Christine", "Marie", "Janet", "Catherine", "Frances",
			"Ann", "Joyce", "Diane", "Alice", "Julie", "Heather", "Teresa",
			"Doris", "Gloria", "Evelyn", "Jean", "Cheryl", "Mildred",
			"Katherine", "Joan", "Ashley", "Judith", "Rose", "Janice", "Kelly",
			"Nicole", "Judy", "Christina", "Kathy", "Theresa", "Beverly",
			"Denise", "Tammy", "Irene", "Jane", "Lori", "Rachel", "Marilyn",
			"Andrea", "Kathryn", "Louise", "Sara", "Anne", "Jacqueline",
			"Wanda", "Bonnie", "Julia", "Ruby", "Lois", "Tina", "Phyllis",
			"Norma", "Paula", "Diana", "Annie", "Lillian", "Emily", "Robin",
			"Peggy", "Crystal", "Gladys", "Rita", "Dawn", "Connie", "Florence",
			"Tracy", "Edna", "Tiffany", "Carmen", "Rosa", "Cindy", "Grace",
			"Wendy", "Victoria", "Edith", "Kim", "Sherry", "Sylvia",
			"Josephine", "Thelma", "Shannon", "Sheila", "Ethel", "Ellen",
			"Elaine", "Marjorie", "Carrie", "Charlotte", "Monica", "Esther",
			"Pauline", "Emma", "Juanita", "Anita", "Rhonda", "Hazel", "Amber",
			"Eva", "Debbie", "April", "Leslie", "Clara", "Lucille", "Jamie",
			"Joanne", "Eleanor", "Valerie", "Danielle", "Megan", "Alicia",
			"Suzanne", "Michele", "Gail", "Bertha", "Darlene", "Veronica",
			"Jill", "Erin", "Geraldine", "Lauren", "Cathy", "Joann",
			"Lorraine", "Lynn", "Sally", "Regina", "Erica", "Beatrice",
			"Dolores", "Bernice", "Audrey", "Yvonne", "Annette", "June",
			"Samantha", "Marion", "Dana", "Stacy", "Ana", "Renee", "Ida",
			"Vivian", "Roberta", "Holly", "Brittany", "Melanie", "Loretta",
			"Yolanda", "Jeanette", "Laurie", "Katie", "Kristen", "Vanessa",
			"Alma", "Sue", "Elsie", "Beth", "Jeanne" };

	static Random r = new Random();

	static String[] browsers = {
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; GWX:QUALIFIED; rv:11.0) like Gecko",
			"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0" };

	public static void main(String[] args) {
		FileWriter writer = null;
		try {
			writer = new FileWriter("D://access.log", true);

			for (int j = 0; j < 50000; j++) {
				String[] d = getDatetime(day);
				StringBuilder str = new StringBuilder();
				int i1 = r.nextInt(255);
				int i2 = r.nextInt(255);
				int i3 = r.nextInt(255);
				int i4 = r.nextInt(255);
				str.append(i1 + "." + i2 + "." + i3 + "." + i4);
				str.append("^A" + d[0]);
				str.append("^A" + 59410 + "^A/log.gif?");
				str.append("ver=1&pl=website&sdk=js&l=zh-CN&b_rst=1536*864&c_time="
						+ d[1]);

				if ((r.nextInt(10) > 4 && uids.size() < 10000) || j < 20) {// 新用户
					String uid = UUID.randomUUID().toString();
					String sid = UUID.randomUUID().toString();
					uids.add(uid);
					sids.add(sid);
					String browserInfo = browsers[r.nextInt(3)];
					writer.write(getP_l(str, uid, sid, browserInfo));
					writer.write(getP_V(str, uid, sid,
							mids[r.nextInt(mids.length)], browserInfo));

				} else {
					int i = r.nextInt(uids.size());
					String uid = uids.get(i);
					String sid = sids.get(i);
					String browserInfo = browsers[r.nextInt(3)];
					writer.write(getP_V(str, uid, sid,
							mids[r.nextInt(mids.length)], browserInfo));
				}

			}
			System.out.println(sids.size() + "-----" + uids.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getP_l(StringBuilder str, String uid, String sid,
			String binfo) throws Exception {
		str.append("&en=e_l&u_ud=" + uid);
		str.append("&u_sd=" + sid);
		str.append("&b_iev=" + URLEncoder.encode(binfo, "UTF-8"));
		str.append("\r\n");
		return str.toString();
	}

	public static String getP_V(StringBuilder str, String uid, String sid,
			String mid, String binfo) throws Exception {
		str.append("&en=e_pv&u_ud=" + uid);
		str.append("&u_sd=" + sid);
		str.append("&u_mid=" + sid);
		str.append("&b_iev=" + URLEncoder.encode(binfo, "UTF-8"));
		int i = r.nextInt(1000);
		if (i < 900) {
			str.append("&p_url=" + pre_url + r.nextInt(200) + ".jsp");
			str.append("&p_ref=" + pre_url + r.nextInt(200) + ".jsp");
		} else {
			str.append("&p_url=" + pre_url + r.nextInt(200) + ".jsp");
		}
		str.append("\r\n");
		return str.toString();
	}

	public static String[] getDatetime(String day) throws Exception {
		String d = day
				+ String.format(
						"%02d%02d%02d",
						new Object[] { r.nextInt(24), r.nextInt(60),
								r.nextInt(60) });
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(d);
		String datetime = date.getTime() + "";
		String prefix = datetime.substring(0, datetime.length() - 3);
		String i = r.nextInt(1000) + "";
		if (i.length() == 2) {
			i = "0" + i;
		} else if (i.length() == 1) {
			i = "00" + i;
		}
		return new String[] { prefix + "." + i, datetime };
	}
}
