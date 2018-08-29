package de.riedeldev.sunplugged.heater.io;

public class Addresses {

	// Digital Outputs
	public static final int MAIN_HEATER_ONE_DO = 0;
	public static final int MAIN_HEATER_TOW_DO = 1;
	public static final int MAIN_HEATER_THREE_DO = 2;
	public static final int PLC_START = 3;
	public static final int PLC_RUN = 4;
	public static final int HORN = 5;
	public static final int GREEN_LIGHT = 6;
	public static final int ORANGE_LIGHT = 7;
	public static final int RED_LIGHT = 8;

	// Analog Outputs
	public static final int PRE_HEATER_TWO_AO = 0;
	public static final int PRE_HEATER_ONE_AO = 1;
	public static final int HEATER_FAN_ONE = 2;
	public static final int HEATER_FAN_TWO = 3;

	// Analog Inputs
	// 0 - 10V

	// Thermo Coupler
	public static final int MAIN_HEATER_ONE_AI = 4;
	public static final int MAIN_HEATER_TWO_AI = 5;
	public static final int MAIN_HEATER_THREE_AI = 6;
	public static final int PRE_HEATER_ONE_AI = 7;
	public static final int PRE_HEATER_TWO_AI = 8;// This one is actual missing

	// Digital Inputs
	public static final int PLC_ALL_FINE = 0;
	public static final int OVERTEMPERATURE = 1;
	public static final int PANEL_INTERLOCK_ONE = 2;
	public static final int PANEL_INTERLOCK_TWO = 3;
	public static final int PANEL_INTERLOCK_THREE = 4;
	public static final int COVER_ALARM_TOP = 5;
	public static final int COVER_ALARM_BOTTOM = 6;
	public static final int PANEL_INTERLOCK_ALARM = 7;

	private Addresses() {
	}
}
