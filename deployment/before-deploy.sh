#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then

	openssl aes-256-cbc -K $encrypted_ab1c605bcedb_key -iv $encrypted_ab1c605bcedb_iv -in deployment/codesigning.asc.enc -out deployment/codesigning.asc -d

    gpg2 --fast-import deployment/codesigning.asc

    echo "keys"
    gpg2 --list-keys

    echo "secret keys"
    gpg2 --list-secret-keys
fi