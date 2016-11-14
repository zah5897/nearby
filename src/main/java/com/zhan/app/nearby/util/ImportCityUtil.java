package com.zhan.app.nearby.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.service.CityService;

public class ImportCityUtil {
	public static void importCity(CityService cityService) {
		try {
			Document doc = Jsoup.parse(new File("C:/Users/zah/Desktop/arrays.xml"), "utf-8");
			Elements eles = doc.select("string-array[name='province']").first().children();
			System.out.println(eles.size());
			List<City> province = new ArrayList<City>();
			int id = 1;
			for (Element e : eles) {
				String name = e.text();
				City c = new City();
				c.setId(id);
				c.setName(name);
				c.setType(0);
				province.add(c);
				cityService.insert(c);
				id++;
			}
			Elements cities = doc.select("string-array[name='city']").first().children();

			int index = 0;
			for (Element e : cities) {
				String name = e.text();
				String[] city = name.split(",");
				List<City> cs = new ArrayList<City>();
				for (int i = 0; i < city.length; i++) {
					City c = new City();
					c.setId(id);
					c.setName(city[i]);
					c.setType(1);
					c.setParent_id(province.get(index).getId());
					cs.add(c);
					province.get(index).setChildren(cs);
					cityService.insert(c);
					id++;
				}
				index++;
			}
			System.out.println(JSON.toJSON(province));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
