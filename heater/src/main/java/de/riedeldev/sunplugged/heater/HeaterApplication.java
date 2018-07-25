package de.riedeldev.sunplugged.heater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.ApplicationEventBus;
import org.vaadin.spring.events.internal.ScopedEventBus;
import org.vaadin.spring.events.support.ApplicationContextEventBroker;

@SpringBootApplication
@EnableScheduling
public class HeaterApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(HeaterApplication.class, args);
	}

	@Bean
	public ApplicationEventBus applicationEventBus() {
		return new ScopedEventBus.DefaultApplicationEventBus();
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}

	@Autowired
	EventBus.ApplicationEventBus eventBus;

	@Bean
	ApplicationContextEventBroker applicationContextEventBroker() {
		return new ApplicationContextEventBroker(eventBus);
	}
}
