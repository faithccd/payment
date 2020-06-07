package com.faithccd.payment;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;

import com.faithccd.BootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
@AutoConfigureMockMvc
@Transactional
public class PaymentControllerTest {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MockMvc mvc;
    
	@Test
	public void payment_test() throws Exception {
		
		Map<String, Object> test = new HashMap<String, Object>();
		test.put("cardNo", "1111222233334444");
		test.put("cardExpireDate", "0425");
		test.put("cardCvc", 111);
		test.put("installmentMonths", 0);
		test.put("vat", 1000);
		test.put("amount", 11000);
		
		String json = (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(test);
		
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/payment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(json)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		logger.debug(content);
		assertEquals(200, status);		
	}

}
