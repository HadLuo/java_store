package com.hadluo.store.api.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "t_main_items")
public class MainItems {

	@Id
	private Integer id;
	private Integer itemId;
	private Integer isDelete ;
	private Integer sort ;

}
