package de.riedeldev.sunplugged.heater.io;

public class Addresses {

	public static final int PRE_HEATER_ONE_AO = 1;
	public static final int PRE_HEATER_ONE_AI = 1;
	public static final int PRE_HEATER_TWO_AI = 0;
	public static final int PRE_HEATER_TWO_AO = 0;

	public static final int MAIN_HEATER_ONE_AI = 2;
	public static final int MAIN_HEATER_TWO_AI = 3;
	public static final int MAIN_HEATER_THREE_AI = 4;

	public static final int HEATER_FAN_ONE = 2;
	public static final int HEATER_FAN_TWO = 3;

	public static final int PLC_ALL_FINE = 0;

	public static final int OVERTEMPERATURE = 1;

	public static final int PANEL_INTERLOCK_ONE = 2;

	public static final int PANEL_INTERLOCK_TWO = 3;

	public static final int PANEL_INTERLOCK_THREE = 4;

	public static final int COVER_ALARM_TOP = 5;

	public static final int COVER_ALARM_BOTTOM = 6;

	public static final int PANEL_INTERLOCK_ALARM = 7;

	public static final int HORN = 4;

	public static final int PLC_START = 5;

	public static final int PLC_RUN = 6;

	public static final int GREEN_LIGHT = 7;

	public static final int ORANGE_LIGHT = 8;

	public static final int RED_LIGHT = 9;

	private Addresses() {
	}
}
