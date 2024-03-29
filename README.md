# E-commerce
물건을 사고 파는 온라인 쇼핑몰 (www.dlghckd.store)

## 0. 학습 목표
+ 그동안 학습했던 Spring, JPA를 활용해 보기
+ ┏가상 면접 사례로 배우는 대규모 시스템 설계 기초┛ 의 내용 구현해 보기

## 1. 개요
+ 개발 인원 : 1명
+ 개발 기간 : 23.3.29 ~
+ 간단 소개 : 일반 사용자는 본인의 정보를 수정할 수 있고, 쇼핑몰에 등록된 상품들을 구매할 수 있다. 쇼핑몰 관리자는 상품들을 등록하고 관리할 수 있다.

## 2. 기능 요구사항
+ 상품 관리
  + 상품을 등록할 때 이미지를 올려야 한다.
  + 관리자는 상품을 등록하고 수정할 수 있다.
  + 상품 상세페이지에 접근 시 조회수가 올라야 한다.
  + 메인 화면에서 상품을 조회수가 높은 순으로 보여주어야 한다.

+ 회원 관리
  + 회원 가입을 할 수 있다.
  + 모든 아이디는 고유해야 한다.
  + 회원은 자신의 정보를 수정하고 탈퇴할 수 있다.
  + 로그인을 할 수 있고, 로그인 상태가 유지되어야 한다.
  + 사용자의 역할에 따라 접근할 수 있는 자원을 제한해야 한다.
  + 본인의 자원 외 다른 사용자의 자원은 접근을 제한해야 한다.

+ 주문 관리
  + 주문 완료 상태일 때, 주문 취소할 수 있다.
  + 상품을 즉시 구매하거나 장바구니에 보관할 수 있다. 
  + 주문 시 상품의 재고가 부족하면 구매되어선 안 된다.
  + 상품의 재고가 있다면 회원은 아이템을 주문할 수 있다.
 
## 3. 기술 스택
Java 17, Spring Boot, JPA, MySQL, QueryDSL, Thymeleaf, Redis, AWS

## 4. ERD
![온라인 마켓플레이스](https://github.com/hossang/ecommerce/assets/60059710/b825a6f5-e5b6-4823-ae03-f830ebba7a5d)

## 5. 화면
![쇼핑몰  화면](https://github.com/hossang/ecommerce/assets/60059710/6c171eee-b407-472f-a382-503a579a97ae)

## 6. 아키텍처
![아키텍처](https://github.com/hossang/ecommerce/assets/60059710/670b4d0d-5ee8-439e-a766-97e9907010b7)

## 7. 관심 사항
+ AWS를 통한 배포
+ 캐싱을 통한 성능 향상
+ 쿼리 튜닝을 통한 성능 향상
+ BatchSize를 통한 N + 1 문제 해결
+ Redis를 사용해 상태정보를 웹 서버로부터 분리
+ Lock을 통한 상품 재고와 게시글 조회수의 데이터 일관성 유지


