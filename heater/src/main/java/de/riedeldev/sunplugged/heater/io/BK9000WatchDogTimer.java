package de.riedeldev.sunplugged.heater.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.riedeldev.sunplugged.beckhoff.bk9000.BK9000;

@Component
public class BK9000WatchDogTimer {

	@Autowired
	private BK9000 bk9000;

	@Scheduled(fixedRate = 1000)
	public void resetWatchDog() {
		bk9000.resetWatchDog();
	}
}
