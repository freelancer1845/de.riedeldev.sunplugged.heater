package de.riedeldev.sunplugged.heater;

import java.io.IOException;
import java.nio.file.Paths;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.mainheater.AbstractMainHeater;
import de.riedeldev.sunplugged.heater.pid.PIDLogger;

public class AbstractMainHeaterPidCustom {

	public static void main(String args[])
			throws IOServiceException, InterruptedException, IOException {
		IOService ioService = new IOServiceTest();

		AbstractMainHeater heater = new AbstractMainHeater("Test", 1, 1,
				ioService, new Parameters(), "NonTopic") {

			@Override
			protected void setPower(double power) {
				super.setPower(power);
				((IOServiceTest) ioService).setNewPower(getPower());
			}
		};

		PIDLogger logger = new PIDLogger(
				Paths.get("pidtests", "piddata").toString(), heater);
		logger.start();

		heater.startControlLoop();

		heater.setTargetTemperature(250);
		heater.activateControlling();
		heater.on();
		heater.setPIDValues(0.004, 0.0009, 0.0001);

		Thread.sleep(60000);
		heater.setTargetTemperature(120);
		Thread.sleep(60000);
		heater.setTargetTemperature(350);
		Thread.sleep(120000);
		heater.setTargetTemperature(0);
		Thread.sleep(120000);

	}

	private static class IOServiceTest implements IOService {
		private static int bit16 = (int) (Math.pow(2, 16) - 1);

		private static double typeKM = (1370.0 - 100.0) / bit16;
		private static double typeKC = -100;

		private static double ROOM_FACTOR = 8e-2;
		private static double HEATER_FACTOR = 4e-1;

		public IOServiceTest() {
			Thread thread = new Thread(() -> {
				while (true) {

					long delta = System.currentTimeMillis()
							- lastTimePowerAsked;

					// Room temperature decay
					currentTemperature -= delta / 1000.0 * ROOM_FACTOR
							* (currentTemperature - 20.0);

					currentTemperature += delta / 1000.0
							* (400 - currentTemperature) * power
							* HEATER_FACTOR;
					lastTimePowerAsked = System.currentTimeMillis();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						break;
					}
				}

			}, "Heater Calculator");
			thread.setDaemon(true);
			thread.start();
		}

		public static double typeKConversion(int value) {
			return typeKM * value - typeKC - 273.13;
		}

		private long lastTimePowerAsked = System.currentTimeMillis();

		private double power = 0.0;

		public void setNewPower(double power) {
			this.power = power;
		}

		public static int typeKConversionReverse(double temperature) {
			return (int) ((temperature + typeKC + 273.13) / typeKM);
		}

		private static final double linearMaxGAIN = 5; // 5C° pro sekunde
		private double currentTemperature = 20.0;

		@Override
		public void setDO(int address, boolean value)
				throws IOServiceException {
		}

		@Override
		public int getAI(int address) throws IOServiceException {

			return typeKConversionReverse(currentTemperature);
		}

		@Override
		public boolean getDO(int address) throws IOServiceException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean getDI(int address) throws IOServiceException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setAO(int address, int value) throws IOServiceException {
			// TODO Auto-generated method stub

		}

		@Override
		public int getAO(int address) throws IOServiceException {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}
