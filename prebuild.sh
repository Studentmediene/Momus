#!/bin/sh
cp /srv/momus/local.properties src/main/resources/local.properties
npm install
bower install
grunt build