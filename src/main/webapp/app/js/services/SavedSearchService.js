'use strict';

angular.module('momusApp.services')
    .service('SavedSearchService', function ($http) {
        return {
            getSavedSearches: function () {
                return $http.get('api/savedsearch/get');
            },
            saveSearch: function (name, url) {
                return $http.put('api/savedsearch/put', name, url);
            }
        };
    });