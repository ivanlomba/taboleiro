run:
	docker-compose run --service-ports web sh -c "mvn -DskipTests package && java -jar target/taboleiro-0.3.0-SNAPSHOT.jar"

test:
	docker-compose run code sh -c "mvn clean && mvn test"

clean:
	docker-compose run code mvn clean

migratedb:
	docker-compose run web mvn flyway:migrate

cleandb:
	docker-compose run web mvn flyway:clean
