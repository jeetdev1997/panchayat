package com.panchayat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PanchayatApplication {

	public static void main(String[] args) {
		SpringApplication.run(PanchayatApplication.class, args);
		System.out.println("======= Application Started ========");
	}

}
