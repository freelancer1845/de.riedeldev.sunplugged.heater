package de.riedeldev.sunplugged.heater.io;

import org.springframework.stereotype.Service;

@Service
public class IOServiceStub implements IOService {

	@Override
	public void setDO(int address, boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getDO(int address) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDI(int address) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAO(int address, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAO(int address) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAI(int address) {
		// TODO Auto-generated method stub
		return 0;
	}

}
