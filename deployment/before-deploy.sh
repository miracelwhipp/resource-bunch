#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then

    openssl aes-256-cbc -K $encrypted_9aa72e404c2e_key -iv $encrypted_9aa72e404c2e_iv -in deployment/codesigning.asc.enc -out deployment/codesigning.asc -d

    echo "decrypted file"

    gpg2 --fast-import deployment/codesigning.asc
fi