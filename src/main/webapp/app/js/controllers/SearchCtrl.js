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
    .controller('SearchCtrl', function ($scope, $http, $location, $q, PersonService, PublicationService, ArticleService, $modal) {

        var pageSize = 100;

        $scope.data = [];
        $scope.hasNextPage = false;
        $scope.defaultSearch = {
            free: '',
            status: '',
            persons: [],
            section: '',
            publication: '',
            page_number: 1,
            page_size: pageSize,
            archived: false
        };

        $scope.articleSortReverse = false;
        $scope.articleSortType = "section.id";
        $scope.search = angular.copy($scope.defaultSearch);

        // Get stuff from the server
        $q.all([PersonService.getAll(), PublicationService.getAll()]).then(function (data) {
            $scope.persons = data[0].data;
            $scope.publications = data[1].data;
            if (updateSearchParametersFromUrl()) { // If the URL contained a search
                search();
            } else if ($scope.publications.length > 0) { // default search on the newest publication
                $scope.search.publication = PublicationService.getActive($scope.publications).id;
                $location.search('publication', $scope.search.publication).replace();

                search();
            }
        });

        ArticleService.getSections().success(function (data) {
            $scope.sections = data;
        });

        ArticleService.getStatuses().success(function (data) {
            $scope.statuses = data;
        });


        $scope.$on('$routeUpdate', function () { // when going back/forward
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

            $scope.search = angular.copy($scope.defaultSearch);

            for (var key in $scope.search) {
                var value = urlSearch[key];

                if (value) {
                    $scope.search[key] = value;
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
            var newValues = $scope.search;
            for (var key in newValues) {
                var value = newValues[key];

                $location.search(key, value ? value : null);
            }
        }


        $scope.searchFunc = function (pageDelta) {
            if (pageDelta) {
                $scope.search.page_number = parseInt($scope.search.page_number, 10) + pageDelta; // parse, as suddenly it's a string!
            }

            rememberSearchState();
            search();
        };


        function search() {
            $scope.data = null;
            $scope.loading = true;
            $scope.noArticles = false;

            ArticleService.search($scope.search).success(function (data) {
                $scope.hasNextPage = (data.length > pageSize); // search always returns one too many
                $scope.data = data.slice(0, pageSize);
            }).finally(function () {
                $scope.loading = false;
                if ($scope.data.length <= 0) {
                    $scope.noArticles = true;
                }
            });
        }

        $scope.sortSearch = function(type){
            if($scope.articleSortType != type){
                $scope.articleSortReverse = false;
                $scope.articleSortType = type;
            } else {
                $scope.articleSortReverse = !$scope.articleSortReverse;
            }

            $scope.$apply();
        };

        $scope.createArticle = function(){
            var modal = $modal.open({
                templateUrl: 'partials/article/createArticleModal.html',
                controller: 'CreateArticleModalCtrl'
            });
            modal.result.then(function(id){
                console.log('#/artikler/' + id);
                $location.url('artikler/' + id);
            })
        }
    });