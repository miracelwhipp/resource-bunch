#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then

    openssl aes-256-cbc -K $encrypted_9aa72e404c2e_key -iv $encrypted_9aa72e404c2e_iv -in deployment/codesigning.asc.enc -out deployment/codesigning.asc -d
    gpg --version
    gpg2 --version
    gpg --fast-import deployment/codesigning.asc
fi