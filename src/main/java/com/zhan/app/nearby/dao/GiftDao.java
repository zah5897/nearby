package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.util.TextUtils;

@Repository("giftDao")
public class GiftDao extends BaseDao {
	public static final String TABLE_NAME = "t_gift";
	@Resource
	private JdbcTemplate jdbcTemplate;

	public long insert(Gift gift) {
		long id = saveObj(jdbcTemplate, TABLE_NAME, gift);
		gift.setId(id);
		return id;
	}

	public List<Gift> listGifts() {
		return jdbcTemplate.query("select *from " + TABLE_NAME, new BeanPropertyRowMapper<Gift>(Gift.class));
	}

	public void delete(long id) {
		jdbcTemplate.update("delete from " + TABLE_NAME + " where id=?", new Object[] { id });
	}

	public void update(Gift gift) {
		if (TextUtils.isEmpty(gift.getImage_url())) {
            jdbcTemplate.update("update "+TABLE_NAME+" set name=?,price=?,old_price=?,description=?,remark=? where id=?",new Object[]{gift.getName(),gift.getPrice(),gift.getOld_price(),gift.getDescription(),gift.getRemark(),gift.getId()});
		}else{
			 jdbcTemplate.update("update "+TABLE_NAME+" set name=?,price=?,old_price=?,image_url=?,description=?,remark=? where id=?",new Object[]{gift.getName(),gift.getPrice(),gift.getOld_price(),gift.getImage_url(),gift.getDescription(),gift.getRemark(),gift.getId()});
		}
	}
}
