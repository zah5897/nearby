package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.service.TopicService;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/topic")
public class TopicController {
	@Resource
	private TopicService topicService;

	private static Logger log = Logger.getLogger(TopicController.class);

	/**
	 * 发现
	 * 
	 * @param user_id
	 * @param lat
	 * @param lng
	 * @param count
	 * @return
	 */
	@RequestMapping("add_topic")
	public ModelMap upload(HttpServletRequest request, Topic topic) {
		if (request instanceof DefaultMultipartHttpServletRequest) {
			DefaultMultipartHttpServletRequest multipartRequest = (DefaultMultipartHttpServletRequest) request;
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imagePath = ImageSaveUtils.saveUserImages(file, multipartRequest.getServletContext());
						topic.setIcon(imagePath);
						break;
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
						break;
					}
				}
			}
		}

		topic.setCreate_time(new Date());
		topicService.insert(topic);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("topic", topic);
		return result;

	}

	@RequestMapping("list")
	public ModelMap list(HttpServletRequest request, String lat, String lng) {
		ModelMap result=ResultUtil.getResultOKMap();
		List<Topic> topics=topicService.list();
		result.put("topics", topics);
		return result;
	}
}
