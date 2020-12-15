package com.hadluo.store.service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hadluo.store.api.pojo.Item;
import com.hadluo.store.api.pojo.ItemType;
import com.hadluo.store.api.pojo.MainBanner;
import com.hadluo.store.api.pojo.MainConfig;
import com.hadluo.store.api.pojo.MainItems;
import com.hadluo.store.service.mapper.ItemMapper;
import com.hadluo.store.service.mapper.ItemTypeMapper;
import com.hadluo.store.service.mapper.MainBannerMapper;
import com.hadluo.store.service.mapper.MainConfigMapper;
import com.hadluo.store.service.mapper.MainItemsMapper;
import com.hadluo.store.service.req.ItemSelect;
import com.uc.framework.logger.Logs;
import com.uc.framework.obj.Result;

@RestController
@RequestMapping("item")
@CrossOrigin
public class ItemController {
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private ItemTypeMapper itemTypeMapper;
	@Autowired
	MainItemsMapper mainItemsMapper;
	@Autowired
	MainConfigMapper mainConfigMapper;
	@Autowired
	MainBannerMapper mainBannerMapper;
	
	
	@ResponseBody
	@GetMapping("/selectByTypes")
	public Result<List<Item>> selectByTypes(@RequestParam(name = "typeId",defaultValue = "1") Integer typeId) {
		ItemSelect itemSelect = new ItemSelect();
		itemSelect.setType(typeId);
		itemSelect.setPageSize(Integer.MAX_VALUE);
		return Result.ok(itemMapper.selectByPage(itemSelect));
	}

	
	@ResponseBody
	@GetMapping("/selectOne")
	public Result<Item> selectOne(@RequestParam(name = "id",defaultValue = "1") Integer id) {
		Result<Item> r =  Result.ok(itemMapper.selectByPrimaryKey(id));
		Logs.e(getClass(), "JokeController selectOne req >> id=" + id + " , ret = " + JSON.toJSONString(r));
		return r ;
	}

	@ResponseBody
	@GetMapping("/types")
	public Result<List<ItemType>> types() {
		List<ItemType> types = itemTypeMapper.selectSortByPage() ;
		Logs.e(getClass(), "types>>" + JSON.toJSONString(types) );
		return Result.ok(types);
	}
	
	@ResponseBody
	@GetMapping("/type")
	public Result<ItemType> type(@RequestParam(name = "id",defaultValue = "1") Integer id) {
		return Result.ok(itemTypeMapper.selectByPrimaryKey(id));
	}
	
	@ResponseBody
	@GetMapping("/mainitems")
	public Result<List<Item>> mainItems() {
		List<MainItems> items = mainItemsMapper.selectSortByPage();
		List<Item> results = Lists.newArrayList();
		for(MainItems item : items) {
			results.add(itemMapper.selectByPrimaryKey(item.getItemId()));
		}
		Logs.e(getClass(), "mainItems>>" + JSON.toJSONString(results) );
		return Result.ok(results);
	}
	
	@ResponseBody
	@GetMapping("/maincfg")
	public Result<MainConfig> mainConfig() {
		MainConfig record = new MainConfig();
		record.setIsDelete(0);
		MainConfig cfg = mainConfigMapper.selectOne(record) ;
		Logs.e(getClass(), "mainConfig>>" + JSON.toJSONString(cfg) );
		return Result.ok(cfg);
	}
	
	@ResponseBody
	@GetMapping("/banners")
	public Result<List<MainBanner>> banners() {
		List<MainBanner> datas = mainBannerMapper.selectSortByPage();
		Logs.e(getClass(), "banners>>" + JSON.toJSONString(datas) );
		return Result.ok(datas);
	}

}
