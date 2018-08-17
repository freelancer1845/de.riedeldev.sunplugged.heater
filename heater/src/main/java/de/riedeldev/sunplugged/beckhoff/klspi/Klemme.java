package de.riedeldev.sunplugged.beckhoff.klspi;

import com.digitalpetri.modbus.master.ModbusTcpMaster;

public interface Klemme {

	public void attach(int readAddressOffset, int writeAddressOffset,
			ModbusTcpMaster master);

}
