package com.hadluo.store.api.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Table(name = "t_item_type")
public class ItemType {

	@Id
	private Integer id;
	/** 标题 */
	private String name;
	private String cover ;
	private String bigImage ;
	private Integer sort ;
	private String descript ;

}
