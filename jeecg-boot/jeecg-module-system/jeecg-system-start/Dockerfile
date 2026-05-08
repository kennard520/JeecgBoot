FROM registry.cn-hangzhou.aliyuncs.com/dockerhub_mirror/java:17-anolis

MAINTAINER jeecgos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

#RUN mkdir -p /jeecg-boot/config/jeecg/

WORKDIR /jeecg-boot

EXPOSE 8080

#ADD ./src/main/resources/jeecg ./config/jeecg
# 通配版本号，pom 升版本不用改 Dockerfile
ADD ./target/jeecg-system-start-*.jar ./

RUN mkdir -p /jeecg-boot/config

CMD sleep 60;java -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -jar jeecg-system-start-*.jar