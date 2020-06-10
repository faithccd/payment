
# Rest API 기반 결제시스템 프로토타입


## 빌드 및 실행 방법

컴파일 및 빌드

```sh
mvn clean compile
```

Maven 에서 직접 실행

```sh
mvn spring-boot:run
```

Application 실행

```sh
mvn clean package spring-boot:repackage
java -jar target/payment.jar
```

## 개발 환경 및 프로젝트 구성

Spring Boot 2.2.5 starter 를 활용하였기 때문에 JDK 1.8 이상 환경이 필요하다. 사용하고 있는 주요 오픈 소스 요소는 다음과 같다.

* [Spring Boot 2.2.5](https://spring.io/projects/spring-boot/)
* [H2 1.4.200](https://www.h2database.com/)
* [Apache Commons Projects (DBCP, BeanUtils, Collections...)](http://commons.apache.org/)
* [Logback 1.2.3](http://logback.qos.ch/)
* [MyBatis 3.5.4](http://www.mybatis.org/)
* [Jackson 2.10.2](https://www.jax.org//)

## 문제해결


## 테이블 레이아웃
Application 실행시 Embedded type H2 로 아래 테이블 스키마 자동 생성 시킴

### Payment
Column | Name | type | Key | Desc
------ | ---- | ---- | --- | ----
unique_id | 관리번호 | BIGINT | key |  auto_increment
unique_id_payment | 원거래 관리번호 | BIGINT |  |  
card_information | 카드정보 | VARCHAR2(128) |  |  카드번호,유효일자,CVS 콤마구분자 AES256 암호화문자열
installment_months | 할부개월수 | INTEGER |  |  
amount | 금액 | BIGINT |  |  
vat | 부가가치세 | BIGINT |  |  
status | 결제상태 | VARCHAR2(16) |  | PAYMENT:결제, CANCEL:취소
timestamp | 생성시점 | BIGINT |  |

### Payment_send
Column | Name | type | Key | Desc
------ | ---- | ---- | --- | ----
unique_id | 관리번호 | BIGINT | key |  auto_increment
send_string | 전송문자열 | VARCHAR2(450) |  |  
timestamp | 생성시점 | BIGINT |  
