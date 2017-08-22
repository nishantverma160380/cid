FROM webdizz/centos-java8
VOLUME /tmp
ADD target/adminWebApp.war adminWebApp.war
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /adminWebApp.war" ]