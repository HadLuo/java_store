package com.hadluo.store.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * main函数 主入口
 * 
 * @author HadLuo
 * @date 2020-9-8 15:17:17
 */
@EnableFeignClients(basePackages = "com.hadluo") // 开启 FeignClients
@SpringBootApplication(scanBasePackages = "com.hadluo")
@EnableDiscoveryClient // 开启注册中心服务发现
@EnableAspectJAutoProxy
public class MainApplication {

	public static void main(String[] args) {
		// 本地开发需加上环境： NACOS_CONFIG_ADDR = http://49.234.123.192:8848/
		SpringApplication.run(MainApplication.class, args);
	}

}