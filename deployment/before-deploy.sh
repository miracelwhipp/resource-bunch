#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then

#    openssl aes-256-cbc -K $encrypted_9aa72e404c2e_key -iv $encrypted_9aa72e404c2e_iv -in deployment/codesigning.asc.enc -out deployment/codesigning.asc -d
	openssl aes-256-cbc -K $encrypted_ab1c605bcedb_key -iv $encrypted_ab1c605bcedb_iv -in deployment/codesigning.asc.enc -out deployment/codesigning.asc -d

    echo "decrypted file"
    head -n3 deployment/codesigning.asc

    gpg2 --fast-import deployment/codesigning.asc
fi