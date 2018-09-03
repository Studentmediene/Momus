#!/bin/sh

# clean the project
rm -rf src/main/resources/local.properties
rm -rf webapp/dist
rm -rf webapp-beta/dist
rm -rf node_modules
rm -rf target


# copy in production values
cp /srv/momus/local.properties src/main/resources/local.properties
cp /srv/momus/Momuslive-6b43d0b81c8e.p12 src/main/resources/googlekey.p12

# install frontend dependencies
npm install

# build frontend
npm run build
npm run build:beta