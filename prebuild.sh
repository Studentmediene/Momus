#!/bin/sh
# copy in production values
cp /srv/momus/local.properties src/main/resources/local.properties

# install frontend dependencies
npm install
bower install

# build frontent
grunt build