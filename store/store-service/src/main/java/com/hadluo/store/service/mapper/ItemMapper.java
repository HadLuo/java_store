package com.hadluo.store.service.mapper;

import java.util.List;
import com.hadluo.store.api.pojo.Item;
import com.hadluo.store.service.req.ItemSelect;
import tk.mybatis.mapper.common.BaseMapper;

public interface ItemMapper extends BaseMapper<Item> {

	public List<Item> selectByPage(ItemSelect select);

	public int selectCountByPage(ItemSelect select);

}
