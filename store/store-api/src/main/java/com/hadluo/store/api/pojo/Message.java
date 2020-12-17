package com.hadluo.store.api.pojo;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Table(name = "t_message")
public class Message {
	@Id
	private Integer id;
	/** 姓名 */
	private String name;
	/** 联系电话 */
	private String phone;
	private String qq;
	private String wechat;
	/** 内容 */
	private String content;
	private Date createTime;
	private Integer isDelete;
}
