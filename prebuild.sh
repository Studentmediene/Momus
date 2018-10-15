#!/bin/sh

# clean the project
rm -rf src/main/resources/local.properties
rm -rf src/main/webapp/dist
rm -rf src/main/webapp/app/libs
rm -rf node_modules
rm -rf target


# copy in production values
cp /srv/momus/local.properties src/main/resources/local.properties
cp /srv/momus/Momuslive-6b43d0b81c8e.p12 src/main/resources/googlekey.p12
cp /srv/momus/spcert.pem src/main/resources/spcert.pem
cp /srv/momus/spkey.der src/main/resources/spkey.der

# install frontend dependencies
npm install
bower install

# build frontent
grunt build