package de.riedeldev.sunplugged.beckhoff.klspi;

import com.digitalpetri.modbus.master.ModbusTcpMaster;

public interface Klemme {

	public void setTcpMaster(ModbusTcpMaster master);

	public void setInputOffset(int offset);

	public void setOutputOffset(int offset);

	public String getId();

	public int addressSpaceInput();

	public int addressSpaceOutput();

}
