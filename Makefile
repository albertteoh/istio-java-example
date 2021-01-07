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

.PHONY: clean
clean:
	@./gradlew servicea:clean
	@./gradlew serviceb:clean
	@docker rmi -f service-a-java service-b-java
