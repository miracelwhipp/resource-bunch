#!/usr/bin/env bash

echo "$TRAVIS_BRANCH"
echo "$TRAVIS_PULL_REQUEST"

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_9aa72e404c2e_key -iv $encrypted_9aa72e404c2e_iv -in deployment/codesigning.asc.enc -out deployment/codesigning.asc -d
    gpg --fast-import deployment/codesigning.asc
fi