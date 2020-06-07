package com.faithccd.payment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.faithccd.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PaymentController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PaymentService service;
	
	@RequestMapping(value = { "/payment" }, method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> payment(@RequestBody ConcurrentHashMap<String, Object> map) throws Exception {	
	
		logger.debug("{}", (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(map));		
		
		map.put("status", "PAYMENT");		
		map = (ConcurrentHashMap<String, Object>) service.insertPayment(map);	
		
		service.sendPayment(map);
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("unique_id", map.get("unique_id"));
		
		return result;
	}
	
	@RequestMapping(value = { "/cancel" }, method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> cancel(@RequestBody ConcurrentHashMap<String, Object> map) throws Exception {	
	
		logger.debug("{}", (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(map));		
		
		map.put("status", "CANCEL");
		map = (ConcurrentHashMap<String, Object>) service.insertPayment(map);
		
		service.sendPayment(map);
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("unique_id", map.get("unique_id"));
		
		return result;
	}
	
	@RequestMapping(value = { "/get" }, method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> get(@RequestBody ConcurrentHashMap<String, Object> map) throws Exception {	
	
		logger.debug("{}", (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(map));		
		
		map = (ConcurrentHashMap<String, Object>) service.getPayment(map);		
		
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String,Object> cardInfo = new HashMap<String,Object>();
		cardInfo.put("cardNo", map.get("cardNo"));
		cardInfo.put("cardExpireDate", map.get("cardExpireDate"));
		cardInfo.put("cardCvc", map.get("cardCvc"));
		Map<String,Object> amountInfo = new HashMap<String,Object>();
		amountInfo.put("amount", map.get("AMOUNT"));
		amountInfo.put("vat", map.get("VAT"));		
		
		result.put("uniqueId", map.get("uniqueId"));
		result.put("cardInfo", cardInfo);
		result.put("status", map.get("STATUS"));
		result.put("amountInfo", amountInfo);
		return result;
	}	
}