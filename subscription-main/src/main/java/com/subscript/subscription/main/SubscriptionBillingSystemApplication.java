package com.subscript.subscription.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.subscript.subscription")
@org.springframework.boot.autoconfigure.domain.EntityScan("com.subscript.subscription.api.model")
@org.springframework.data.jpa.repository.config.EnableJpaRepositories("com.subscript.subscription.service.repository")
@EnableScheduling
public class SubscriptionBillingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionBillingSystemApplication.class, args);
	}

}