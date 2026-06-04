setup:
	mkdir -p code/src/main/resources/certs
	openssl genrsa -out code/src/main/resources/certs/private.pem 2048
	openssl rsa -in code/src/main/resources/certs/private.pem -pubout -out code/src/main/resources/certs/public.pem
	cd code && ./gradlew wrapper --gradle-version 9.5.0
	cd code && ./gradlew build
