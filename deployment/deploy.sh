#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then

	echo "calling maven deploy - with settings: "
	cat deployment/settings.xml

    mvn deploy --settings deployment/settings.xml
fi