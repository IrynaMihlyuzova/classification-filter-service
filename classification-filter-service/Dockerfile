FROM openjdk:8

ARG JAR_FILE
ARG GIT_SHA
ARG APPLICATION_ROOT=/cef

# JAR_FILE must be set and must not be empty.
# GIT_SHA must be set and must not be empty.
# APPLICATION_ROOT must be set and must not be empty.
RUN : x="${JAR_FILE:?The JAR_FILE build argument must be the path to the application jar file.}" && \
  x="${GIT_SHA:?The GIT_SHA build argument must be a Git commit SHA value (e.g. \"git rev-parse HEAD\").}" && \
  x="${APPLICATION_ROOT:?The APPLICATION_ROOT build argument must be a directory path (e.g. \"/app\").}" && \
  unset x && \
  apt-get -y --allow-unauthenticated update && \
  apt-get -y --allow-unauthenticated upgrade && \
  mkdir ${APPLICATION_ROOT} && \
  rm -rf /var/lib/apt/lists/*

RUN mkdir -p /tmp/awscli && \
  curl -sS "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "/tmp/awscliv2.zip" && \
  unzip -q /tmp/awscliv2.zip -d /tmp/awscli && \
  /tmp/awscli/aws/install && \
  rm -f /tmp/awscliv2.zip && \
  rm -rf /tmp/awscli

RUN curl -sSL "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" \
     -o /tmp/newrelic-java.zip && \
  unzip -q /tmp/newrelic-java.zip -d /tmp && \
  rm -f /tmp/newrelic-java.zip && \
  cp /tmp/newrelic/newrelic.jar ${APPLICATION_ROOT} && \
  cp /tmp/newrelic/newrelic.yml ${APPLICATION_ROOT} && \
  rm -rf /tmp/newrelic

# Add the api_host entry to the newrelic.yml to aid deployment markers
# http://docs.newrelic.com/docs/java/recording-deployments-with-the-java-agent
# It was a deliberate choice to use SED instead of a library to edit the yaml file as this
# is a one-off use case and SED is readily available on most *nix machines.
# If we are having to do this more where a more complex editing is required, please explore some
# libraries https://jlordiales.me/2016/05/24/jyparser/ OR https://yq.readthedocs.io/en/latest/ and such.
RUN sed -i -e '/common:/a\' -e '  api_host: rpm.newrelic.com' ${APPLICATION_ROOT}/newrelic.yml

VOLUME /tmp
WORKDIR ${APPLICATION_ROOT}
COPY ${JAR_FILE} ./app.jar

# Copy the script to run as the docker entry point to the application root
COPY docker-entrypoint.sh .
RUN chmod 700 ./docker-entrypoint.sh

# Copy the git revision and application root to an environment variable and make it available to the
# docker-entrypoint.sh for deployment marking.
ENV GIT_SHA_ENV=${GIT_SHA}
ENV APPLICATION_ROOT_ENV=${APPLICATION_ROOT}
ENTRYPOINT ["./docker-entrypoint.sh"]
