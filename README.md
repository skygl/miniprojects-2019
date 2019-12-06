# TurkeyBook

> 우아한테크코스 level 2 미니프로젝트

페이스북 기반의 서비스

## Member

우테코 닉네임 | 이름 | 역할
:--: | :--: | :--:
베디 | 배대준 | 백엔드 & 프론트엔드 & 검색기능
철시 | 박경철 | 백엔드 & 프론트엔드 & API 문서화
이지 | 김지훈 | 백엔드 & 프론트엔드 & 태그기능
히브리 | 송원근 | 백엔드 & 프론트엔드 & 실시간 유저 현황
올라프 | 백현영 | 백엔드 & 프론트엔드 & 메신저

## Environment

tool | version
:--: | :--:
Java | JDK 1.8
Spring Boot | 2.1.7.RELEASE
DB(MySQL) | 8.0.17
AWS EC2 | -
AWS S3 | -

## 구현 기능

- 로그인 / 회원가입
- 글 작성, 수정, 삭제
- 친구 글 및 내 글만 조회하도록 기능 구현
- 해당 유저의 mypage 접근 시 해당 유저가 쓴 글 볼 수 있음
- 댓글 작성, 수정, 삭제, 조회
- 글, 댓글 좋아요
- 친구 요청, 친구 요청 삭제
- 친구 추가, 친구 삭제
- 메신저
- 검색
- 실시간 접속 현황
- 프로필 / 커버 사진 추가
- 소개 추가 기능
- API 문서화

## 시연 캡쳐

### 초기 화면

> /users/form (로그인 & 회원가입 화면)

로그인하지 않는 경우 로그인 및 회원가입 페이지으로 이동합니다.

