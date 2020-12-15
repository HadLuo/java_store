package com.hadluo.store.service.mapper;

import java.util.List;
import com.hadluo.store.api.pojo.ItemType;
import com.hadluo.store.api.pojo.MainItems;

import tk.mybatis.mapper.common.BaseMapper;

public interface MainItemsMapper extends BaseMapper<MainItems> {

	public List<MainItems> selectSortByPage();


}
