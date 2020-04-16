package com.sxt.transformer.mr.nu1;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;

import com.sxt.common.KpiType;
import com.sxt.transformer.model.dim.StatsUserDimension;
import com.sxt.transformer.model.value.map.TimeOutputValue;
import com.sxt.transformer.model.value.reduce.MapWritableValue;

/**
 * reduce：将相同的key的数据汇聚到一起，得到最终的结果
 * 	输入：k:statsUserDimenion
 * 		v:timeOutputValue(uuid,time)
 *	输出：k:StatsUserDimension
 *		v:mapWritableValue
 * @author root
 *
 */
public class NewInstallUserReducer extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, MapWritableValue>{

	//创建reduce端输出的value对象
	MapWritableValue mapWritableValue = new MapWritableValue();
	//创建去重的集合对象set  : 同个k下，同个v不会重复
	Set<String> unique = new HashSet<String>();
	
	@Override
	protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> iter,
			Context context)
			throws IOException, InterruptedException {
		//清空set集合，防止上一个迭代器留下的值产生影响
		this.unique.clear();
		//遍历迭代器,将set集合的大小作为最终的统计结果
		for (TimeOutputValue timeOutputValue : iter) {
			this.unique.add(timeOutputValue.getId());
		}
		//存放最终的计算结果，map的key是一个唯一标识，方便取值，value是集合的大小，最终的统计结果
		MapWritable map  = new MapWritable();
		map.put(new IntWritable(-1), new IntWritable(this.unique.size()));
		//将map结果放置到reduce输出的value对象中
		mapWritableValue.setValue(map);
		
		
		//获取模块维度名称标识    ：存的是枚举的名字，，现在拿名字得到枚举的值
		String kpiName = key.getStatsCommon().getKpi().getKpiName();
		//将kpiType设置到reduce端输出的对象中
		if(KpiType.NEW_INSTALL_USER.name.equals(kpiName)){
			mapWritableValue.setKpi(KpiType.NEW_INSTALL_USER);
		}else if(KpiType.BROWSER_NEW_INSTALL_USER.name.equals(kpiName)){
			mapWritableValue.setKpi(KpiType.BROWSER_NEW_INSTALL_USER);
		}
		context.write(key, mapWritableValue);
		
	}
}
