package de.riedeldev.sunplugged.heater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class HeaterApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(HeaterApplication.class, args);
	}
}
