#!/usr/bin/env bash
./gradlew build

curl -F "ipa=@app/build/outputs/apk/app-release-unsigned.apk" -F "status=2" -H "X-HockeyAppToken:98f19bcd0c234bffa360145e691387e1" https://rink.hockeyapp.net/api/2/apps/upload


