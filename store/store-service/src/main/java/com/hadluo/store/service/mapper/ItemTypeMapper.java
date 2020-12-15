package com.hadluo.store.service.mapper;

import java.util.List;
import com.hadluo.store.api.pojo.ItemType;
import tk.mybatis.mapper.common.BaseMapper;

public interface ItemTypeMapper extends BaseMapper<ItemType> {

	public List<ItemType> selectSortByPage();


}
