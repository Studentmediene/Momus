#!/bin/sh
cp /srv/momus/local.properties local.properties
npm install
bower install
grunt build