package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.dao.ManagerDao;

@Service
@Transactional("transactionManager")
public class ManagerService {
	@Resource
	private ManagerDao managerDao;
	@Resource
	private UserCacheService userCacheService;

	public int getHomeFoundSelectedCount() {
		return managerDao.getHomeFoundSelectedCount();
	}

	public List<UserDynamic> getHomeFoundSelected(int pageIndex, int pageSize) {
		return managerDao.getHomeFoundSelected(pageIndex, pageSize);
	}

	public int getUnSelectedCount() {
		return managerDao.getUnSelectedCount();
	}

	public List<UserDynamic> getUnSelected(int pageIndex, int pageSize) {
		return managerDao.getUnSelected(pageIndex, pageSize);
	}

	public int removeFromSelected(long id) {
		return managerDao.removeFromSelected(id);
	}
	public int addToSelected(long id) {
		return managerDao.addToSelected(id);
	}
	
	public boolean updateWelcome(String welcome){
		return userCacheService.setWelcome(welcome);
	}
	public String getWelcome(){
		return userCacheService.getWelcome();
	}
}
