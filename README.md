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

> ⭐ [Frontend rework repository 링크](https://github.com/OliveLover/Ahimmoyak-LMS-CMS-Frontend-Rework?tab=readme-ov-file)

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

 ## 🗺 기능 동작

 <details>
  <summary>
     $\rm{\normalsize{\color{#6580DD}훈련과정\ 구성하기}}$
  </summary>

  <br />

  <p>화면 우측 상단에서 "새 과정생성" 버튼을 클릭합니다.</p>
<img width="1679" alt="image" src="https://github.com/user-attachments/assets/570bc0ba-c100-41e0-b934-fdeba1e77dc4" />

<p>훈련 과정 생성에 필요한 훈련과정의 메타정보를 입력한 뒤, "과정 생성" 버튼을 클릭합니다.</p>
<img width="1679" alt="image" src="https://github.com/user-attachments/assets/ad875c69-c0a3-4618-8efa-abac39307fb2" />


<p>수강생에게 보여질 훈련과정의 썸네일을 선택하여 업로드합니다. 썸네일에 대한 파일의 메타정보가 실시간으로 확인이 가능하며, 업로드 완료시 Toast로 알림이 가게 됩니다.</p>
<img width="1673" alt="image" src="https://github.com/user-attachments/assets/77c59470-24dd-4532-85e6-90cfe825d71d" />


<p>"+차시 추가" 버튼을 클릭하여 차시제목을 입력한 후, "확인"을 클릭하면 차시가 생성됩니다.</p>
<img width="1680" alt="image" src="https://github.com/user-attachments/assets/2d02e951-1b48-44f2-8eeb-40380a9b4c7b" />


<p>"+콘텐츠 추가" 버튼을 클릭하여 콘텐츠를 추가합니다. 콘텐츠는 "영상"과 "퀴즈"의 형태로 설정할 수 있습니다.</p>
<p>첫 번째 페이지에서 페이지 이름을 입력한 후, "영상"을 선택하여 업로드합니다. 업로드한 파일의 메타정보는 실시간으로 확인할 수 있으며, 진행 상황도 Toast 메시지를 통해 즉시 확인 가능합니다.</p>
<p>* react-player는 mp4, webm, ggg는 지원하지만, wav를 지원하지 않습니다. 현업에서는 mp4와 wav를 많이 사용하기 때문에 mp4만 허욕하도록 설정하였습니다.</p>
<img width="1680" alt="image" src="https://github.com/user-attachments/assets/5cd4c617-cee6-48a5-9c4b-8deec105fe97" />


<p>"+콘텐츠 추가" 버튼을 클릭하여 콘텐츠를 추가하고, 빈 페이지에 "퀴즈"를 입력해보겠습니다. "퀴즈 추가"를 클릭합니다.</p>
<img width="1680" alt="image" src="https://github.com/user-attachments/assets/e060cee1-f4e6-4a71-9d9a-d73325c49feb" />


<p>아래와 같이 문제와, 퀴즈의 선지 수를 선택하고(최대 5개) 정답을 선택 후, 해설을 작성합니다.</p>
<p>* 훈련 과정의 차시 구성은 사용자의 편의성을 위하여 blur로 즉각적으로 반영됩니다. 새로고침을 하더라도 데이터는 남아 있습니다.</p>
<img width="1680" alt="image" src="https://github.com/user-attachments/assets/5b15ae27-9c4e-4c57-a02a-92af44eb2a83" />

</details>

 <details>
  <summary>
     $\rm{\normalsize{\color{#6580DD}구성한\ 훈련과정\ 미리보기}}$
  </summary>

  <br />

  <p>앞서 미리 구성해둔 훈련 과정은 "미리 보기"를 통하여 어떻게 구성되는지 확인이 가능합니다. "미리 보기"를 클릭합니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/517600d4-b324-4b36-b785-7704efbf32ae" />

  <p>구성된 훈련과정을 미리 볼 수 있습니다. 이 화면은 수강생들에게 보여질 화면을 예상할 수 있게 합니다.</p>
  <p>인덱스를 클릭하면 해당 페이지로 바로 이동이 가능합니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/5c464acc-883d-4585-b07b-41347ec48271" />

</details>

 <details>
  <summary>
     $\rm{\normalsize{\color{#6580DD}구성한\ 훈련과정의\ 퀴즈 풀어보기}}$
  </summary>

  <br />

  <p>미리 구성해둔 훈련 과정의 퀴즈 페이지로 이동합니다.</p>
  <p>퀴즈의 문제를 읽고 선택지를 고르면 정답과 함께 해설이 표시됩니다. "다음 퀴즈"버튼을 클릭하여 다음 퀴즈를 풀 수 있습니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/f182ab35-1ee7-42b7-a51e-80a32ec323d6" />

  <p>정답이 일치 하지 않으면 alert와 함께 2번의 기회가 더 주어집니다. 2번의 기회가 지나면 정답과 함께 해설이 표시됩니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/889cdeb6-1ff4-4b3c-9df2-fc34b87b5b83" />
  <img width="1679" alt="image" src="https://github.com/user-attachments/assets/4c775663-36f8-4b5f-9b79-d30216e4aa8a" />

  <p>모든 퀴즈를 풀이하면 결과 화면을 통해 어떤 문제가 틀렸고, 맞췄는지 진단이 가능하며, 다시 풀기 기능으로 복습이 가능합니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/1ac5533f-be6c-45b5-aa53-0e155caafb85" />

</details>

 <details>
  <summary>
     $\rm{\normalsize{\color{#6580DD}구성한\ 훈련과정의\ 영상 UI}}$
  </summary>

  <br />

  <p>업로드된 영상은 페이지의 인덱스의 순서에 맞게 배치됩니다.</p>
  <p>UI는 사용자의 편의성을 위해 Index, 페이지 이동, 소리 조정, 큰 화면으로 보기, 재생시간 조절, "space bar"를 입력하면 영상의 재생과 정지가 가능하도록 구성하였습니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/70f7dcd7-02dd-42f9-b2bf-b050f674f071" />

  
</details>

 <details>
  <summary>
     $\rm{\normalsize{\color{#6580DD}구성한\ 훈련과정의\ 페이지 순서 바꾸기(차시 순서 바꾸기}}$
  </summary>

  <br />

  <p>아래와 같은 구성으로 4페이지의 "본강의 2"의 앞에 5페이지의 "퀴즈"를 삽입하여 위치를 바꾸도록 하겠습니다. "."6개가 있는 아이콘을 클릭하고 드래그하여 5페이지의 "퀴즈"를 4페이지의 "본강의 2"위로 드랍해줍니다.</p>
  <img width="1675" alt="image" src="https://github.com/user-attachments/assets/772245fb-f144-4124-83b7-e98f42171d93" />

  <p>4페이지와 5페이지의 순서가 변경되었습니다. 변경은 즉각적으로 이루어 지며, 페이지의 인덱스가 자동으로 재정렬 되어 페이지와 제목이 맞지 않는 것을 확이할 수 있습니다. 확인을 위하여 제목은 변경하지 않고, "미리 보기"를 통해 확인하겠습니다.</p>
  <img width="1679" alt="image" src="https://github.com/user-attachments/assets/b6acdf2b-7987-42a2-808f-ba31ac382c7b" />

  <p>아래 인덱스에서 확인 가능하듯이 순서가 즉각적으로 변경된 것이 확인 가능합니다. 동작 또한 바뀐 상태로 하게 됩니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/e28526ba-59ac-4c35-bd8f-83b7b3b6de79" />

  <p>* 차시 또한 "."6개 아이콘을 클릭하면 동일한 방식으로 손쉽게 차시의 순서 변경이 가능합니다.</p>
  <img width="1679" alt="image" src="https://github.com/user-attachments/assets/bc6f1725-e9f6-42c4-927f-f021e0c0c251" />


</details>

 <details>
  <summary>
     $\rm{\normalsize{\color{#6580DD}구성한\ 훈련과정의\ 페이지,\ 차시,\ 과정\ 삭제하기}}$
  </summary>

  <br />

  <p>구성된 훈련과정은 아래와 같이 간단한 테이블 형식으로 메타정보를 확인 할 수 있습니다.</p>
  <p>삭제하고자 하는 훈련과정의 행을 "더블클릭"합니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/dfb5107a-adc3-4752-b340-b7f22afc620a" />

  <p>해당 훈련 과정의 "2차시"의 1페이지의 "1.Opening"을 삭제 하도록 하겠습니다. 페이지 우측 상단의 "x"버튼을 클릭합니다.</p>
  <img width="1649" alt="image" src="https://github.com/user-attachments/assets/966ba9e9-40c0-4e1b-9dc5-1836988d5285" />

  <p>삭제 또한 실시간 적으로 반영됩니다. 1페이지의 "1.Opening"이 삭제되고, 이전 2페이지의 "2.학습목표"가 1페이지로 재정렬 되었습니다. "미리 보기"를 통해 확인해 보겠습니다.</p>
  <img width="1639" alt="image" src="https://github.com/user-attachments/assets/e1ad1205-24ad-4ae2-9211-81cec4c045c3" />

  <p>총 페이지 수가 6페이지 -> 5페이지 로 줄어들었으며, Index를 확인하면 1페이지의 "1.Opening"이 제거되고 1페이지가 "2.학습 목표"가 되었습니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/68e67763-79fb-4619-a035-a8cf90e68e18" />
  
  <p>해당 훈련과정의 차시를 제거해 보도록 하겠습니다. 차시의 우측 상단의 "x"버튼을 클릭합니다.</p>
  <img width="1678" alt="image" src="https://github.com/user-attachments/assets/72a59f40-faa8-4eb4-b5c0-044160011fbf" />

  <p>훈련 과정이 구성되어 있던 차시의 삭제가 즉각적으로 반영되었습니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/350bc450-fea5-4f56-ac01-fcd1dc4ce4ef" />

  <p>훈련 과정 제거를 하겠습니다. 훈련과정 제거에 앞서 임의의 훈련과정 5개를 생성하였습니다.</p>
  <p>제거하고자 하는 훈련과정을 체크박스로 선택한 후, 해당 영역의 "선택 삭제"버튼을 클릭합니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/6f81cd0e-38bd-459b-b8d3-4f998d1e04cc" />


  <p>선택한 훈련 과정이 제거되었습니다.</p>
  <img width="1680" alt="image" src="https://github.com/user-attachments/assets/f00480e0-bdc6-4943-a806-25ed34e36654" />

  <p>* 차시 삭제 : 해당 차시와 관련된 영상과 퀴즈가 모두 제거됩니다. </p>
  <p>* 훈련 과정 삭제 " 해당 훈련과정과 관련된 차시와 영상, 퀴즈, 썸네일이 모두 제거됩니다.</p>

</details>
