#!/usr/bin/env bash
./gradlew assemble

curl -F "ipa=@app/app-qa-release.apk" -F "status=2" -H "X-HockeyAppToken:efd7e714037146c592c49c5dd97f23a7" https://rink.hockeyapp.net/api/2/apps/upload


