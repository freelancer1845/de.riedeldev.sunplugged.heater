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

import de.riedeldev.sunplugged.beckhoff.bk9000.BK9000;
import de.riedeldev.sunplugged.beckhoff.bk9000.BK9000.BK9000Builder;
import de.riedeldev.sunplugged.beckhoff.kl1xxx.KL1104;
import de.riedeldev.sunplugged.beckhoff.kl2xxx.KL2114;
import de.riedeldev.sunplugged.beckhoff.kl3xxx.KL3064;
import de.riedeldev.sunplugged.beckhoff.kl3xxx.KL3312;
import de.riedeldev.sunplugged.beckhoff.kl4xxx.KL4004;

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

	@Bean
	BK9000 BK9000() {
		KL4004 kl4004 = new KL4004("7");
		KL3312 kl33121 = new KL3312("8");
		KL3312 kl33122 = new KL3312("9");

		BK9000 bk = new BK9000Builder().with(new KL1104("1"))
				.with(new KL1104("2")).with(new KL1104("3"))
				.with(new KL2114("4")).with(new KL2114("5"))
				.with(new KL3064("6")).with(kl4004).with(kl33121).with(kl33122)
				.build();
		bk.connect("localhost", 502);

		// for (int i = 0; i < kl4004.outputs(); i++) {
		// Configurator configurator = kl4004.getConfigurator(i);
		//
		// configurator.deactivateReadOnly();
		// // do configuration here
		// configurator.switchOffRegisterCommunication();
		// }
		return bk;
	}
}
