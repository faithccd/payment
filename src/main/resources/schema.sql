CREATE TABLE payment(
unique_id BIGINT auto_increment PRIMARY KEY 
,unique_id_payment BIGINT
,card_information VARCHAR2(128)
,installment_months INTEGER
,amount  BIGINT
,vat  BIGINT
,status VARCHAR2(16)
,timestamp DATETIME
);

CREATE TABLE payment_send(
unique_id BIGINT PRIMARY KEY 
,send_string VARCHAR2(450)
,timestamp DATETIME
);
