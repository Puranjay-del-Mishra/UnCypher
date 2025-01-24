# deploy.sh
mvn clean package
java -jar target/backend.war
