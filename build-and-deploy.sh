#!/bin/sh
# Build and install snapshot plugin in Sonar

# Build first and check status
mvn clean install
if [ "$?" != 0 ]; then
	echo "ERROR - Java build failed!" 1>&2
	exit $?
fi

# Run shell tests
shelltest src/test/shell --execdir --diff
if [ "$?" != 0 ]; then
	echo "ERROR - Shell tests failed!" 1>&2
	exit $?
fi

# Deploy new verion of plugin in Sonar dir
cp target/sonar-objective-c-plugin-0.3.1.jar $SONARQUBE_HOME/extensions/plugins

# Stop/start Sonar
$SONARQUBE_HOME/bin/macosx-universal-64/sonar.sh stop
$SONARQUBE_HOME/bin/macosx-universal-64/sonar.sh start

