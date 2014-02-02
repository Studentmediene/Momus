/*
 * Copyright 2013 Studentmediene i Trondheim AS
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
    .controller('SourceSearchCtrl', function ($scope, $location) {
        $scope.tags = [];

        $scope.search = {
            name: "",
            tags: [],
            freetext: ""
        };

        updateSearchParametersFromUrl();
        keepUrlAndSearchParametersInSync();

        $scope.sources = [
            {
                name: "Mats Svensson",
                tags: ["kul", "smart", "flink"],
                description: "prosjektleder momus"
            },
            {
                name: "Test Testesen",
                tags: ["flink", "rar", "hårete og feit"],
                description: "student i oslo"
            }
        ];


        $scope.select2Options = {
            'multiple': true,
            'simple_tags': true,
            tags: function () { // wrapped in a function so it sees changes to $scope.tags
                return $scope.tags;
            },
            createSearchChoice: function () {
                return null;
            }, // only use pre-defined tags
            tokenSeparators: [","]
        };




        $scope.tags = ["kul", "smart", "flink", "rar", "hårete og feit"];

        $scope.tagFilter = function (source) {
            for (var i = 0; i < $scope.search.tags.length; i++) {
                if (source.tags.indexOf($scope.search.tags[i]) === -1) {
                    return false;
                }
            }
            return true;
        };

        function updateSearchParametersFromUrl() {
            if ($location.search().name) {
                $scope.search.name = $location.search().name;
            }
            if ($location.search().freetext) {
                $scope.search.freetext = $location.search().freetext;
            }

            if ($location.search().tags) {
                $scope.search.tags = $location.search().tags;
            }

        }

        function keepUrlAndSearchParametersInSync() {
            $scope.$on('$routeUpdate', function(){
                console.log("route");
                updateSearchParametersFromUrl();
            });

            $scope.$watch('search', function (newValue) {
                console.log("params");
                $location.search(newValue).replace();
            }, true);
        }

    });