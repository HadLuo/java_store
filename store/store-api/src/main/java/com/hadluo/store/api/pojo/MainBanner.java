package com.hadluo.store.api.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Table(name = "t_main_items")
public class MainBanner {

	@Id
	private Integer id;
	private String title;
	private Integer itemId;
	private Integer isDelete ;
	private Integer sort ;
	private String image ;

}
