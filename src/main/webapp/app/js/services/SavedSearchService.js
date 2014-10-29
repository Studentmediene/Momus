'use strict';

angular.module('momusApp.services')
    .service('SavedSearchService', function ($http, $log) {
        return {
            getSavedSearches: function () {
                return $http.get('/api/savedsearch/get');
            },
            saveSearch: function (name, url) {
                $log.log(url);
                return $http.post('/api/savedsearch/put', {name:name, url:url, owner:0});
            }
        };
    });