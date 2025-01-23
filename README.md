# 📍 기업대상 직무교육 LMS, CMS 플랫폼 Backend Rework (진행중)

### 🎯 프로젝트 목표

> - **Backend** 는 비용절약을 위해 **`SpringBoot`** 애플리케이션을 **`ECS Fargate`** , **Frontend** 는 **`AWS Cloudfront`** 로 **`CI/CD`** 구축하여 **배포 자동화**
> - MSA의 코드, 인프라 관리가 어려운 단점을 보완하기 위해 **“One Time One Set(한번에 세팅)”** 을 위한 **`IaC(Infrastructure as Code)`** 작성
> - 전문 개발인력을 두기 어려운 ‘훈련기관’을 고려하여 ‘비개발’인력도 손쉽게 교육 과정을 구성
> - **RDBMS** 보다 비교적 가볍고 비용이 저렴한 **`NoSQL(DynamoDb)`** 과 **`AWS SDK`** 를 적극적으로 사용
> - 최종발표때 말씀해주신 피드백 내용 수용

<details>
  <summary>
     $\rm{\normalsize{\color{#6580DD}피드백}}$
  </summary>

  <br />

1. 정부와 관련된 서비스는 수익성을 기대하기 어렵기 때문에 BEP(Break - evne Point)를 기대하기 어려움
2. 통합 LMS의 실시간 강의는 BEP를 기대하기 어렵기 때문에, 단순히 LMS를 만들어서 판매하는 것이 더 이득이 될 것
3. 직무교육 LMS는 집중력 유도 보다는 수료가 더 중요한 것, 라이브 스트리밍이 중요하지 않음
4. 하지만, 현실적인 문제에 대하여 솔루션을 내놓고 다른 시각으로 접근하여 도전해본것이 매우 인상적임
5. 완전한 LMS보다는 일부의 특정 문제점에 대하여 선택과 집중을 하여 구현해보는 것도 좋은 방법
6. 이벤트 기반의 서버리스 아키텍처와 ECS를 같이 운영 배포하는 것은 잘못 되었음. ECS의 경우 매우 많이 복잡하게 얽혀 있는 경우 어쩔수 없이 한 번에 올려 운영하는 경우는 있지만, 완전한 서버리스로 운영하는 것이 더 좋음
7. GitHub Actions에서 ECR에 제대로 업로드 되었는지 단계를 추가하지 않은 것이 아쉬움
   
</details>

<br />

> 🔗 [Frontend original repository 링크](https://github.com/AhHimMoYak/lms_fe)

> 🔗 [Backend original repository 링크](https://github.com/AhHimMoYak/lms_be)

> ⭐ [Frontend rework repository 링크](https://github.com/AhHimMoYak/lms_be)

<br />

## 🚩 프로젝트 개요

### 📌 프로젝트 간략 소개

- **LMS & CMS 솔루션**:  
  기업을 대상으로 하는 학습 관리 시스템(LMS)과 콘텐츠 관리 시스템(CMS)을 통합하여 제공합니다.
  
- **비개발자 친화적 구성**:  
  비개발자도 손쉽게 학습 과정을 설계하고 관리할 수 있는 사용자 중심의 인터페이스를 제공합니다.

<br />

### 🚧 프로젝트 링크
> 🚧 [프로젝트 배포 링크](https://www.ahimmoyak.shop/admin/courses) (리워크 중)

<br />

### 🗓️ 개발 기간
 2024.1.7 ~  (진행중)

<br />

 ## ⚙️ 기술 스택 & Tools

**Language**
<div align="start">
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
  <img src="https://img.shields.io/badge/css3-1572B6?style=for-the-badge&logo=css3&logoColor=white">
</div>
<br />
  
 **Framework**
<div align="start">
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white">
</div>
<br />

**Storage**
<div>
  <img src="https://img.shields.io/badge/amazon s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
  <img src="https://img.shields.io/badge/amazon dynamodb-4053D6?style=for-the-badge&logo=amazondynamodb&logoColor=white">
</div>
<br />

**Build Tool & CI/CD Tool**
<div align="start">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/Github Actions-2088FF?style=for-the-badge&logo=Github Actions&logoColor=white">
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
</div>
<br />

**AWS**
<div align="start">
  <img src="https://img.shields.io/badge/amazon ecs-FF9900?style=for-the-badge&logo=amazonecs&logoColor=white">
  <img src="https://img.shields.io/badge/amazon ecr-FF9900?style=for-the-badge&logo=amazonecr&logoColor=white">
  <img src="https://img.shields.io/badge/amazon route53-C4FFF?style=for-the-badge&logo=amazonroute53&logoColor=white">
  <img src="https://img.shields.io/badge/amazon api gateway-FF4F8B?style=for-the-badge&logo=amazonapigateway&logoColor=white">
  <img src="https://img.shields.io/badge/amazon iam-DD344C?style=for-the-badge&logo=amazoniam&logoColor=white">
  <img src="https://img.shields.io/badge/aws fargate-FF9900?style=for-the-badge&logo=awsfargate&logoColor=white">
  <img src="https://img.shields.io/badge/amazon cognito-DD344C?style=for-the-badge&logo=amazoncognito&logoColor=white">
  <img src="https://img.shields.io/badge/amazon cloudformation-FF0069?style=for-the-badge&logo=amazoncloudformation&logoColor=white">
</div>
<br />

**Tools**
<div>
  <img src="https://img.shields.io/badge/GITHUB-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/googlesheets-34A853?style=for-the-badge&logo=googlesheets&logoColor=white">
</div>
<br />
