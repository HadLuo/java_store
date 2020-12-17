package com.hadluo.store.service.req;

import lombok.Data;

@Data
public class ItemSelect {
	private int type = 0;
	private int kindId = 0;
	private String title ;

	private int pageIndex = 1;

	private int pageSize = 10;

}
