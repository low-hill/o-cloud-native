#stage 1
FROM eclipse-temurin:17-jdk-jammy as builder

# 작업 디렉터리 설정
WORKDIR /workspace/app

# Gradle Wrapper 파일들을 먼저 복사하여 의존성 레이어 캐싱을 활용합니다.
COPY settings.gradle .
COPY build.gradle .
COPY gradlew .
COPY gradle gradle
COPY eurekaserver/build.gradle ./eurekaserver/

# 의존성을 다운로드합니다. 소스 코드가 변경되지 않으면 이 레이어는 캐시됩니다.
RUN ./gradlew :eurekaserver:dependencies || true

# 애플리케이션 소스 코드를 복사합니다.
COPY eurekaserver/src ./eurekaserver/src

# Gradle을 사용하여 애플리케이션을 빌드합니다.
# CI 단계에서 테스트를 이미 수행했다고 가정하고 스킵하여 빌드 속도를 높입니다.
RUN ./gradlew :eurekaserver:bootJar -x test

#stage 2
FROM eclipse-temurin:17-jre-jammy

# 작업 디렉터리 설정
WORKDIR /app

# 빌드 환경에서 생성된 최종 JAR 파일만 복사합니다.
COPY --from=builder /workspace/app/eurekaserver/build/libs/*.jar app.jar

# 컨테이너가 시작될 때 실행할 명령을 지정합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]