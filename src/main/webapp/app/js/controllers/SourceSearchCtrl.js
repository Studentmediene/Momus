/*
 * Copyright 2016 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

angular.module('momusApp.controllers')
    .controller('SourceSearchCtrl', function ($scope, $location, $http) {
        $scope.tags = [];
        $scope.sources = [];
        $scope.hasLoadedSources = false;

        $scope.search = {
            name: "",
            tags: [],
            freetext: ""
        };

        $scope.select2Options = {
            'multiple': true,
            'simple_tags': true,
            tags: function () { // wrapped in a function so it sees changes to $scope.tags
                return $scope.tags;
            },
            createSearchChoice: function () {
                return null; // only use pre-defined tags
            }
        };

        $scope.tagFilter = function (source) { // Removes the sources that don't have all the selected tags
            var tags = source.tags.map(function(e) {return e.tag;});
            for (var i = 0; i < $scope.search.tags.length; i++) {
                if (tags.indexOf($scope.search.tags[i]) === -1) {
                    return false;
                }
            }
            return true;
        };

        getTagsFromServer();
        getSourcesFromServer();

        updateSearchParametersFromUrl();
        keepUrlAndSearchParametersInSync();

        function getSourcesFromServer() {
            $http.get('/api/source').success(function (data) {
                $scope.sources = data;
                $scope.hasLoadedSources = true;
            });
        }

        function getTagsFromServer() {
            $http.get('/api/source/tag').success(function(data) {
                $scope.tags = data.map(function(e) { // map the tag-objects to an array of strings
                    return e.tag;
                });
            });
        }

        function updateSearchParametersFromUrl() {
            var s = $location.search();

            for (var key in s) {
                var value = s[key];

                if (value) {
                    $scope.search[key] = value;
                }
            }

        }

        function keepUrlAndSearchParametersInSync() {
            $scope.$on('$routeUpdate', function(){
                updateSearchParametersFromUrl();
            });

            $scope.$watch('search', function (newValue) {
                for (var key in newValue) {
                    var value = newValue[key];

                    if (value) {
                        $location.search(key, value);
                    } else {
                        $location.search(key, null);
                    }
                }

                $location.replace();

            }, true);
        }

    });