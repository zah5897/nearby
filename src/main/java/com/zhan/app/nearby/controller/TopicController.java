package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.service.TopicService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/topic")
public class TopicController {
	@Resource
	private TopicService topicService;

//	private static Logger log = Logger.getLogger(TopicController.class);

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

	@RequestMapping("history")
	public ModelMap history(HttpServletRequest request,Long current_topic_id) {
		ModelMap result = ResultUtil.getResultOKMap();
		List<Topic> topics = topicService.history(current_topic_id==null?0:current_topic_id);
		ImagePathUtil.completeTopicImagePath(topics, true);
		result.put("topics", topics);
		return result;
	}

	@RequestMapping("list_dynamics")
	public ModelMap list_dynamic(long topic_id, long last_id, int page_size) {
		ModelMap result = ResultUtil.getResultOKMap();
		List<UserDynamic> dynamics = topicService.listDynamics(topic_id, last_id, page_size);
		ImagePathUtil.completeDynamicsPath(dynamics, true);
		result.put("dynamics", dynamics);
		return result;
	}
}
