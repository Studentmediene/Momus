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

        var listOfPersonsContainsID = function(list, id) {
            var i;
            for (i = 0; i < list.length; i++) {
                // Note that this compares the object's integer ID to the string id
                if (list[i].id == id) {
                    return true;
                }
            }
            return false;
        };

        var removeFromArray = function(array, object) {
            var index = array.indexOf(object);
            if(index > -1){
                array.splice(index, 1);
            }
        };

        $scope.addJournalist = function(newJournalistID){
            if (listOfPersonsContainsID($scope.article.journalists, newJournalistID)){
                return;
            }
            $http.get('/api/person/' + newJournalistID).success( function(data) {
                $scope.article.journalists.push(data);
                $http.put('/api/article/' + $scope.article.id + '/photographers/', $scope.article.journalists);
            });
        };

        $scope.removeJournalist = function(journalist) {
            removeFromArray($scope.article.journalists, journalist);
            $http.put('/api/article/' + $scope.article.id + '/photographers/', $scope.article.journalists);
        };

        $scope.addPhotographer = function(newPhotographerID){
            if (listOfPersonsContainsID($scope.article.photographers, newPhotographerID)){
                return;
            }
            $http.get('/api/person/' + newPhotographerID).success( function(data) {
                $scope.article.photographers.push(data);
                $http.put('/api/article/' + $scope.article.id + '/photographers/', $scope.article.photographers);
            });
        };

        $scope.removePhotographer = function(photographer) {
            removeFromArray($scope.article.photographers, photographer);
            $http.put('/api/article/' + $scope.article.id + '/photographers/', $scope.article.photographers);
        };

    });

