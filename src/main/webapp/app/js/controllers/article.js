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
    .controller('ArticleCtrl', function ($scope, $http, $routeParams, articleParserRules, noteParserRules) {

        // The scope of this controller is the entire article view

        $http.get('/api/article/' + $routeParams.id).success(function (data) {
            $scope.article = data;
            $scope.originalContent = angular.copy($scope.article.content);
//            $scope.originalNote = angular.copy($scope.article.note);
        });

        $scope.articleRules = articleParserRules;

        $scope.$watch('article.content', function (newVal, oldVal) {
            $scope.articleIsDirty = !angular.equals($scope.article.content, $scope.originalContent);
        });

        $scope.saveArticle = function () {
            $http.put('/api/article', $scope.article).success(function (data) {
                $scope.article = data;
                $scope.originalContent = angular.copy($scope.article.content);
                $scope.articleIsDirty = false;
            });
        };

        // Metadata editing functionality

        $scope.addJournalist = function(newJournalist){
            $http.get('/api/person/' + newJournalist).success( function(data) {
                $scope.article.journalists.push(data);
                // Sends the whole article object, but the method will only change the server-version of the journalists field
                $http.put('/api/article/journalists/', $scope.article);
            });
        };

        var removeFromArray = function(array, object) {
            var index = array.indexOf(object);
            if(index > -1){
                array.splice(index, 1);
            }
        };

        $scope.removeJournalist = function(journalist) {
            removeFromArray($scope.article.journalists, journalist);
            // Sends the whole article object, but the method will only change the server-version of the journalists field
            $http.put('/api/article/journalists/', $scope.article);
        };

        $scope.addPhotographer = function(newPhotographer){
            $http.get('/api/person/' + newPhotographer).success( function(data) {
                $scope.article.photographers.push(data);
                // Sends the whole article object, but the method will only change the server-version of the photgraphers field
                $http.put('/api/article/photographers/', $scope.article);
            });
        };

        $scope.removePhotographer = function(photographer) {
            removeFromArray($scope.article.photographers, photographer);
            // Sends the whole article object, but the method will only change the server-version of the journalists field
            $http.put('/api/article/photographers/', $scope.article);
        };

    });

