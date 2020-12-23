# CapstoneDesign - Voice Regcognition Macro
- 플레이스토어 링크 https://play.google.com/store/apps/details?id=com.hknu.capstone_designn

### 사용자가 저장한 터치작업들을 음성호출을 사용하여 순차적으로 실행시키는 앱
- 유튜브 튜토리얼 링크 https://youtu.be/-8hD61G6ip0
- 시연영상 링크 https://www.youtube.com/watch?v=jA8BIdwPn8c

(개인정보를 담고있는 일부 소스파일은 제외되어 온전히 작동하지 않음)
#### 프로젝트 계획이유
-------------------------------
운전할때나 샤워할때 처럼 일상속에서 안드로이드 스마트폰을 직접 조작하여 사용하기 어려운 상황에 닥쳤을때
빅스비(Bixby)나 구글 어시스턴트(Google Assistant)를 사용하게 되는데, 이 둘이 알아듣지 못하는 행동들을
직접 매크로로 지정해서 호출하면 좋겠다는 생각에 착안하여 개발하게 되었다.

(추가로 스마트폰을 사용하기 어려워 하는 부모님을 위해 코로나 QR코드 인증기능을 이 앱으로 예약해 드리고나서
어르신이나 장애인 분들을 위해 자주사용하는 기능들을 매크로로 예약해두는 것도 도움이 될 것이라 생각하게 되었습니다.)


#### 사용설명서
------------------------------
- 앱 로딩화면 이미지


<img src="https://user-images.githubusercontent.com/46473153/100839494-a82b4680-34b7-11eb-9bb4-09eb41c17eb1.png" width="250" height="250">


- 튜토리얼 화면


<img src="https://user-images.githubusercontent.com/46473153/100839973-7bc3fa00-34b8-11eb-8cab-31176b4356ff.png" width="250" height="450"> <img src="https://user-images.githubusercontent.com/46473153/100840067-add55c00-34b8-11eb-8d3c-c4f2f4ed9f2f.png" width="250" height="450">


- 권한 및 설정

앱을 시작하면 음성인식과 다른 앱 위에 표시 권한을 요청한다. 이후 메인화면으로 넘어가
파란색의 시작하기 버튼을 누르면 이런 접근성 설정 화면이 나온다.

![image](https://user-images.githubusercontent.com/46473153/93813501-2d130c00-fc8e-11ea-8e08-c3c340cb84a4.png)

ㄴ 사진에 표시된 화면의 설치된 서비스 항목을 누르면 터치 어시스턴트 권한을 얻을 수 있게되어
앱을 정상적으로 사용할 수 있게 된다.


- Main 화면

![image](https://user-images.githubusercontent.com/46473153/93813426-0ead1080-fc8e-11ea-9613-2937025b251e.png)

ㄴ Main 화면의 추가하기 버튼을 누르면 매크로의 이름을 입력하는 화면이 나온다.

- 매크로 추가하기 화면

![image](https://user-images.githubusercontent.com/46473153/93814016-ef62b300-fc8e-11ea-840e-27036c73737e.png)

ㄴ 추가할 매크로의 이름을 입력하는 화면으로 오직 한글만 입력이 가능하며 최소 2글자 최대 9글자 제한이 걸려있다.

- 매크로 입력 화면 (Floating Window)

![image](https://user-images.githubusercontent.com/46473153/93814216-305ac780-fc8f-11ea-8063-27aa11edbe0c.png)

ㄴ 맨 처음 나오는 아이콘으로 이 상태에서는 자유롭게 이동이 가능하다.

![image](https://user-images.githubusercontent.com/46473153/93814256-3cdf2000-fc8f-11ea-9b21-f8549c375413.png)

ㄴ 아이콘을 누르거나 이동시키면 펼쳐지는 화면으로 빨간색 재생버튼을 누르면 터치를 입력받을 수 있고 노란색 정지버튼을 누르면 입력했던 터치들이 매크로로 저장된다.
(초록색 버튼은 다시 접어주는 버튼이며 빨간색 X버튼은 매크로 저장을 취소하는 버튼이다.)

![image](https://user-images.githubusercontent.com/46473153/93814446-80d22500-fc8f-11ea-9e5a-4b1776639d75.png)

ㄴ 빨간색 재생버튼을 눌러서 빨간색의 반투명한 화면이 나온모습으로 터치를 입력받을 준비가 된 상태이다.
반투명한 화면을 누르면 눌린 지점의 좌표값을 저장하고 터치해준다.
(투명화면은 저장버튼을 누르거나 취소하기 전까지 반복해서 나온다)
