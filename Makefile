all: start

.PHONY: build
build: service-a service-b

.PHONY: start
start: build
	./scripts/start.sh

.PHONY: stop
stop:
	./scripts/stop.sh

.PHONY: service-a
service-a:
	@./gradlew servicea:build
	@docker build --build-arg JAR_FILE=servicea/build/libs/\*.jar -t service-a-java -f servicea/Dockerfile .

.PHONY: service-b
service-b:
	@./gradlew serviceb:build
	@docker build --build-arg JAR_FILE=serviceb/build/libs/\*.jar -t service-b-java -f serviceb/Dockerfile .

.PHONY: run-service-a
run-service-a:
	@docker run -p 8081:8081 service-a-java

.PHONY: run-service-b
run-service-b:
	@docker run -p 8082:8082 service-b-java

.PHONY: clean-service-b
clean-service-b:
	@./gradlew serviceb:clean
	@docker rmi -f service-b-java

.PHONY: clean-service-a
clean-service-a:
	@./gradlew servicea:clean
	@docker rmi -f service-a-java

.PHONY: clean
clean: clean-service-a clean-service-b
