package com.hadluo.store.api.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "t_main_config")
public class MainConfig {

	@Id
	private Integer id;
	private String title;
	private String title2;
	private String content;
	private String end;
	private Integer isDelete ;
	private String image ;
	private String contactQq ;
	private String contactQrbarcode ;

}
