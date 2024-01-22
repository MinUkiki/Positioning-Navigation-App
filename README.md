# Indoor-Positioning-Navigation-App
실내 공간에서 사용자가 자신의 위치를 파악하고 길을 찾도록 도와주기 위한 실내 측위가 가능한 네비게이션 앱

# 1. 모델 학습하기

1. 학습할 데이터 준비

2. `Training_MobileNet.ipynb`을 열기

3. 학습할 데이터 경로 설정

4. 코드 실행 후 .tflite의 확장명을 가진 파일 생성

# 2. 안드로이드 스튜디오에 모델 연동

![Untitled]({{site.url}}/images/image_13.png)

![Untitled]({{site.url}}/images/image_14.png)

![Untitled]({{site.url}}/images/image_15.png)


# 3. 안드로이드 파일 추가

`ImaveViewActivity.java`

`MainActivity.java`

app > src > main > java > com > example > "프로젝트 명"

---

`activity_imave_view.xml`

`activity_main.xml`

app > src > main > res > layout 

각각의 파일을 해당 경로에 넣기

# 4. 실행

emulate 또는 모바일에 앱을 실행