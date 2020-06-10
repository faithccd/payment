package com.faithccd.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faithccd.payment.dao.PaymentDao;
import com.faithccd.payment.exception.BusinessException;
import com.faithccd.payment.exception.ErrorCode;
import com.faithccd.security.AES256Util;
import com.faithccd.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymentService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PaymentDao dao;
	
	@Autowired
	private AES256Util aes256;
	
	public Map<String, Object> insertPayment(Map<String, Object> map) throws Exception {
	
		String cardNo = null;
		String cardExpireDate = null;
		Integer cardCvc = 0;
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal balanceVat = BigDecimal.ZERO;
		
		if ("PAYMENT".equals(map.get("status"))) {
			cardNo = StringUtils.leftPad((String)map.get("cardNo"),16,"0");			
			cardExpireDate = (String)map.get("cardExpireDate");
			cardCvc = (int)map.get("cardCvc");			
		} else {
			String encryptCardStr = "";			
			//취소시 유효성 검사를 위해 결제금액 및 부가가치세 누적 금액 계산   
			List<Map<String, Object>> list = dao.getPaymentInfo(map);
			for (Map<String, Object> payment : list) {
				if ("PAYMENT".equals(payment.get("STATUS"))) {
					encryptCardStr = (String) payment.get("CARD_INFORMATION");
					balanceAmount = balanceAmount.add(new BigDecimal( (Long) payment.get("AMOUNT")  ));
					balanceVat = balanceVat.add(new BigDecimal( (Long) payment.get("VAT")  ));
				} else {
					balanceAmount = balanceAmount.subtract(new BigDecimal( (Long) payment.get("AMOUNT")  ));
					balanceVat = balanceVat.subtract(new BigDecimal( (Long) payment.get("VAT")  ));					
				}
			}
			// 취소시 결제 시 암호화된 카드정보 얻어옴
			String plainCardStr = aes256.decrypt(encryptCardStr);
			if (null != plainCardStr && plainCardStr.length() == 25) {
				String[] arrCardInfo = plainCardStr.split(",");
				cardNo = arrCardInfo[0];
				cardExpireDate = arrCardInfo[1];
				cardCvc = Integer.parseInt(arrCardInfo[2]);					
				map.put("cardNo",cardNo);
				map.put("cardExpireDate",cardExpireDate);
				map.put("cardCvc",cardCvc);
				map.put("installmentMonths", 0); // 취소시 할부개월수 일시불 처리
			}
		}
		
		// 신용번호, 유효기간, CVC 정보 콤마(,) 구분자로 문자열 생성후 암호 
		String plainCardStr = cardNo+","+cardExpireDate+","+cardCvc;
		logger.debug("plainCardStr {}", plainCardStr);
		String encryptCardStr = aes256.encrypt(plainCardStr);
		logger.debug("encryptCardStr {}", encryptCardStr);
		map.put("cardInformation", encryptCardStr);
		
		BigDecimal amount = new BigDecimal( (Integer) map.get("amount")  );
		BigDecimal vat = BigDecimal.ZERO;
		
		if (null != map.get("vat")) {
			vat = new BigDecimal( (Integer) map.get("vat")  );
		} else {		
			// 부가가치세 안남길경우 자동계산
			if (amount.compareTo(new BigDecimal(100)) == 1 ) {				
				vat = amount.divide(new BigDecimal(11),BigDecimal.ROUND_HALF_UP);
			}
		}
		
		if (vat.compareTo(amount) == 1)
			throw new BusinessException("VAT can't be greater then payment amount !!",ErrorCode.INVAILD_VAT);
		
		if ("CANCEL".equals(map.get("status"))) {			
			// 취소시 부가가치세 null 일경우 남은 부가가치세 자동결제
			if (null == map.get("vat")) {
				if (vat.compareTo(balanceVat) == 1) 
					vat = balanceVat;
			}
			
			// 취소시 취소금액이 남은결제금액 보다 크면 안된다.
			if (amount.compareTo(balanceAmount) == 1)
				throw new BusinessException("Cancel Amount can't be greater then balance amount !!",ErrorCode.INVAILD_CANCEL_AMT);
			// 취소시 부가가치세가 남은 부가가치세 보다 크면 안된다.
			if (vat.compareTo(balanceVat) == 1)
				throw new BusinessException("Cancel VAT can't be greater then balance VAT !!",ErrorCode.INVAILD_CANCEL_VAT);
		}	
		
		map.put("vat", vat);
		map.put("amount", amount);
		
		logger.debug("{}", (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(map));
		
		dao.insertPayment(map);
		
		logger.debug("unique_id {}", map.get("unique_id"));
		
		return  map;
		
	}
	
	/*
	 * 카드사 전송 문자열
	 */
	public Map<String, Object> sendPayment(Map<String, Object> map) {		
		
		logger.debug("status is [{}]",StringUtils.rightPad((String)map.get("status"),10," "));
		logger.debug("unique_id is [{}]",StringUtils.rightPad(Long.toString((long) map.get("unique_id")),20," "));
		logger.debug("cardNo is [{}]",StringUtils.rightPad((String)map.get("cardNo"),20," "));
		logger.debug("installmentMonths is [{}]",StringUtils.leftPad(Long.toString((int) map.get("installmentMonths")),2,"0"));
		logger.debug("cardExpireDate is [{}]",StringUtils.rightPad((String)map.get("cardExpireDate"),4," "));
		logger.debug("cardCvc is [{}]",StringUtils.rightPad(Long.toString((int)map.get("cardCvc")),3," "));
		logger.debug("amount is [{}]",StringUtils.leftPad(((BigDecimal)map.get("amount")).toString(),10,"0"));
		logger.debug("vat is [{}]",StringUtils.leftPad(((BigDecimal)map.get("vat")).toString(),10,"0"));
		
		if (null == map.get("uniqueIdPayment")) {
			logger.debug("uniqueIdPayment is [{}]",StringUtils.rightPad("",20," "));
		} else {
			logger.debug("uniqueIdPayment is [{}]",StringUtils.rightPad(Long.toString((int)map.get("uniqueIdPayment")),20," "));
		}
		
		logger.debug("cardInformation is [{}]",StringUtils.rightPad((String)map.get("cardInformation"),300," "));
		logger.debug("filler is [{}]",StringUtils.rightPad("",47," "));
		
		String send = StringUtils.rightPad((String)map.get("status"),10," ");
		send += StringUtils.rightPad(Long.toString((long) map.get("unique_id")),20," ");
		send += StringUtils.rightPad((String)map.get("cardNo"),20," ");
		send += StringUtils.leftPad(Long.toString((int) map.get("installmentMonths")),2,"0");
		send += StringUtils.rightPad((String)map.get("cardExpireDate"),4," ");
		send += StringUtils.rightPad(Long.toString((int)map.get("cardCvc")),3," ");
		send += StringUtils.leftPad(((BigDecimal)map.get("amount")).toString(),10," ");  
		send += StringUtils.leftPad(((BigDecimal)map.get("vat")).toString(),10,"0");
		
		if (null == map.get("uniqueIdPayment")) {
			send += StringUtils.rightPad("",20," ");
		} else {
			send += StringUtils.rightPad(Long.toString((int)map.get("uniqueIdPayment")),20," ");
		}
		
		send += StringUtils.rightPad((String)map.get("cardInformation"),300," ");
		send += StringUtils.rightPad("",47," ");
		
		send = StringUtils.leftPad(""+send.length(),4," ")+send;
		
		logger.debug("send string is [{}]",send);
		
		map.put("sendString", send);
		
		dao.sendPayment(map);
		
		return  map;
	}
	
	public Map<String, Object> getPayment(Map<String, Object> map) throws Exception {
		Map<String, Object> mapPayment = dao.getPayment(map);
		map.putAll(mapPayment);
		
		// 암호화된 카드정보 얻어옴
		String encryptCardStr = ((String)mapPayment.get("CARD_INFORMATION"));
		logger.debug("encryptCardStr {}", encryptCardStr);
		String plainCardStr = aes256.decrypt(encryptCardStr);
		logger.debug("plainCardStr {}", plainCardStr);
		
		if (null != plainCardStr && plainCardStr.length() == 25) {
			String[] arrCardInfo = plainCardStr.split(",");
			String cardNo = arrCardInfo[0].replace("000000", "");
			// 카드번호 마스킹 처리
			if (16 == cardNo.length()) {
				cardNo = Utils.maskCardNumber(cardNo, "######*******###");
			} else {
				cardNo = Utils.maskCardNumber(cardNo, "####****##");
			}
			map.put("cardNo",cardNo);			
			map.put("cardExpireDate",arrCardInfo[1]);
			map.put("cardCvc",Integer.parseInt(arrCardInfo[2]));
		}
		return map;
	}

}
