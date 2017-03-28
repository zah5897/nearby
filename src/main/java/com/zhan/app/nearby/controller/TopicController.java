package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.service.TopicService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/topic")
public class TopicController {
	@Resource
	private TopicService topicService;

	private static Logger log = Logger.getLogger(TopicController.class);

	@RequestMapping("list")
	public ModelMap list(HttpServletRequest request) {
		ModelMap result = ResultUtil.getResultOKMap();
		List<Topic> topics = topicService.list();
		ImagePathUtil.completeTopicImagePath(topics, true);
		result.put("topics", topics);
		return result;
	}

	@RequestMapping("top")
	public ModelMap top(HttpServletRequest request) {
		ModelMap result = ResultUtil.getResultOKMap();
		Topic topic = topicService.top();
		ImagePathUtil.completeTopicImagePath(topic, true);
		result.put("topic", topic);
		return result;
	}
}
