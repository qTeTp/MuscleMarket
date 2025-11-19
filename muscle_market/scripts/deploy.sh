#!/bin/bash

# 1. 환경변수 로드 (SSH 비대화형 모드에서도 환경변수를 읽기 위함)
# /etc/environment에 DB 정보, JWT 키 등을 저장해뒀다고 가정
set -a
source /etc/environment
set +a

# 2. 배포 디렉토리 설정 (원하는 경로로 수정 가능)
DEPLOY_PATH="$HOME/muscle-market"
JAR_NAME="muscle-market.jar"

echo "==== 배포 시작 : $(date) ===="

# 3. 현재 구동 중인 애플리케이션 pid 확인 및 종료
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> 실행중인 애플리케이션 종료 (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

# 4. 새 애플리케이션 실행
echo "> 새 애플리케이션 실행"
# nohup: 백그라운드 실행, 로그는 nohup.out에 저장
# prod 프로필 적용
nohup java -jar -Dspring.profiles.active=prod -Dspring.jpa.hibernate.ddl-auto=create $DEPLOY_PATH/$JAR_NAME > $DEPLOY_PATH/nohup.out 2>&1 &

echo "==== 배포 완료 ===="