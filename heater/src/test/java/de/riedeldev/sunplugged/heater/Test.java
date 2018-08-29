package de.riedeldev.sunplugged.heater;

public class Test {

	public static void main(String[] args) {

		int currentValue = (0b11000110 << 8) + 0b11000010;

		int first8Bit = (byte) currentValue;
		byte second8Bit = (byte) (currentValue >> 8);

		System.out.println(Integer.toBinaryString(currentValue));
		System.out.println(Integer.toBinaryString(first8Bit));
		System.out.println(Integer.toBinaryString(second8Bit));

		int first8BitToWrite = 0b00000110;
		System.out.println(Integer.toBinaryString(first8BitToWrite));
		first8BitToWrite = first8BitToWrite | ((first8Bit >> 7 & 1) << 7);
		System.out.println(Integer.toBinaryString(first8BitToWrite));
	}

}
