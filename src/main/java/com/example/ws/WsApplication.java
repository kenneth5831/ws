package com.example.ws;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.ws.mapper")
public class WsApplication {
	public static void main(String[] args) {
		SpringApplication.run(WsApplication.class, args);
	}
}