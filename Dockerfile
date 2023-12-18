FROM gradle:7.6.0-jdk17 as build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
COPY . /home/gradle/src

#RUN gradle build -x test
#RUN gradle build --debug
RUN gradle build


# TODO: check slim version of eclipse-temurin:17 and update it here
FROM eclipse-temurin:17
RUN mkdir /usr/src/app
COPY --from=build /home/gradle/src/build/libs/*SNAPSHOT.war /usr/src/app/app.war
USER root

EXPOSE 9000
RUN apt-get update
RUN apt-get -y upgrade
RUN apt-get install curl

CMD ["java", "-jar", "/usr/src/app/app.war"]