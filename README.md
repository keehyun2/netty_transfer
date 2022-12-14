# java network socket test 일기

### 1. java.net.Socket
- local 환경에서 java.net.Socket 을 사용해서 10MB 파일 120 개를 멀티 스레드로 보내고 받는 테스트
- 보낼때도 멀티스레드로 보내고, 받을때도 멀티스레도로 받는 로직 사용
- 중간에 간헐적으로 렉이 발생함. 이유 못찾음. 

### 2. netty framework
- java socket 방식의 old 한 방식보다는 프레임워크로 좀더 간단하고, 효율적인 개발을 하려고 시도
- 예전부터 netty 에 관심을 가지고 있었음. 책도 찾아보고 공부하려했었는데 spring 프레임워크 위주로만 사용하던 터라 netty 가 너무 낮설고, 어렵고, 사용할 곳이 그리많지 않다고 판단
- 이번 기회다 싶어서 example 소스코드 찾아서 공부하면서 테스트.
- 다시봐도 어려움.. 내가 생각하고있는 로직을 구현하는데 어떤걸 사용해야할지 감을 못잡음

### 3. java.nio.channels.SocketChannel
- java 7 에 새롭게 추가된 socketChannel 을 사용하기로함.
- zero-copy 라는 파일전송할때 효율적인 구현방식이 있다는 것을 알게됨.
- FileChannel.transferTo() 라는 함수를 통해서 zeroCopy 구현

### 4. 테스트
- local에서 server , client 2개 다 실행해서 전송하였을때 매우 빠름.. 
- 다른 컴퓨터 간에 소켓 통신 최대 속도는 **990 Mbps**  인데 zero-copy 를 사용해서 전송시에는 240Mbps 가 됨.. 
- 그리고 내 컴퓨터를 서버로 두고 다른 컴퓨터에서 zero-copy 로 전송하였더니 90Mbps 밖에 속도가 안나옴.
- 내가 보낼때랑 상대방이 보낼때랑 속도에 차이가 있음.
- 파일 전송에 영향을 주는 하드웨어 성능 - hdd(보내는쪽은 read, 받는쪽은 write 성능), 네트워크카드 성능

## to-do
- 파일질라로 파일 업로드 최대 속도를 측정? 해보고 프로그램으로도 측정해보기? 
- 파일 이어서 업로드 하기? 

## 참고
- zero-copy : 파일 전송시에 메모리에 copy 하는 단계를 줄여서 속도를 향상시키는 기술 (https://soft.plusblog.co.kr/7) 
- netty : 자바 네트워크 프레임워크 
- iperf: 컴퓨터간 최대 대역폭 측정 프로그램


