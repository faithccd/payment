package com.faithccd.payment.dao;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Inject
  	private SqlSession sqlSession;

	public int insertPayment(Map<String, Object> map) {
		return sqlSession.insert("payment.insertPayment", map);
	}
	
	public List<Map<String, Object>> getPaymentInfo(Map<String, Object> map) {		
		return sqlSession.selectList("payment.getPaymentInfo", map);
	}	
	
	public Map<String, Object> getPayment(Map<String, Object> map) {
		return sqlSession.selectOne("payment.getPayment", map);
	}
	
	public int sendPayment(Map<String, Object> map) {
		return sqlSession.insert("payment.sendPayment", map);
	}	
}
