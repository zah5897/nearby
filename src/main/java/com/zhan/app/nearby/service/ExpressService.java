package com.zhan.app.nearby.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.Express;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.dao.ExpressDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ResultUtil;

@Service
@Transactional("transactionManager")
public class ExpressService {

	public static final int LIMIT_COUNT = 5;

	@Resource
	private ExpressDao expressDao;

	@Resource
	private DynamicMsgService dynamicMsgService;

	public ModelMap insert(Express express) {
		express.setCreate_time(new Date());
		expressDao.insert(express);
		if (express.getId() > 0) {
			dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_EXPRESS, express.getUser_id(), express.getId(),
					express.getTo_user_id(), "有人向你发起表白信");
			return ResultUtil.getResultOKMap();
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_SYS);
		}
	}

}
