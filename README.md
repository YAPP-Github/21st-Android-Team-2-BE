# 파인드띵스 (Find Things)

![image](https://user-images.githubusercontent.com/75151848/223892493-45c537ab-33df-4633-922e-f9cd7dd54752.png)

## 프로젝트 소개

- 누구나 쉽게 우리 집 물건을 관리하기 위한 모바일 앱
- 사용자는 빠르게 물건을 찾고 사진을 통해 물건 위치와 정보를 확인할 수 있다.

### 목적 
- 물건의 위치를 찾는데 드는 시간을 절약한다.
- 잊고 있던 공간을 효율적으로 이용한다.
- 소비기한이 임박한 물건을 리마인드한다.

## 기술 스택

- BackEnd : Kotlin, SpringBoot, Spring Data JPA, Querydsl, Swagger, Kotest, Mockk, Github actions
- Database : MariaDB, Redis
- Infra : Nginx, AWS EC2, AWS RDS, AWS S3

## 주요 기능

#### 1. 메인 탭 (물건 관리) 

<p>
    <img src="https://user-images.githubusercontent.com/75151848/224462482-92653cee-8768-4880-8217-e5dffa8ba793.png" style="width:32%" />
    <img src="https://user-images.githubusercontent.com/75151848/224462487-89e55684-ad7d-4fea-9111-9eb1ad66a7a1.png" style="width:32%" />
    <img src="https://user-images.githubusercontent.com/75151848/224462490-b12f79fb-d067-44f5-bd36-f01570837c9e.png" style="width:32%" />
</p>

- 특정 공간 안의 보관함 내부에 있는 물건 목록을 보여준다.
- 각 물건의 정보(보관함 내 위치, 이름, 대표 이미지, 종류, 태그, 소비기한 등)를 보여준다.
- 사용자는 스크롤을 내리면서 특정 물건이 보관함 내의 어떤 위치에 존재하는지 시각적으로 확인할 수 있다.

#### 2. 태그 탭

<img src="https://user-images.githubusercontent.com/75151848/224462638-cd374928-a3f2-46cd-b221-776dc3b0dcf8.png" style="width:32%" />

- 물건에 달린 태그 목록을 보여준다. 물건 종류(생활, 식품, 패션)별 태그가 달린 개수도 함께 표시한다.
- 태그를 클릭하면, 특정 태그가 달린 물건 목록을 조회할 수 있다.

#### 3. 리마인더 탭 (알림)

<img src="https://user-images.githubusercontent.com/75151848/224462668-57cefef0-74b8-4352-9d8e-14e31aad08e5.png" style="width:32%" />

- 소비기한이 임박한 순으로 물건을 정렬하여 조회한다.


