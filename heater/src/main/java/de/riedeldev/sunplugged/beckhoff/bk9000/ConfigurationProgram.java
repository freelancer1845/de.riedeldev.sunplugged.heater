package de.riedeldev.sunplugged.beckhoff.bk9000;

import java.util.concurrent.ExecutionException;

import de.riedeldev.sunplugged.beckhoff.bk9000.BK9000.BK9000Builder;
import de.riedeldev.sunplugged.beckhoff.kl1xxx.KL1104;
import de.riedeldev.sunplugged.beckhoff.kl2xxx.KL2114;
import de.riedeldev.sunplugged.beckhoff.kl3xxx.KL3064;
import de.riedeldev.sunplugged.beckhoff.kl3xxx.KL3312;
import de.riedeldev.sunplugged.beckhoff.kl4xxx.KL4004;
import de.riedeldev.sunplugged.beckhoff.klspi.Configurator;

public class ConfigurationProgram {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		KL4004 kl4004 = new KL4004("7");
		KL3312 kl33121 = new KL3312("8");
		KL3312 kl33122 = new KL3312("9");

		BK9000 bk = new BK9000Builder().with(new KL1104("1"))
				.with(new KL1104("2")).with(new KL1104("3"))
				.with(new KL2114("4")).with(new KL2114("5"))
				.with(new KL3064("6")).with(kl4004).with(kl33121).with(kl33122)
				.build();
		bk.connect("192.168.178.16", 502);
		bk.resetWatchDog();
		bk.setWatchDogTime(0);
		Configurator conf = kl4004.getConfigurator(3);
		
		conf.deactivateReadOnly();
		int currentValue = conf
				.readValuteFromConfigRegister(32).get();
		byte first8Bit = (byte) currentValue;
		byte second8Bit = (byte) (currentValue >> 8);

		byte first8BitToWrite = (byte) 0b00000101;
		byte second8BitToWrite = second8Bit;
		first8BitToWrite = (byte) (first8BitToWrite
				| (((first8Bit >> 4) & 1) << 4));
		first8BitToWrite = (byte) (first8BitToWrite
				| (((first8Bit >> 6) & 1) << 6));
		first8BitToWrite = (byte) (first8BitToWrite
				| (((first8Bit >> 7) & 1) << 7));

		int userRegisterValue = first8BitToWrite
				+ (second8BitToWrite << 8);

		conf.writeValueToConfigRegister(32,
				userRegisterValue);
		conf.writeValueToConfigRegister(33, 0);
		conf.writeValueToConfigRegister(34, 128);
		System.out.println("Hadware offset: " + conf.readValuteFromConfigRegister(17).get());
		System.out.println("Hardware gain: " + conf.readValuteFromConfigRegister(18).get());
		System.out.println("Feature Register: " + conf.readValuteFromConfigRegister(32).get());
		System.out.println("Manfacture offset: " + conf.readValuteFromConfigRegister(19).get());
		System.out.println("Manfacture scaling: " + conf.readValuteFromConfigRegister(20).get());
		System.out.println("User scaling offset: " + conf.readValuteFromConfigRegister(33).get());
		System.out.println("User scaling gain: " + conf.readValuteFromConfigRegister(34).get());
		
		
		conf.activateReadOnly();
		conf.switchOffRegisterCommunication();
		
		
		bk.setWatchDogTime(2000).get();
//		bk.disconnect().get();
	}
	
}
