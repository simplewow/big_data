package com.sxt.transformer.mr.nu1;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import com.sxt.common.DateEnum;
import com.sxt.common.EventLogConstants;
import com.sxt.common.KpiType;
import com.sxt.transformer.model.dim.StatsCommonDimension;
import com.sxt.transformer.model.dim.StatsUserDimension;
import com.sxt.transformer.model.dim.base.BrowserDimension;
import com.sxt.transformer.model.dim.base.DateDimension;
import com.sxt.transformer.model.dim.base.KpiDimension;
import com.sxt.transformer.model.dim.base.PlatformDimension;
import com.sxt.transformer.model.value.map.TimeOutputValue;

//只写输出的 k,维度组合，，v：唯一标识（用的uuid 和 时间，）
public class NewInstallUserMapper extends TableMapper<StatsUserDimension, TimeOutputValue> {
	// 创建map端输出的key的对象和value的对象
	StatsUserDimension statsUserDimension = new StatsUserDimension();
	TimeOutputValue timeOutputValue = new TimeOutputValue();

	// 定义列族
	byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);
	// 定义模块维度 标识
	// 用户基本信息模块
	KpiDimension newInstallUser = new KpiDimension(KpiType.NEW_INSTALL_USER.name);
	// 浏览器模块
	KpiDimension InstallUserBrowser = new KpiDimension(KpiType.BROWSER_NEW_INSTALL_USER.name);

	@Override
		protected void map(ImmutableBytesWritable key, Result value,
				Context context)
				throws IOException, InterruptedException {
			
//			1）单维度对象
			//从hbase中获取时间的值
			String date = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME))));
			
			long time = Long.valueOf(date);
			//构建时间维度  ：这个函数，天是最低单位了，如果是其他的，就把后面单位设置为0，返回
			DateDimension  dateDimension = DateDimension.buildDate(time, DateEnum.DAY);
			
			//获取平台维度的值  （本身 + all）
			String platform = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_PLATFORM))));
			//构建平台维度
			List<PlatformDimension> platformDimensions = PlatformDimension.buildList(platform);
			
			//获取浏览器的名称和版本  （（本身 + all））
			String browserName = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME))));
			String browserVersion = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION))));
			//构建浏览器维度
			List<BrowserDimension> browserDimensions = BrowserDimension.buildList(browserName, browserVersion);
			
//			2）标识对象
			//获取用户唯一标识
			String uuid = Bytes.toString(CellUtil.cloneValue(value.getColumnLatestCell(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_UUID))));
			//给输出的value对象赋值
			timeOutputValue.setId(uuid);
			timeOutputValue.setTime(time);

//			3）
			//拼接维度组合  ： 两大类： user ： 时间 + 平台   + 标识   browser： 时间 +平台 + 标识 ，  时间两大类一样（外面写），公用平台，重写kpi
			//无论维度组合如何组合，时间维度总是一致的，所以先设置时间维度
			StatsCommonDimension statsCommon = statsUserDimension.getStatsCommon();
			statsCommon.setDate(dateDimension);
			
			//创建一个空的浏览器维度
			BrowserDimension defaultBrowser = new BrowserDimension("","");
			//其他维度组合
			for (PlatformDimension pl:platformDimensions) {
				statsCommon.setKpi(newInstallUser);
				statsCommon.setPlatform(pl);
				statsUserDimension.setBrowser(defaultBrowser);
				context.write(statsUserDimension, timeOutputValue);
				for (BrowserDimension bd : browserDimensions) {
					statsCommon.setKpi(InstallUserBrowser);
					statsUserDimension.setBrowser(bd);
					context.write(statsUserDimension, timeOutputValue);
				}
			}
	}

}
