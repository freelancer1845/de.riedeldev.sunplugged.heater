package de.riedeldev.sunplugged.heater.io;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Conversions {

	private static int bit16 = (int) (Math.pow(2, 16) - 1);

	/*
	 * -100 = m * 0 + c 1370 = m * bit16 - 100
	 * 
	 * m = 1370 - 100 / bit16
	 * 
	 */
	private static double typeKM = (1370 - 100) / bit16;
	private static double typeKC = -100;

	public static double typeKConversion(int value) {
		return typeKM * value - typeKC;

	}

	public static int unsingedVoltageToUnsingedInt(double voltage) {
		if (voltage < 0.0) {
			log.warn("Voltage lower than 0.0 V. Will not convert but return 0");
			return 0;
		} else if (voltage > 10.0) {
			log.warn(
					"Voltage hight than 10.0 V. Will not convert but return 2^16");
			return (int) (bit16);
		}
		return (int) ((bit16) * voltage / 10.0);
	}

	public static double unsingedIntToUnsingedVoltage(int value) {
		if (value < 0) {
			log.warn("Integer < 0 in !unsinged! conversion, will return 0.0");
			return 0.0;
		} else if (value > bit16) {
			log.warn(
					"Integer > 2^16 - 1 in !unsinged int! conversion, will return 10.0");
			return 10.0;
		}
		return (value / bit16) * 10.0;
	}

	private Conversions() {
	}
}
