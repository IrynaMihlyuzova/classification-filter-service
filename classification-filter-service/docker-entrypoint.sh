#!/bin/bash
set -e

# To set a deployment marker, we must run the Java agent as a separate command with the "deployment" option.
# We kick off the new relic agent for "deployment marking" in a subshell in the background.
# We wait for 180 seconds before launching this agent to allow the application to
# start up and register with New Relic. A deployment marker cannot be recorded
# without an app. There is a race condition here but 180 seconds should give the application
# sufficient time to start up.
# Also, we reset the _JAVA_OPTIONS in this subshell mainly to disable the JMX options.
# Not doing this will cause this New Relic agent to fail as the JMX rmi port would already be bound.
# We don't need JMX options for this subshell that is only meant to record deployments.
# There is a known issue with the deployment markers here in that we are getting a marker each
# for each of the instances. The open support ticket with New Relic is https://support.newrelic.com/tickets/335655/edit.
( export _JAVA_OPTIONS=""; sleep 180 && java -jar ${APPLICATION_ROOT_ENV}/newrelic.jar deployment --revision=$GIT_SHA_ENV --appname=$NEW_RELIC_APP_NAME) &

java -javaagent:${APPLICATION_ROOT_ENV}/newrelic.jar -Dcef.root=${APPLICATION_ROOT_ENV} -jar ${APPLICATION_ROOT_ENV}/app.jar

exec "$@"
