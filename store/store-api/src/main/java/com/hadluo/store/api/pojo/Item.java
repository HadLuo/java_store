package com.hadluo.store.api.pojo;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Table(name = "t_item")
public class Item {

	@Id
	private Integer id;
	/** 类型 */
	private Integer type;
	/** 标题 */
	private String title;
	/** 列表的预览 */
	private String preview;
	private Integer pv ;
	private Integer up ;
	/** 内容 */
	private String content;
	//封面图
	private String cover;
	private String bigImage;
	//來源
	private String src ;
	private Date createTime ;

}
