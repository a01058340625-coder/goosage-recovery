# GooSage API

행동 데이터 기반 학습 상태 판단 엔진  
A decision engine that analyzes study behavior and recommends the next action.

---

## 1. Overview

GooSage API는 사용자의 학습 행동 데이터를 수집하고,  
현재 학습 상태를 판단한 뒤 다음 행동(NextAction)을 추천하는 백엔드 엔진입니다.

단순 결과가 아닌 “행동 흐름”을 기반으로 상태를 해석합니다.

---

## 2. Why this project?

기존 학습 앱은 결과만 보여줍니다.  
GooSage는 행동 데이터를 기반으로 현재 상태를 판단하고,  
지금 필요한 최소 행동을 추천하는 것을 목표로 합니다.

---

## 3. Core Features

### Study Event 수집
- JUST_OPEN  
- QUIZ_SUBMIT  
- REVIEW_WRONG  
- WRONG_REVIEW_DONE  

### 상태 스냅샷 생성
- 최근 활동량  
- 연속 학습 일수  
- 오답 복습 수  
- 마지막 학습 이후 시간  

### 상태 판단 (Prediction)
- SAFE / WARNING / DANGER  

### NextAction 추천
현재 상태에서 가장 적절한 다음 행동 제안

- TODAY_DONE  
- READ_SUMMARY  
- JUST_OPEN  
- REVIEW_WRONG_ONE  
- RETRY_QUIZ  

---

## 4. Architecture

API → Application → Domain → Infra

- Rule 기반 판단 구조  
- 상태 흐름 중심 엔진  

---

## 5. Example Output

```text
level=WARNING
reason=REVIEW_WRONG_PENDING
action=REVIEW_WRONG_ONE

evidence:
daysSinceLastEvent=0
recentEventCount3d=6
streakDays=2
wrongReviews=3
quizSubmits=5

```

## 6. Tech Stack

- Java
- Spring Boot
- MySQL
- Docker

## 7. Run
docker-compose up

또는

./mvnw spring-boot:run

Windows:

mvnw.cmd spring-boot:run

## 8. Goal

행동 데이터를 기반으로
현재 상태를 해석하고 다음 행동을 추천하는
Decision Engine 구축

## 9. Related
goosage-scripts (시뮬레이션)
video-behavior-lab (영상 행동 분석)

---

