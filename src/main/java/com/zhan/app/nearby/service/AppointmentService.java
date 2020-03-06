package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.bean.AppointmentTheme;
import com.zhan.app.nearby.dao.AppointmentDao;

@Service
public class AppointmentService {

	@Autowired
	private AppointmentDao appointmentDao;

	public void save(Appointment appointment) {
		appointment.setCreate_time(new Date());
		appointmentDao.insert(appointment);
	}

	public List<Appointment> list(long user_id, Integer last_id, int count) {
		return appointmentDao.queryAll(user_id,last_id,count);
	}

	public int del(long user_id, Integer id) {
		return appointmentDao.deleteById(user_id,id);
	}

	public List<AppointmentTheme> listTheme() {
		
		return appointmentDao.listTheme();
	}
 
}
