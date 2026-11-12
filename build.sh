#!/bin/bash
# Build Video SRT Tool APK from decompiled sources.
# Usage: ./build.sh <versionCode> <versionName>
set -e
cd /root/.buddy/work/apk-analysis
VC="${1:?versionCode}"
VN="${2:?versionName}"

AJ=/root/android-sdk/android-36/android.jar
JARS=$(ls libs3/*.jar | tr '\n' ' ')
CP="$AJ:$(echo "$JARS" | tr ' ' ':')"

echo "== javac =="
rm -rf classes
mkdir -p classes
javac --release 8 -nowarn -encoding UTF-8 -cp "$CP" -d classes @files.txt 2>&1 | grep -v "^Note:" || true

echo "== classlist =="
find classes -name '*.class' | sort > classlist.txt
wc -l classlist.txt

echo "== d8 =="
rm -rf dexout
mkdir -p dexout
d8 --release --min-api 24 --lib "$AJ" --output dexout @classlist.txt $JARS 2>&1 | tail -5
ls -l dexout

echo "== repackage =="
sed -i "s/^NEW_VERSION_CODE = .*/NEW_VERSION_CODE = $VC/; s/^NEW_VERSION_NAME = .*/NEW_VERSION_NAME = '$VN'/" repackage.py
python3 repackage.py
zipalign -f -p 4 unsigned.apk aligned.apk
apksigner sign --ks bkggds.keystore --ks-pass pass:bkggds123 --key-pass pass:bkggds123 \
  --ks-key-alias bkggds --v1-signing-enabled true --v2-signing-enabled true \
  --min-sdk-version 24 --out "Video_SRT_Tool_$VN.apk" aligned.apk
apksigner verify "Video_SRT_Tool_$VN.apk"
ls -l "Video_SRT_Tool_$VN.apk"
