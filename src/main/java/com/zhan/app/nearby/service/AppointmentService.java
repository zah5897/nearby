package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.bean.AppointmentTheme;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.dao.AppointmentDao;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service
public class AppointmentService {

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private CityService cityService;

	public void save(Appointment appointment,String iosAddr,String android_addr) {
		appointment.setCreate_time(new Date());
		City city=null;
		if(!TextUtils.isEmpty(iosAddr)) {
			city = praseIosAddrGetCity(iosAddr);
		}
		if(!TextUtils.isEmpty(android_addr)) {
			city = cityService.getCityByName(android_addr);
		}
		if (city != null) {
			appointment.setCity_id(city.getId());
		}
		appointmentDao.insert(appointment);
	}

	public City praseIosAddrGetCity(String ios_addr) {
		if (ios_addr == null || TextUtils.isEmpty(ios_addr.trim())) {
			City c = new City();
			c.setId(2);
			c.setName("上海市");
			return c;
		}

		Map<String, Object> obj = JSONUtil.jsonToMap(ios_addr);
		if (obj == null) {
			City c = new City();
			c.setId(2);
			c.setName("上海市");
			return c;
		}

		String city = obj.get("City").toString();
//		String SubLocality = obj.get("SubLocality").toString();
//		String Street = obj.get("Street").toString();
		return cityService.getCityByName(city);
	}

	public List<Appointment> list(long user_id, Integer last_id, int count, Integer theme_id, Integer time_stage,
			String appointment_time, Integer city_id, String keyword) {
		return appointmentDao.queryAll(user_id, last_id, count, theme_id, time_stage, appointment_time, city_id,
				keyword);
	}

	public List<Appointment> mine(long user_id, Integer last_id, int count) {
		return appointmentDao.queryMine(user_id, last_id, count);
	}
	public List<Appointment> loadUserAppointments(long uid, Integer last_id, int count) {
		return appointmentDao.loadUserAppointments(uid, last_id, count);
	}

	public int del(long user_id, Integer id) {
		return appointmentDao.deleteById(user_id, id);
	}

	public List<AppointmentTheme> listTheme() {

		return appointmentDao.listTheme();
	}

	public List<Appointment> listToCheck(int status, int page, int count) {
		return appointmentDao.queryAllToCheck(status, page, count);
	}

	public int getCheckCount(int status) {
		return appointmentDao.getCheckCount(status);
	}

	public void changeStatus(int id, int newStatus) {
		appointmentDao.changeStatus(id, newStatus);
	}

	public int getAppointMentUnlockCount(long user_id, int id) {
		return appointmentDao.getAppointMentUnlockCount(user_id,id);
	}
	public int getAppointMentTodayCount(long user_id) {
		return appointmentDao.getAppointMentTodayCount(user_id);
	}
	public void unlock(long user_id, int id) {
		appointmentDao.unlock(user_id,id);
	}

	public Appointment load(int id) {
		return appointmentDao.loadById(id);
	}

}
