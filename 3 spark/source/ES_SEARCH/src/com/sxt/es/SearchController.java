package com.sxt.es;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

	@Autowired
	private IndexService service;
	
	@RequestMapping("/search.do")
	public String search(String keyword,int num,int count,Model m){
		PageBean<HtmlBean> page =service.search(keyword, num, count);
		m.addAttribute("page", page);
		return "/index.jsp";
	}
}
