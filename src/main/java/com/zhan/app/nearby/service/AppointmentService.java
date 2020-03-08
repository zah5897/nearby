package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.bean.AppointmentTheme;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.dao.AppointmentDao;
import com.zhan.app.nearby.dao.CityDao;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.SpringContextUtil;

@Service
public class AppointmentService {

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private CityService cityService;

	public void save(Appointment appointment,String iosAddr) {
		appointment.setCreate_time(new Date());
		City c=praseIosAddrGetCity(iosAddr);
		if(c!=null) {
			appointment.setCity_id(c.getId());
		}
		appointmentDao.insert(appointment);
	}

	public City praseIosAddrGetCity(String ios_addr) {
		Map<String, Object> obj = JSONUtil.jsonToMap(ios_addr);
		String city = obj.get("City").toString();
//		String SubLocality = obj.get("SubLocality").toString();
//		String Street = obj.get("Street").toString();
		return cityService.getCityByName(city);
	}
	public List<Appointment> list(long user_id, Integer last_id, int count,Integer theme_id,Integer time_stage,String appointment_time,Integer city_id,String keyword) {
		return appointmentDao.queryAll(user_id,last_id,count,theme_id,time_stage,appointment_time,city_id,keyword);
	}
	public List<Appointment> mine(long user_id, Integer last_id, int count) {
		return appointmentDao.queryMine(user_id,last_id,count);
	}
	
	
	public int del(long user_id, Integer id) {
		return appointmentDao.deleteById(user_id,id);
	}

	public List<AppointmentTheme> listTheme() {
		
		return appointmentDao.listTheme();
	}
	
	
	public List<Appointment> listToCheck(int status,int page,int count) {
		return appointmentDao.queryAllToCheck(status,page,count);
	}
	public int getCheckCount(int status) {
		return appointmentDao.getCheckCount(status);
	}

	public void changeStatus(int id, int newStatus) {
		appointmentDao.changeStatus(id,newStatus);
	}
 
}
