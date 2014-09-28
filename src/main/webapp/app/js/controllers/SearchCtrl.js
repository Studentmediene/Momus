/*
 * Copyright 2014 Studentmediene i Trondheim AS
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
    .controller('SearchCtrl', function ($scope, $http, $location, $q, PersonService, PublicationService, ArticleService) {

        $scope.data = [];
        $scope.search = {
            free: '',
            status: '',
            persons: '',
            section: '',
            publication: ''
        };

        // Get stuff from the server
        $q.all([PersonService.getAll(), PublicationService.getAll()]).then(function (data) {
            $scope.persons = data[0].data;
            $scope.publications = data[1].data;

            if (updateSearchParametersFromUrl()) { // If the URL contained a search
                search();
            } else if ($scope.publications.length > 0){ // default search on the newest publication
                $scope.search.publication = $scope.publications[0].id;
                $location.search('publication', $scope.search.publication).replace();
                search();
            }
        });


        $scope.$on('$routeUpdate', function(){ // when going back/forward
            updateSearchParametersFromUrl();

            if ($scope.data) { // if we're not doing a search, trigger one
                search();
            }
        });


        /**
         * Iterates over the $scope.search object and tries to read the
         * corresponding values from the URL, if any is present the function will return true
         */
        function updateSearchParametersFromUrl() {
            var urlSearch = $location.search();
            var aValueWasSet = false;

            for (var key in $scope.search) {
                var value = urlSearch[key];

                $scope.search[key] = value;
                if (value) {
                    aValueWasSet = true;
                }
            }

            if (typeof $scope.search.persons == "string") {
                // convert persons to array, as if it's only one value it will look like a string
                $scope.search.persons = [$scope.search.persons];
            }

            return aValueWasSet;
        }


        function rememberSearchState() {
            var newValue = $scope.search;
            for (var key in newValue) {
                var value = newValue[key];

                if (value) {
                    $location.search(key, value);
                } else {
                    $location.search(key, null);
                }
            }

        }


        $scope.renderPerson = PersonService.renderPerson;

        ArticleService.getTypes().success( function(data){
            $scope.sections = data;
        });

        ArticleService.getStatuses().success( function(data){
            $scope.statuses = data;
        });

        $scope.searchFunc = function () {
            rememberSearchState();
            search();
        };


        function search() {
            $scope.data = null;
            $scope.loading = true;

            ArticleService.search($scope.search).success(function (data) {
                $scope.data = data;
            }).finally(function () {
                $scope.loading = false;
            });
        }
    });