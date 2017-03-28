package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.dao.TopicDao;

@Service
@Transactional("transactionManager")
public class TopicService {
	@Resource
	private TopicDao topicDao;

	public void insert(Topic topic) {
		long id = topicDao.insert(topic);
		topic.setId(id);
	}

	public List<Topic> list() {
		return topicDao.list();
	}
	public  Topic top() {
		return topicDao.top();
	}

 

}
