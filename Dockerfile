FROM java:8

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y gdebi && \
    wget http://ppa.launchpad.net/natecarlson/maven3/ubuntu/pool/main/m/maven3/maven3_3.2.1-0~ppa1_all.deb && \
    gdebi --n maven3_3.2.1-0~ppa1_all.deb && \
    ln -s /usr/share/maven3/bin/mvn /usr/bin/mvn

ADD code/pom.xml /tmp/pom.xml
RUN cd /tmp && \
    mvn dependency:resolve && \
    mvn verify
