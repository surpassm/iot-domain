package com.gihub.surpassm.iot.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author mc
 * Create date 2020/5/21 9:43
 * Version 1.0
 * Description
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class IotMqttServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotMqttServerApplication.class, args);
	}

}
