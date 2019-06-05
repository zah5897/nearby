package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.dao.TopicDao;
import com.zhan.app.nearby.dao.UserDynamicDao;

@Service
public class TopicService {
	@Resource
	private TopicDao topicDao;
	@Resource
	private UserDynamicDao userDynamicDao;

	public void insert(Topic topic) {
		long id = topicDao.insert(topic);
		topic.setId(id);
	}

	public List<Topic> list() {
		return topicDao.list();
	}
	public List<Topic> history(long current_topic_id) {
		return topicDao.history(current_topic_id);
	}

	public Topic top() {
		return topicDao.top();
	}

	public List<UserDynamic> listDynamics(long topic_id, long last_id, int page_size) {
		return userDynamicDao.getSelectedDynamicByTopic(topic_id, ImageStatus.SELECTED, last_id, page_size);
	}

}
