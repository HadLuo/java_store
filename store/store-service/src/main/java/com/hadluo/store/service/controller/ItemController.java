package com.hadluo.store.service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.hadluo.store.api.pojo.Message;
import com.hadluo.store.service.mapper.ItemMapper;
import com.hadluo.store.service.mapper.ItemTypeMapper;
import com.hadluo.store.service.mapper.MainBannerMapper;
import com.hadluo.store.service.mapper.MainConfigMapper;
import com.hadluo.store.service.mapper.MainItemsMapper;
import com.hadluo.store.service.mapper.MessageMapper;
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
	@Autowired
	MessageMapper messageMapper;

	/***
	 * 搜索
	 * 
	 * @param typeId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/selectByTitles")
	public Result<List<Item>> selectByTitles(@RequestParam(name = "t") String keyWords) {
		ItemSelect itemSelect = new ItemSelect();
		itemSelect.setTitle(keyWords);
		itemSelect.setPageSize(Integer.MAX_VALUE);
		return Result.ok(itemMapper.selectByPage(itemSelect));
	}
	
	
	/***
	 * 根据宠物类别查询宠物文章
	 * 
	 * @param typeId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/selectByKind")
	public Result<List<Item>> selectByKind(@RequestParam(name = "kid") Integer kindId,@RequestParam(name = "tid") Integer typeId) {
		ItemSelect itemSelect = new ItemSelect();
		itemSelect.setKindId(kindId);
		itemSelect.setType(typeId);
		itemSelect.setPageSize(Integer.MAX_VALUE);
		return Result.ok(itemMapper.selectByPage(itemSelect));
	}

	/***
	 * 根据宠物类别查询宠物文章
	 * 
	 * @param typeId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/selectByTypes")
	public Result<List<Item>> selectByTypes(@RequestParam(name = "typeId") Integer typeId) {
		ItemSelect itemSelect = new ItemSelect();
		itemSelect.setType(typeId);
		itemSelect.setPageSize(Integer.MAX_VALUE);
		return Result.ok(itemMapper.selectByPage(itemSelect));
	}

	/***
	 * 根据文章id 查询 宠物详情
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/selectOne")
	public Result<Item> selectOne(@RequestParam(name = "id", defaultValue = "1") Integer id) {
		Result<Item> r = Result.ok(itemMapper.selectByPrimaryKey(id));
		Logs.e(getClass(), "JokeController selectOne req >> id=" + id + " , ret = " + JSON.toJSONString(r));
		return r;
	}

	/***
	 * 查询宠物类别
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/types")
	public Result<List<ItemType>> types() {
		List<ItemType> types = itemTypeMapper.selectSortByPage();
		Logs.e(getClass(), "types>>" + JSON.toJSONString(types));
		return Result.ok(types);
	}

	/***
	 * 根据类别id查询类别详情
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@GetMapping("/type")
	public Result<ItemType> type(@RequestParam(name = "id", defaultValue = "1") Integer id) {
		return Result.ok(itemTypeMapper.selectByPrimaryKey(id));
	}

	/***
	 * 首页的 宠物推荐文章
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/mainitems")
	public Result<List<Item>> mainItems() {
		List<MainItems> items = mainItemsMapper.selectSortByPage();
		List<Item> results = Lists.newArrayList();
		for (MainItems item : items) {
			results.add(itemMapper.selectByPrimaryKey(item.getItemId()));
		}
		Logs.e(getClass(), "mainItems>>" + JSON.toJSONString(results));
		return Result.ok(results);
	}

	/***
	 * 一些主配置 ,（联系人，二维码等等）
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/maincfg")
	public Result<MainConfig> mainConfig() {
		MainConfig record = new MainConfig();
		record.setIsDelete(0);
		MainConfig cfg = mainConfigMapper.selectOne(record);
		Logs.e(getClass(), "mainConfig>>" + JSON.toJSONString(cfg));
		return Result.ok(cfg);
	}

	/***
	 * 首页 banner图
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/banners")
	public Result<List<MainBanner>> banners() {
		List<MainBanner> datas = mainBannerMapper.selectSortByPage();
		Logs.e(getClass(), "banners>>" + JSON.toJSONString(datas));
		return Result.ok(datas);
	}

	/***
	 * 用户留言
	 * 
	 * @return
	 */
	@ResponseBody
	@PostMapping("/message")
	public Result<?> message(@RequestBody Message message) {
		message.setId(null);
		messageMapper.insertSelective(message);
		Logs.e(getClass(), "message>>" + JSON.toJSONString(message));
		return Result.ok();
	}

}