![](https://user-images.githubusercontent.com/30178507/64216825-8bd7dd00-cef5-11e9-82e8-e6587b00350b.png)

> / (index)

로그인 한 경우 초기 화면입니다.

![](https://user-images.githubusercontent.com/30178507/64216864-c5a8e380-cef5-11e9-9e93-3ebabb0f1b94.png)

오른쪽 바는 현재 접속한 모든 유저 및 친구의 현황을 볼 수 있으며 친구 추가 요청을 보내거나 메신저를 통해 DM을 주고 받을 수 있습니다.

### 친구 요청

위 오른쪽 바에서 친구요청을 보내면 다음과 같은 화면이 등장합니다.

![](https://user-images.githubusercontent.com/30178507/64216934-1587aa80-cef6-11e9-8bf6-383cbcffacdd.png)

그러면 받은 유저는 다음과 같이 친구 요청을 볼 수 있습니다.

![](https://user-images.githubusercontent.com/30178507/64216932-14ef1400-cef6-11e9-9070-5e57e1eb3f62.png)

요청을 받은 유저가 수락을 누르면 다음과 같은 알림이 나타나면서 친구가 됩니다!

![](https://user-images.githubusercontent.com/30178507/64216936-1587aa80-cef6-11e9-897f-52a50dc97816.png)

### 마이페이지

> /users/{userId}

![1_본인_페이지_소개](https://user-images.githubusercontent.com/30178507/64217060-bb3b1980-cef6-11e9-8bd9-77e816fe89a5.png)

### 소개 작성

위 마이페이지에서 소개를 작성할 수 있습니다. (회원가입 후에 마이페이지에서 작성해야함)

소개 작성 버튼을 누르면 작성 input이 나타납니다.

![3_소개_수정](https://user-images.githubusercontent.com/30178507/64217061-bb3b1980-cef6-11e9-80b7-9e2b95026fe4.png)

수정이 완료되면 다음과 같이 수정된 소개가 나타납니다.

![4_소개_수정_완료](https://user-images.githubusercontent.com/30178507/64217062-bbd3b000-cef6-11e9-8f3c-978df0acf6ba.png)

### 글 작성

글은 마이페이지와 초기 화면에서 작성할 수 있습니다.

![게시글_삭제](https://user-images.githubusercontent.com/30178507/64217178-5502c680-cef7-11e9-9c2b-289f3b161ac3.PNG)

초기화면에서는 맨 위에 작성 칸이 있으며 작성한 경우 위 캡쳐와 같이 수정, 삭제 버튼이 나타납니다. 작성한 유저가 아니라면 나타나지 않습니다.

게시글 작성시에는 사진과 동영상이 들어갈 수 있습니다. 여러 파일을 넣을 수 있지만 총 파일의 크기가 100MB를 넘으면 안됩니다.

![게시글작성](https://user-images.githubusercontent.com/30178507/64217313-e1ad8480-cef7-11e9-9a28-125d2af1e5c8.png)
![게시글_작성_결과](https://user-images.githubusercontent.com/30178507/64217312-e1ad8480-cef7-11e9-9ae9-c92e7f6b4965.png)

아래는 동영상 
![동영상_게시](https://user-images.githubusercontent.com/30178507/64217179-5502c680-cef7-11e9-845d-891246318965.png)
![동영상_미리보기](https://user-images.githubusercontent.com/30178507/64217181-559b5d00-cef7-11e9-9c59-a250c1717f43.png)
![게시글_영상](https://user-images.githubusercontent.com/30178507/64217182-559b5d00-cef7-11e9-817f-78cb77032262.PNG)

글 작성시에 유저를 태그할 수 있습니다.

![5_친구_태그_친구_없을때](https://user-images.githubusercontent.com/30178507/64217587-ecb4e480-cef8-11e9-8962-91be320bedbd.png)
![6_친구_태그_친구_있을때](https://user-images.githubusercontent.com/30178507/64217588-ed4d7b00-cef8-11e9-9a92-9c3af578331b.png)
![7_친구_태그_체크](https://user-images.githubusercontent.com/30178507/64217589-ed4d7b00-cef8-11e9-82a2-ea1846793a01.png)
![8_친구_태그_후](https://user-images.githubusercontent.com/30178507/64217590-ed4d7b00-cef8-11e9-8c09-39d3259fd1ac.png)
![9_인덱스_페이지_친구_태그_후_글_게시](https://user-images.githubusercontent.com/30178507/64217591-ede61180-cef8-11e9-861a-dd72163c903b.png)
![10_피드_페이지_친구_태그_후_글_게시](https://user-images.githubusercontent.com/30178507/64217592-ede61180-cef8-11e9-8cf8-6b7d62ed65d0.png)

### 댓글 작성

댓글 작성은 글에서 할 수 있습니다.

![댓글작성](https://user-images.githubusercontent.com/30178507/64217405-351fd280-cef8-11e9-8c37-00f6c440c755.png)
![댓글목록](https://user-images.githubusercontent.com/30178507/64217406-35b86900-cef8-11e9-9ee9-56ebaed40ef6.png)

댓글에 대한 답글도 작성할 수 있습니다.

![답글](https://user-images.githubusercontent.com/30178507/64217399-33eea580-cef8-11e9-8ab2-9bd982390d67.png)

마찬가지로 댓글도 작성한 유저에 대해 수정, 삭제 버튼이 나타납니다.

![](https://user-images.githubusercontent.com/30178507/64217545-c55e1780-cef8-11e9-8862-dddf9770f91b.png)
![](https://user-images.githubusercontent.com/30178507/64217401-34873c00-cef8-11e9-86cc-a0b5f8dc0b3c.png)
![](https://user-images.githubusercontent.com/30178507/64217402-34873c00-cef8-11e9-9f7d-fc8e2bd7be8c.png)

### 댓글 좋아요

글과 마찬가지로 댓글에 대해서도 좋아요를 누를 수 있습니다.

![](https://user-images.githubusercontent.com/30178507/64217403-34873c00-cef8-11e9-968a-5447c2f7844b.png)
![](https://user-images.githubusercontent.com/30178507/64217404-351fd280-cef8-11e9-8abd-570dab70e51f.png)

### 검색

검색기능은 자음, 모음, 초성 검색을 지원합니다.

![](https://user-images.githubusercontent.com/30178507/64217584-ec1c4e00-cef8-11e9-9124-9ca346e2e1fe.png)
![](https://user-images.githubusercontent.com/30178507/64217585-ec1c4e00-cef8-11e9-9567-4f211c17a683.png)

위 결과화면은 다음과 같이 나타납니다.
![](https://user-images.githubusercontent.com/30178507/64217586-ecb4e480-cef8-11e9-916b-900e5fcf0bcd.png)

### API 문서화

REST API 관련 문서화를 했습니다.

![docs](https://user-images.githubusercontent.com/30178507/64217583-ec1c4e00-cef8-11e9-9345-7bc12a2ce94c.PNG)
