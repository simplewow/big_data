package com.sxt.util;

import java.io.File;

import com.sxt.es.HtmlBean;
import com.sxt.es.IndexService;

import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class HtmlTool {

	
	
	/**
	 * 
	 * @param path html 文件路径
	 */
	public static HtmlBean parserHtml(String path)throws Throwable{
		HtmlBean bean  =new HtmlBean();
		Source source=new Source(new File(path));
		
		//全面序列化解析
		source.fullSequentialParse();
		
		//拿到一系列数据，封装doc 的bean
		//一级元素中，，，选择title
		Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
		if(titleElement==null){
			return null;
		}else{
			String title=CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
			bean.setTitle(title);
		}
		
		String content =source.getTextExtractor().setIncludeAttributes(true).toString();
		
		String url =path.substring(IndexService.DATA_DIR.length());
		bean.setContent(content);
		bean.setUrl(url);
		return bean;
  }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("haha");
		try {
//			System.out.println("haha");
			System.out.println(parserHtml("d:\\data\\news.cctv.com\\2017\\05\\01\\ARTI0k5MFLx2cvzQZffwQcUp170501.shtml").getContent());
//			System.out.println(parserHtml("d:\\data\\news.cctv.com\\2017\\05\\01\\ARTIxBwWjJETzHHDsPjmtIjn180124.html").getContent());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
