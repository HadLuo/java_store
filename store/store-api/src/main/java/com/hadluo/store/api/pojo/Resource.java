package com.hadluo.store.api.pojo;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "t_resource")
public class Resource {

	@Id
	private Integer id;
	/** 资源名称 */
	private String name;
	/** 资源图片 */
	private String image;
	/** 资源类型,0-代码,1-文章 */
	private Integer type;
	/** 资源标题 */
	private String title;
	/** 资源具体内容 */
	private String content;
	/** 创建时间 */
	private Date createTime;

}
