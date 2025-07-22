FROM jenkins/jenkins:lts

USER root
ENV JAVA_OPTS="-Dhudson.model.UpdateCenter.never=true"

# Copy Zscaler CA cert into the image
COPY ZscalerRootCA.pem /usr/local/share/ca-certificates/ZscalerRootCA.crt

# Import the CA cert into the OS and Java truststore
RUN cp /usr/local/share/ca-certificates/ZscalerRootCA.crt /etc/ssl/certs/ && \
    update-ca-certificates && \
    keytool -import -trustcacerts -keystore /opt/java/openjdk/lib/security/cacerts \
      -storepass changeit -noprompt -alias zscalerrootca \
      -file /usr/local/share/ca-certificates/ZscalerRootCA.crt || true

# Install plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

USER jenkins