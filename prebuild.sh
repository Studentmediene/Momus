#!/bin/sh

# clean the project
rm -rf src/main/resources/local.properties
rm -rf src/main/webapp/dist
rm -rf src/main/webapp/app/libs
rm -rf node_modules
rm -rf target


# copy in production values
cp /srv/momus/local.properties src/main/resources/local.properties

# install frontend dependencies
npm install
bower install

# build frontent
grunt build