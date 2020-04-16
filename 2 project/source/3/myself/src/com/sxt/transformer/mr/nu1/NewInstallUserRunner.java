package com.sxt.transformer.mr.nu1;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.sxt.common.EventLogConstants;
import com.sxt.common.EventLogConstants.EventEnum;
import com.sxt.common.GlobalConstants;
import com.sxt.transformer.model.dim.StatsUserDimension;
import com.sxt.transformer.model.value.map.TimeOutputValue;
import com.sxt.transformer.model.value.reduce.MapWritableValue;
import com.sxt.transformer.mr.TransformerOutputFormat;
import com.sxt.util.TimeUtil;

public class NewInstallUserRunner implements Tool {

	// 1,入口
	Configuration conf = null;

	public static void main(String[] args) {
		try {
			ToolRunner.run(new Configuration(), new NewInstallUserRunner(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 2,完成实现方法
	@Override
	public Configuration getConf() {

		return this.conf;
	}

	// 2,1 连接集群，弄mysql ，得conf
	@Override
	public void setConf(Configuration conf) {
		conf.set("fs.defaultFS", "hdfs://node1:8020");
		conf.set("hbase.zookeeper.quorum", "node2,node3,node4");
		// 为了存到mysql
		conf.addResource("output-collector.xml");
		conf.addResource("query-mapping.xml");
		conf.addResource("transformer-env.xml");

		this.conf = HBaseConfiguration.create(conf);
	}
	//2,2 job的设置主题
	@Override
	public int run(String[] args) throws Exception {
		
		
		Configuration conf =this.getConf();
	//3,1 解析参数
		this.processArgs(conf, args);
		
		Job job = Job.getInstance(conf, "num of increasing");
		job.setJarByClass(NewInstallUserRunner.class);
		
//		1)hbase--->map  (方法里已经设置好了map的输出格式，以及一系列map的)
		TableMapReduceUtil.initTableMapperJob(getScans(conf), NewInstallUserMapper.class, StatsUserDimension.class, 
				TimeOutputValue.class, job, false);
		
//		2)re : 
		job.setReducerClass(NewInstallUserReducer.class);
		job.setOutputKeyClass(StatsUserDimension.class);
		job.setOutputValueClass(MapWritableValue.class);
		
//		3) 输出mysql
		job.setOutputFormatClass(TransformerOutputFormat.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	
	//3,1
	/**
	 * 解析参数 参数格式：-d 2018-12-29  : 分析 哪天的
	 * 
	 * @param conf
	 * @param args
	 */
	private void processArgs(Configuration conf, String[] args) {
		String date = null;
		for (int i = 0; i < args.length; i++) {
			if ("-d".equals(args[i])) {
				if (i + 1 < args.length) {
					date = args[++i];
				}
			}
		}
		//import org.apache.commons.lang.StringUtils;
		if (StringUtils.isNotBlank(date) || !TimeUtil.isValidateRunningDate(date)) {
			date = TimeUtil.getYesterday();
		}

		conf.set(GlobalConstants.RUNNING_DATE_PARAMES, date);
	}
	
	//3,2  获取给定天的数据
	/**
	 * 从hbase获取符合条件的数据 条件 
	 * 1、时间范围 
	 * 2、事件类型（en=e_l）
	 *  3、获取部分列
	 * 
	 * @param conf
	 * @return
	 */
	private List<Scan> getScans(Configuration conf) {
		// 获取时间
		String date = conf.get(GlobalConstants.RUNNING_DATE_PARAMES);
		long time = TimeUtil.parseString2Long(date);
		
		String startRow = String.valueOf(time);
		String stopRow = String.valueOf(time + GlobalConstants.DAY_OF_MILLISECONDS);
		
		
		Scan scan = new Scan();
		//1，)
		scan.setStartRow(startRow.getBytes());
		scan.setStopRow(stopRow.getBytes());
		
		
		//2,)过滤器
		FilterList filters = new FilterList(FilterList.Operator.MUST_PASS_ALL);

		//2.1)，,过滤特定数据  即L事件
		SingleColumnValueFilter filter1 = new SingleColumnValueFilter(
				EventLogConstants.EVENT_LOGS_FAMILY_NAME.getBytes(),
				EventLogConstants.LOG_COLUMN_NAME_EVENT_NAME.getBytes(), CompareOp.EQUAL,
				EventEnum.LAUNCH.alias.getBytes());
		filters.addFilter(filter1);
		
		//2.2)
		String[] columns = new String[] { EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME,
				EventLogConstants.LOG_COLUMN_NAME_PLATFORM, EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME,
				EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION, EventLogConstants.LOG_COLUMN_NAME_UUID };
		filters.addFilter(getFilter(columns));		
		scan.setFilter(filters);
		
		
		//制定从哪个hbase表中获取数据
		scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, EventLogConstants.HBASE_NAME_EVENT_LOGS.getBytes());
		return Arrays.asList(scan);
	}

	/**
	 * 获取某些制定的列
	 * @param columns
	 * @return
	 */
	private Filter getFilter(String[] columns) {
		
		int length = columns.length;
		byte[][] b = new byte[length][];
		for(int i = 0;i<length;i++){
			b[i] = columns[i].getBytes();
		}
		
		return new MultipleColumnPrefixFilter(b);
	}

}
