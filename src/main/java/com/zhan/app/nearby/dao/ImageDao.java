package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.comm.ImageStatus;

@Repository("imageDao")
public class ImageDao extends BaseDao {
	public static final String TABLE_USER_IMAGES = "t_user_image";
	public static final String TABLE_IMAGES_SELECTED = "t_image_selected";
	@Resource
	private JdbcTemplate jdbcTemplate;

	public long insertImage(Image image) {
		return saveObj(jdbcTemplate, TABLE_USER_IMAGES, image);
	}

	public List<Image> getImagesBySelectedState(ImageStatus status, long last_id, int page_size) {
		{

			String sql = "select *from " + TABLE_USER_IMAGES + " image right join " + TABLE_IMAGES_SELECTED
					+ " selected on image.id=selected.image_id and selected.selected_state=? and selected.image_id>? order by selected.image_id desc limit ?";

			return jdbcTemplate.query(sql, new Object[] { status.ordinal(), last_id, page_size },
					new BeanPropertyRowMapper<Image>(Image.class));
		}
	}
	
	public void addSelectedImage(long image_id){
		String sql="insert into "+TABLE_IMAGES_SELECTED+" values (?, ?)";
		jdbcTemplate.update(sql, new Object[]{image_id,ImageStatus.SELECTED.ordinal()});
	}
	
	 

}
