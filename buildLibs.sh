#!/bin/sh

# Run gradle build
./gradlew build

if [ -d "pumpX2Lib" ] 
then
    echo "Directory pumpX2Lib exists, using." 
else
    mkdir pumpX2Lib
fi

suffix=$1

cd pumpX2Lib
rm *
cp ../lib/build/outputs/aar/*.aar com.jwoglom.pumpx2.core${suffix}.aar
cp ../messages/build/libs/messages.jar com.jwoglom.pumpx2.messages${suffix}.jar
cp ../shared/build/libs/shared.jar com.jwoglom.pumpx2.shared${suffix}.jar

cd ..


# If you want to copy this file locally into some directory create copyLibs.sh shell file in same directory
if [ -x "copyLibs.sh" ] 
then
    echo "Copying files to your local directory" 
    ./copyLibs.sh
fi 
