package de.riedeldev.sunplugged.heater.tests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.riedeldev.sunplugged.beckhoff.bk9000.BK9000;
import de.riedeldev.sunplugged.beckhoff.kl3xxx.KL3312;
import de.riedeldev.sunplugged.heater.config.Parameters;

@RestController
public class TestController {

	@Autowired
	Parameters para;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public Double getTestData() {
		return para.getPreHeaterI();
	}

	@RequestMapping(value = "/set/{value}", method = RequestMethod.GET)
	public Double setTestData(@PathVariable("value") Integer value) {
		para.setPreHeaterI(value);
		para.saveParameters();
		return para.getPreHeaterI();
	}

	@Autowired
	BK9000 bk;

	@GetMapping("/codeword")
	public void testSetCodeWord() {
		KL3312 clamp = (KL3312) bk.getClamp("8");

		clamp.getConfigurator(0).deactivateReadOnly();
	}

	@GetMapping("/deletecodeword")
	public void testDeletedCodeWord() {
		KL3312 clamp = (KL3312) bk.getClamp("8");

		clamp.getConfigurator(0).activateReadOnly();
		clamp.getConfigurator(0).switchOffRegisterCommunication();
	}

}
