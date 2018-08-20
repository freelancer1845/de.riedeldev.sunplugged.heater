package de.riedeldev.sunplugged.beckhoff.klspi;

import com.digitalpetri.modbus.master.ModbusTcpMaster;

public abstract class AbstractClamp implements Klemme {

	protected ModbusTcpMaster master;

	protected int writeAddressOffset;
	protected int readAddressOffset;

	private final int addressSpaceSizeOutput;
	private final int addressSpaceSizeInput;
	private final String id;

	public AbstractClamp(int addressSpaceSizeOutput, int addressSpaceSizeInput,
			String id) {
		this.addressSpaceSizeOutput = addressSpaceSizeOutput;
		this.addressSpaceSizeInput = addressSpaceSizeInput;
		this.id = id;
	}

	@Override
	public void setTcpMaster(ModbusTcpMaster master) {
		this.master = master;
	}

	@Override
	public void setInputOffset(int offset) {
		this.readAddressOffset = offset;
	}

	@Override
	public void setOutputOffset(int offset) {
		this.writeAddressOffset = offset;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int addressSpaceInput() {
		return addressSpaceSizeInput;
	}

	@Override
	public int addressSpaceOutput() {
		return addressSpaceSizeOutput;
	}

}
