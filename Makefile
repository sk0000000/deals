build:
	mvn clean install

run:
	mvn spring-boot:run

test:
	mvn test

coverage:
	mvn jacoco:report
