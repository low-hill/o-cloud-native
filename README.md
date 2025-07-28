# Spring Cloud 기반 마이크로서비스 프로젝트

이 프로젝트는 Spring Boot와 Spring Cloud를 사용하여 마이크로서비스 아키텍처(MSA)를 구현한 샘플 프로젝트입니다. 중앙 집중식 설정 관리, 서비스 간 통신, 컨테이너 기반 배포 등 MSA의 핵심 개념을 학습하고 실습하는 것을 목표로 합니다.

## 아키텍처 개요

이 프로젝트는 여러 개의 독립적인 서비스로 구성된 모노레포(Monorepo) 구조를 따릅니다.

-   **Config Server (`configserver`)**: 모든 마이크로서비스의 설정을 중앙에서 관리하고 제공하는 서버입니다. Git 또는 로컬 파일 시스템(`native`)을 통해 설정 정보를 관리할 수 있습니다.
-   **Licensing Service (`licensingservice`)**: 비즈니스 로직을 수행하는 예제 마이크로서비스입니다. 시작 시 Config Server로부터 자신의 설정 정보를 받아옵니다.
-   **Database (`postgres`)**: `licensingservice`가 사용하는 PostgreSQL 데이터베이스입니다.

## 기술 스택

-   **언어**: Java 17
-   **프레임워크**: Spring Boot, Spring Cloud
-   **빌드 도구**: Gradle
-   **컨테이너**: Docker, Docker Compose
-   **데이터베이스**: PostgreSQL

## 사전 준비 사항

프로젝트를 실행하기 위해 다음 도구들이 설치되어 있어야 합니다.

-   Java 17 (JDK)
-   Docker
-   Docker Compose

## 실행 방법

이 프로젝트는 Gradle과 Docker Compose를 사용하여 한 번의 명령어로 모든 서비스를 빌드하고 실행할 수 있도록 구성되어 있습니다.
```bash
# Now we are going to use docker-compose to start the actual image.  To start the docker image, stay in the directory containing  your chapter 5 source code and  Run the following command: 
$ gradle buildAndComposeUp

$ ./gradlew :licensing-service:build
```


## 서비스 접속 정보

서비스가 정상적으로 실행되면 다음 URL을 통해 각 서비스에 접근할 수 있습니다.

-   **Config Server**: `http://localhost:8071`
    - http://127.0.0.1:8071/licensing-service/dev
-   **Licensing Service**: `http://localhost:8080`


