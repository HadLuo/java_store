package com.hadluo.store.service.mapper;

import java.util.List;
import com.hadluo.store.api.pojo.MainBanner;
import tk.mybatis.mapper.common.BaseMapper;

public interface MainBannerMapper extends BaseMapper<MainBanner> {

	public List<MainBanner> selectSortByPage();


}
