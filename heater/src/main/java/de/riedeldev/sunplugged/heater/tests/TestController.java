package de.riedeldev.sunplugged.heater.tests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
