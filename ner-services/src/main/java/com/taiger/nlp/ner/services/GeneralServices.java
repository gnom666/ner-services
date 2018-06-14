package com.taiger.nlp.ner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/")
public class GeneralServices {
	
	@Autowired
    private ApplicationContext appContext;

    public void initiateShutdown(int returnCode){
    	SpringApplication.exit(appContext, () -> returnCode);
    }

	@RequestMapping(value="/die", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public void die() {
		initiateShutdown(0);
    }
	
	@RequestMapping(value="/echo", method=RequestMethod.GET, produces="application/text;charset=UTF-8")
    public String echo(@RequestParam(value="str", defaultValue="") String str) {
		Assert.notNull(str, "sentence shouldn't be null");
		log.info("ECHO : " + str);
		return str;
    }
	
}
