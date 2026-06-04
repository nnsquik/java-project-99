setup:
	cd code && mkdir -p certs

	cd code && openssl genrsa -out certs/private.pem 2048
	cd code && openssl rsa -in certs/private.pem -pubout -out certs/public.pem

	cd code && ./gradlew wrapper --gradle-version 9.5.0
	cd code && ./gradlew test
