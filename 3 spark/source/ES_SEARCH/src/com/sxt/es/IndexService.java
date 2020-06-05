package com.sxt.es;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryParser;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.springframework.stereotype.Service;

import com.sxt.util.HtmlTool;

@Service
public class IndexService {

	//存放html文件的目录
	public static String DATA_DIR="d:\\data\\";
	
	public static Client client;

	//连接
	static {
		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", "es1").build();
		try {
			client = TransportClient
					.builder()
					.settings(settings)
					.build()
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress
									.getByName("192.168.18.31"), 9300))
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress
									.getByName("192.168.18.32"), 9300))
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress
									.getByName("192.168.18.33"), 9300));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * admin():管理索引库的。client.admin().indices()
	 * 
	 * 索引数据的管理：client.prepare
	 * 
	 */
	@Test
	public void createIndex() throws Exception {
		IndicesExistsResponse resp = client.admin().indices().prepareExists("bjsxt").execute().actionGet();
		if(resp.isExists()){
			client.admin().indices().prepareDelete("bjsxt").execute().actionGet();
		}
		client.admin().indices().prepareCreate("bjsxt").execute().actionGet();

		new XContentFactory();

		XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
				.startObject("htmlbean").startObject("properties")
				.startObject("title").field("type", "string")
				.field("store", "yes").field("analyzer", "ik_max_word")
				.field("search_analyzer", "ik_max_word").endObject()
				.startObject("content").field("type", "string")
				.field("store", "yes").field("analyzer", "ik_max_word")
				.field("search_analyzer", "ik_max_word").endObject()
//				.startObject("url").field("type", "string")
//				.field("store", "yes").field("analyzer", "ik_max_word")
//				.field("search_analyzer", "ik_max_word").endObject()
				.endObject().endObject().endObject();
		PutMappingRequest mapping = Requests.putMappingRequest("bjsxt").type("htmlbean").source(builder);
		client.admin().indices().putMapping(mapping).actionGet();

	}
	
	/**
	 * 把源数据html文件添加到索引库中（构建索引文件）
	 */
	@Test
	public void addHtmlToES(){
		readHtml(new File(DATA_DIR));
	}
	
	/**
	 * 遍历数据文件目录d:/data ，递归方法
	 * @param file
	 */
	public void readHtml(File file){
		if(file.isDirectory()){
			File[]  fs =file.listFiles();
			for (int i = 0; i < fs.length; i++) {
				File f = fs[i];
				readHtml(f);
			}
		}else{
			HtmlBean bean;
			try {
				bean = HtmlTool.parserHtml(file.getPath());
				if(bean!=null){
					Map<String, String> dataMap =new HashMap<String, String>();
					dataMap.put("title", bean.getTitle());
					dataMap.put("content", bean.getContent());
					dataMap.put("url", bean.getUrl());
					//写索引
					client.prepareIndex("bjsxt", "htmlbean").setSource(dataMap).execute().actionGet();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 搜索
	 * @param kw
	 * @param num
	 * @return
	 */
	public PageBean<HtmlBean> search(String kw,int num,int count){
		PageBean<HtmlBean> wr =new PageBean<HtmlBean>();
		wr.setIndex(num);		
		MultiMatchQueryBuilder q =new MultiMatchQueryBuilder(kw, new String[]{"title","content"});
		SearchResponse resp=null;
		if(wr.getIndex()==1){
			resp = client.prepareSearch("bjsxt")
					.setTypes("htmlbean")
					.setQuery(q)
					.addHighlightedField("title")
					.addHighlightedField("content")
					.setHighlighterPreTags("<font color=\"red\">")
					.setHighlighterPostTags("</font>")
					.setHighlighterFragmentSize(40)//设置显示结果中一个碎片段的长度
					.setHighlighterNumOfFragments(5)//设置显示结果中每个结果最多显示碎片段，每个碎片段之间用...隔开
					.setFrom(0)
					.setSize(10)
					.execute().actionGet();
			
		}else{
			wr.setTotalCount(count);
			resp = client.prepareSearch("bjsxt")
					.setTypes("htmlbean")
					.setQuery(q)
					.addHighlightedField("title")
					.addHighlightedField("content")
					.setHighlighterPreTags("<font color=\"red\">")
					.setHighlighterPostTags("</font>")
					.setHighlighterFragmentSize(40)
					.setHighlighterNumOfFragments(5)
					.setFrom(wr.getStartRow())
					.setSize(10)
					.execute().actionGet();
		}
		SearchHits hits= resp.getHits();
		wr.setTotalCount((int)hits.getTotalHits());
		
		for(SearchHit hit : hits.getHits()){
			HtmlBean bean =new HtmlBean();
			if(hit.getHighlightFields().get("title")==null){//title中没有包含关键字
				bean.setTitle(hit.getSource().get("title").toString());//获取原来的title（没有高亮的title）
			}else{
				bean.setTitle(hit.getHighlightFields().get("title").getFragments()[0].toString());
			}
			if(hit.getHighlightFields().get("content")==null){//title中没有包含关键字
				bean.setContent(hit.getSource().get("content").toString());//获取原来的title（没有高亮的title）
			}else{
				StringBuilder sb =new StringBuilder();
				for(Text text: hit.getHighlightFields().get("content").getFragments()){
					sb.append(text.toString()+"...");
				}
				bean.setContent(sb.toString());
			}
			
			bean.setUrl("http://"+hit.getSource().get("url").toString());
			wr.setBean(bean);
			
		}
//		//构建查询条件
//		MatchQueryBuilder q1 =new MatchQueryBuilder("title", kw);
//		MatchQueryBuilder q2 =new MatchQueryBuilder("content", kw);
//		
//		//构建一个多条件查询对象
//		BoolQueryBuilder q =new BoolQueryBuilder(); //组合查询条件对象
//		q.should(q1);
//		q.should(q2);
		
//		RangeQueryBuilder q1 =new RangeQueryBuilder("age");
//		q1.from(18);
//		q1.to(40);		
		
		return wr;
	}
	
	
//	@Test
//	public void del(){
////		client.admin().indices().prepareDelete("bjsxt").execute().actionGet();
//		client.admin().indices().prepareDelete("bjsxt2").execute().actionGet();
//	}
}
