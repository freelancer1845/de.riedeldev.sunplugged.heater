package de.riedeldev.sunplugged.heater.pid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.riedeldev.sunplugged.heater.io.IOServiceException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PIDLogger {

	private File file;

	private final Heater heater;

	public PIDLogger(String filename, AbstractHeater heater) {
		Path path = Paths.get(filename + ".txt");
		this.heater = heater;
		file = path.toFile();
		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}

		int i = 1;
		while (file.exists() == true) {
			file = new File(filename + i + ".txt");
			i++;
		}

	}

	public String getFilePath() {
		return file.getAbsolutePath();
	}

	public void start() {
		Thread thread = new Thread(() -> {

			try {
				if (file.createNewFile() == false) {
					throw new IllegalStateException("Failed to create file.");
				}
				int idx = 0;
				while (true) {

					try (FileWriter writer = new FileWriter(file, true)) {
						writer.write(idx + "\t");
						writer.write(heater.getCurrentTemperature() + "\t");
						writer.write(heater.getTargetTemperature() + "\t");
						writer.write(heater.getPower() + "\n");
						writer.flush();
						idx++;
						Thread.sleep(500);
					} catch (IOServiceException | InterruptedException e) {
						log.error(
								"IO Exception while getting currentTemperature. Logger failed.",
								e);
						break;
					}

				}

			} catch (IOException e) {
				log.error("Failed to create file for pid logger.", e);
			}

		}, file.getName());
		thread.setDaemon(true);
		thread.start();
	}

}
