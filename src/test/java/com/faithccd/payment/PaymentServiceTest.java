package com.faithccd.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.faithccd.payment.exception.BusinessException;
import com.faithccd.payment.service.PaymentService;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentServiceTest {
	
	@Autowired
	private PaymentService service;	
	
//	@Ignore
	@Test 
	public void payment_test1() throws Exception { 
		
		//give 결제값 세팅
		Map<String, Object> test = new HashMap<String, Object>();
		test.put("cardNo", "1111222233334444");
		test.put("cardExpireDate", "0425");
		test.put("cardCvc", 111);
		test.put("installmentMonths", 0);
		test.put("vat", 1000);
		test.put("amount", 11000);
		test.put("status", "PAYMENT");
		
		//when 결제
		test = service.insertPayment(test);
		//then 성공
		assertThat(test.get("unique_id")).isNotNull();
		
		//when 카드사 전송
		test = service.sendPayment(test);
		//then 성공
		assertThat(test.get("sendString")).isNotNull();

	}
	
//	@Ignore
	@Test 
	public void payment_test2_cancel_case1() throws Exception {
		
		//given 결제 세팅
		Map<String, Object> pay = new HashMap<String, Object>();
		pay.put("cardNo", "1111222233334444");
		pay.put("cardExpireDate", "0425");
		pay.put("cardCvc", 111);
		pay.put("installmentMonths", 0);
		pay.put("vat", 1000);
		pay.put("amount", 11000);
		pay.put("status", "PAYMENT");
		
		pay = service.insertPayment(pay);
	
		Long unique_id = (Long) pay.get("unique_id");
		
		//given 취소값 세팅 1100, 100
		Map<String, Object> cancel = new HashMap<String, Object>();
		cancel.put("uniqueIdPayment", unique_id);
		cancel.put("vat", 100);
		cancel.put("amount", 1100);
		cancel.put("status", "CANCEL");
		//when 취소
		cancel = service.insertPayment(cancel);
		//then 성공
		assertThat(cancel.get("unique_id")).isNotNull();		
		
		//given 취소값 세팅 3300
		cancel.clear();		
		cancel.put("uniqueIdPayment", unique_id);		
		cancel.put("amount", 3300);
		cancel.put("status", "CANCEL");
		//when 취소
		cancel = service.insertPayment(cancel);//	@Ignore

		//then 성공
		assertThat(cancel.get("unique_id")).isNotNull();	
		
		//then 실패
		assertThrows(BusinessException.class, () -> {			
			//given 취소값 세팅 7000
			Map<String, Object> th = new HashMap<String, Object>();
			th.put("uniqueIdPayment", unique_id);
			th.put("amount", 7000);
			th.put("status", "CANCEL");
			//when 취소
			service.insertPayment(th);
		});
		
		//then 실패
		assertThrows(BusinessException.class, () -> {			
			//given 취소값 세팅 6600, 700
			Map<String, Object> th = new HashMap<String, Object>();
			th.put("uniqueIdPayment", unique_id);
			th.put("vat", 700);
			th.put("amount", 6600);
			th.put("status", "CANCEL");	
			//when 취소
			service.insertPayment(th);
		});	
		
		//given 취소값 세팅 6600, 600
		cancel.clear();		
		cancel.put("uniqueIdPayment", unique_id);	
		cancel.put("vat", 600);
		cancel.put("amount", 6600);
		cancel.put("status", "CANCEL");
		//when 취소
		cancel = service.insertPayment(cancel);
		//then 성공
		assertThat(cancel.get("unique_id")).isNotNull();	
		
		//then 실패
		assertThrows(BusinessException.class, () -> {			
			//given 취소값 세팅 100
			Map<String, Object> th = new HashMap<String, Object>();
			th.put("uniqueIdPayment", unique_id);
			th.put("amount", 100);
			th.put("status", "CANCEL");	
			//when 취소
			service.insertPayment(th);
		});			

	}
	
//	@Ignore	
	@Test 
	public void payment_test2_cancel_case2() throws Exception {
		
		//given 결제 세팅
		Map<String, Object> pay = new HashMap<String, Object>();
		pay.put("cardNo", "1111222233334444");
		pay.put("cardExpireDate", "0425");
		pay.put("cardCvc", 111);
		pay.put("installmentMonths", 0);
		pay.put("vat", 909);
		pay.put("amount", 20000);
		pay.put("status", "PAYMENT");		
		pay = service.insertPayment(pay);	
		Long unique_id = (Long) pay.get("unique_id");
		
		//given 취소값 세팅 10000, 0
		Map<String, Object> cancel = new HashMap<String, Object>();
		cancel.put("uniqueIdPayment", unique_id);
		cancel.put("vat", 0);
		cancel.put("amount", 10000);
		cancel.put("status", "CANCEL");
		//when 취소
		cancel = service.insertPayment(cancel);
		//then 성공
		assertThat(cancel.get("unique_id")).isNotNull();
		
		//then 실패
		assertThrows(BusinessException.class, () -> {			
			//given 취소값 세팅 10000, 0
			Map<String, Object> th = new HashMap<String, Object>();
			th.put("uniqueIdPayment", unique_id);
			th.put("vat", 910);
			th.put("amount", 10000);
			th.put("status", "CANCEL");
			//when 취소
			service.insertPayment(th);
		});
		
		//given 취소값 세팅 10000, 909
		cancel.clear();		
		cancel.put("uniqueIdPayment", unique_id);	
		cancel.put("vat", 909);
		cancel.put("amount", 10000);
		cancel.put("status", "CANCEL");
		//when 취소
		cancel = service.insertPayment(cancel);
		//then 성공
		assertThat(cancel.get("unique_id")).isNotNull();

	}
	
//	@Ignore
	@Test 
	public void payment_test2_cancel_case3() throws Exception {
		
		//given 결제 세팅
		Map<String, Object> pay = new HashMap<String, Object>();
		pay.put("cardNo", "1111222233334444");
		pay.put("cardExpireDate", "0425");
		pay.put("cardCvc", 111);
		pay.put("installmentMonths", 0);
		pay.put("amount", 20000);
		pay.put("status", "PAYMENT");		
		pay = service.insertPayment(pay);	
		Long unique_id = (Long) pay.get("unique_id");
		
		//given 취소값 세팅 10000, 1000
		Map<String, Object> cancel = new HashMap<String, Object>();
		cancel.put("uniqueIdPayment", unique_id);
		cancel.put("vat", 1000);
		cancel.put("amount", 10000);
		cancel.put("status", "CANCEL");
		//when 취소
		cancel = service.insertPayment(cancel);
		//then 성공
		assertThat(cancel.get("unique_id")).isNotNull();
		
		//then 실패
		assertThrows(BusinessException.class, () -> {			
			//given 취소값 세팅 10000, 909
			Map<String, Object> th = new HashMap<String, Object>();
			th.put("uniqueIdPayment", unique_id);
			th.put("vat", 909);
			th.put("amount", 10000);
			th.put("status", "CANCEL");
			//when 취소
			service.insertPayment(th);
		});
		
		//given 취소값 세팅 10000, null
		cancel.clear();
		cancel.put("uniqueIdPayment", unique_id);	
		cancel.put("amount", 10000);
		cancel.put("status", "CANCEL");
		//when 취소
		cancel = service.insertPayment(cancel);
		//then 성공
		assertThat(cancel.get("unique_id")).isNotNull();

	}
	
//	@Ignore	
	@Test 
	public void payment_test3() throws Exception {
		//give 결제값 세팅
		Map<String, Object> test = new HashMap<String, Object>();
		test.put("cardNo", "1111222233334444");
		test.put("cardExpireDate", "0425");
		test.put("cardCvc", 111);
		test.put("installmentMonths", 0);
		test.put("vat", 1000);
		test.put("amount", 11000);
		test.put("status", "PAYMENT");
		
		//when 결제
		test = service.insertPayment(test);
		
		//give key 값 세팅
		Map<String, Object> get = new HashMap<String, Object>();
		get.put("uniqueId", test.get("unique_id"));
		
		//when 조회
		get = service.getPayment(get);
		
		//then 성공
		assertThat(get.get("UNIQUE_ID")).isNotNull();
		
	}

}
