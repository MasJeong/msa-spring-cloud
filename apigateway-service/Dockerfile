# docker build -t masjeong/apigateway-service:1.0 .

# 1단계: 빌드 스테이지 (JDK 포함)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# 1.1 의존성 캐싱을 위한 단계
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
RUN ./gradlew dependencies --no-daemon

# 1.2 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew clean build -x test --no-daemon && \
    java -Djarmode=layertools -jar build/libs/*.jar extract

# 2단계: 런타임 스테이지 (JRE 경량화)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

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