From openjdk:8-jdk-alpine
copy ./target/user-management-api-0.0.1-SNAPSHOT.jar /app/user-management-api-0.0.1-SNAPSHOT.jar
copy ./target/lib /app/lib
copy ./target/classes /app
CMD ["java","-cp","app:app/lib/*","com.auth.userManagement.ResourceServerApplication"]