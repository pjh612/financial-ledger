#!/usr/bin/env sh
./gradlew clean :bootJar

docker build --force-rm -t ${IMG_NAME:-financial-ledger_pjh_ledger_application} .