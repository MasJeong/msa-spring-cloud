# docker build -t masjeong/order-service:1.0 .

# 1단계: 빌드 스테이지 (JDK 포함)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# 실행 권한 부여
COPY gradlew .
RUN chmod +x gradlew

# 의존성 캐시를 위한 설정 파일 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# 의존성만 미리 다운로드 (캐시 레이어 용)
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사
COPY src/ src/

# 빌드 및 Layered JAR extract
RUN ./gradlew clean build -x test --no-daemon && \
    java -Djarmode=layertools -jar build/libs/*.jar extract

# 2단계: 런타임 스테이지 (JRE 경량화)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 2.1 curl과 bash 설치 (healthcheck 및 exec 접속용)
RUN apk add --no-cache curl bash

# 2.1 레이어 복사 최적화 (변경 빈도 낮은 순)
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/application/ ./
COPY --from=builder /app/build/libs/*.jar app.jar

ENV _JAVA_OPTIONS "-XX:MinRAMPercentage=60.0 -XX:MaxRAMPercentage=90.0 \
-Dfile.encoding=UTF-8 \
-Dspring.output.ansi.enabled=ALWAYS \
-Duser.timezone=Asia/Seoul"

# 2.2 진입점 설정
ENTRYPOINT ["java","-jar","app.jar"]