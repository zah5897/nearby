package com.weibo;

import java.util.List;

import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;

public class BlogBizImp {

	private String token = "2.008vg3PG0IS2Gw5c396a427692kCTB";
	Timeline tm;

	public void load() {
		tm = new Timeline(token);
		weibo4j.model.Paging page = new weibo4j.model.Paging();
		page.setPage(1);
		page.setCount(10);
		try {
			StatusWapper status = tm.getHomeTimeline(0, 1, page);// tm.getHomeTimeline();
			if (status != null && status.getStatuses() != null && status.getStatuses().size() > 0) {
				List<Status> list = status.getStatuses();
			}

		} catch (

		Exception e) {
			e.getStackTrace();
			// TODO: handle exception
		}

	}
	
	
	public static void main(String[] args) {
		new BlogBizImp().load();
	}

}
