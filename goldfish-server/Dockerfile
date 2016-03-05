FROM java:8
MAINTAINER Dawid Kubiak


ADD http://central.maven.org/maven2/com/github/dkubiak/goldfish-server/1.0/goldfish-server-1.0.jar /

EXPOSE 9000 8686

CMD ["java","-jar","goldfish-server-1.0.jar","--server.port=9000"]
