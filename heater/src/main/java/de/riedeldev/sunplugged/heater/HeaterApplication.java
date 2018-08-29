package de.riedeldev.sunplugged.heater;

import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import de.riedeldev.sunplugged.beckhoff.klspi.Configurator;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class HeaterApplication extends SpringBootServletInitializer {

	@Value("${bk9000.configure:false}")
	private boolean configureBK9000;

	@Value("${bk9000.ip:localhost}")
	private String bk9000Ip;;

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
		bk.connect(bk9000Ip, 502);
		if (configureBK9000) {

			for (int i = 0; i < kl4004.outputs(); i++) {
				Configurator configurator = kl4004.getConfigurator(i);

				configurator.deactivateReadOnly();

				try {
					int currentValue = configurator
							.readValuteFromConfigRegister(32).get();
					int first8Bit = (byte) currentValue;
					int second8Bit = (byte) (currentValue >> 8);

					int first8BitToWrite = (byte) 0b00000110;
					int second8BitToWrite = second8Bit;
					first8BitToWrite = first8BitToWrite
							| (((first8Bit >> 4) & 1) << 4);
					first8BitToWrite = first8BitToWrite
							| (((first8Bit >> 6) & 1) << 6);
					first8BitToWrite = first8BitToWrite
							| (((first8Bit >> 7) & 1) << 7);

					int userRegisterValue = first8BitToWrite
							+ (second8BitToWrite << 8);

					configurator.writeValueToConfigRegister(32,
							userRegisterValue);
				} catch (InterruptedException | ExecutionException e) {
					log.error(
							"Faield to configure one of the clamps! (KL4004)");
					throw new IllegalStateException(
							"Failed to configure one of the clamps!", e);
				}

				configurator.writeValueToConfigRegister(32, 0b0000110);
				configurator.activateReadOnly();
				configurator.switchOffRegisterCommunication();
			}

			Stream.of(kl33121, kl33122).forEach(k -> {
				for (int i = 0; i < k.inputs(); i++) {
					Configurator configurator = k.getConfigurator(i);
					configurator.deactivateReadOnly();

					int currentValue;
					try {
						currentValue = configurator
								.readValuteFromConfigRegister(32).get();
						int first8Bit = (byte) currentValue;
						int second8Bit = (byte) (currentValue >> 8);

						int first8BitToWrite = 0b00000110;
						first8BitToWrite = first8BitToWrite
								| (((first8Bit >> 7) & 1) << 7);
						int second8BitToWrite = 0b01000000;
						second8BitToWrite = second8BitToWrite
								| (((second8Bit >> 1) & 1) << 1);
						second8BitToWrite = second8BitToWrite
								| (((second8Bit >> 3) & 1) << 3);

						int userRegisterValue = first8BitToWrite
								+ (second8BitToWrite << 8);

						configurator.writeValueToConfigRegister(32,
								userRegisterValue);
						configurator.activateReadOnly();
						configurator.switchOffRegisterCommunication();
					} catch (InterruptedException | ExecutionException e) {
						log.error(
								"Faield to configure one of the clamps! (KL3312)");
						throw new IllegalStateException(
								"Failed to configure one of the clamps!", e);
					}

				}
			});
		}

		return bk;
	}

}
