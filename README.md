# E-commerce
물건을 사고 파는 온라인 쇼핑몰 

## 1. 개요
+ 개발 인원 : 1명
+ 개발 기간 : 23.3.29 ~
+ 간단 소개 : 일반 사용자는 본인의 정보를 수정할 수 있고, 쇼핑몰에 등록된 상품들을 구매할 수 있다. 쇼핑몰 관리자는 상품들을 등록하고 관리할 수 있다.

## 2. 기능 요구사항
+ 상품 관리
  + 관리자는 상품을 등록하고 수정할 수 있다
  + 상품을 등록할 때 이미지를 업로드해야 한다
  + 메인 화면에서 상품을 조회수가 높은 순으로 보여주어야 한다

+ 회원 관리
  + 회원 가입을 할 수 있다
  + 회원은 자신의 정보를 수정하고 탈퇴할 수 있다
  + 로그인을 할 수 있고, 로그인 상태가 유지되어야 한다
  + 인가되지 않은 사용자가 리소스에 접근하는 것을 막아야 한다

+ 주문 관리
  + 상품의 재고가 있다면 회원은 아이템을 주문할 수 있다 
  + 주문 완료상태일 때, 주문 취소할 수 있다
 
## 3. 기술 스택
자바17, 스프링부트, JPA, MySQL, Redis

## 4. ERD
![image](https://github.com/hossang/ecommerce/assets/60059710/0aec0e3a-2ae8-49eb-a347-4799bf268a4b)
<!--
## 5. API 설계
|URL|Method|Description|
|---|---|---|
|/|Get|메인 화면 페이지|
||||
|/sign-up|Get|회원가입 페이지|
|/sign-up|Post|회원가입|
|/sign-in|Get|로그인 페이지|
|/users/{username}/modify|Get|회원 정보 수정 페이지|
|/users/{username}/modify|Post|회원 정보 수정|
|/users/{username}/remove|Get|회원 삭제 페이지|
|/users/{username}/remove|Post|회원 삭제|
|/admins/{username}/users|Get|회원 리스트 조회|
||||
|/admins/{username}/items|Get|아이템 리스트 조회|
|/admins/{username}/items/register|Get|아이템 등록 페이지|
|/admins/{username}/items/register|Post|아이템 등록|
|/items/{id}|Get|아이템 상세 페이지|
|/admins/{username}/items/{id}/modify|Get|아이템 수정 페이지|
|/admins/{username}/items/{id}/modify|Post|아이템 수정|
||||
|/users/{username}/orders/cart|Get|장바구니 페이지|
|/users/{username}/orders/cart|Post|아이템 장바구니에 등록|
|/users/{username}/orders/{id}/create|Get|결제 페이지|
|/users/{username}/orders/{id}/create|Post|결제|
|/users/{username}/orders|Get|주문 리스트 조회|
|/users/{username}/orders/{id}|Get|상세 주문 페이지|
|/users/{username}/orders/{id}cancel|Post|주문 취소|

이미지 api도 넣어줘야 하나?
-->
