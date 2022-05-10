# 지하철 노선 관리

[3단계 API 스펙](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed#_%EC%A7%80%ED%95%98%EC%B2%A0_%EB%85%B8%EC%84%A0_%EB%93%B1%EB%A1%9D)

## 구현할 기능 목록

- 지하철역 관련
  - [x] 지하철역 등록
  - [x] 지하철역 목록 조회
  - [x] 지하철역 삭제
- 지하철 노선 관련
  - [x] 지하철 노선 등록
  - [ ] 지하철 노선 목록
  - [ ] 지하철 노선 조회
  - [ ] 지하철 노선 수정
  - [ ] 지하철 노선 삭제
- 구간 관련
  - [ ] 구간 등록
    - [x] 이미 등록되어있는 역을 기준으로 새로운 역을 추가
    - [x] 갈래길 방지(기존 구간 사이에 추가)
    - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없음
    - [x] 상행역과 하행역이 이미 노선에 등록되어있으면, 추가할 수 없음
    - [x] 상행역과 하행역 둘 중 하나라도 노선에 등록되어있지 않으면 추가할 수 없음
  - [ ] 구간 제거
    - [ ] 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
    - [ ] 중간 역이 제거될 경우 재배치(노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨, 거리는 두 구간의 합)
    - [ ] 구간이 하나인 노선에서 마지막 구간을 제거할 수 없음

