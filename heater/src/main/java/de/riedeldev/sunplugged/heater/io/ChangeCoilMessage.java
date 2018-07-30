package de.riedeldev.sunplugged.heater.io;

import lombok.Data;

@Data
public class ChangeCoilMessage {

	private int address;

	private Boolean newValue;

}
