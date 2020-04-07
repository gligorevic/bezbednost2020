package com.example.server;

import com.example.server.enumeration.KeyUsages;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

}
